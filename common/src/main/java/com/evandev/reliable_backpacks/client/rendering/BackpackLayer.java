package com.evandev.reliable_backpacks.client.rendering;

import com.evandev.reliable_backpacks.Constants;
import com.evandev.reliable_backpacks.platform.Services;
import com.evandev.reliable_backpacks.registry.BPDataAttatchments;
import com.evandev.reliable_backpacks.registry.BPItems;
import com.evandev.reliable_backpacks.registry.BPLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.model.ParentType;
import org.jetbrains.annotations.NotNull;
import tech.thatgravyboat.vanity.common.item.DesignHelper;

import java.util.Objects;

public class BackpackLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/model/backpack.png");
    private static final ResourceLocation BASE_TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/model/backpack_base.png");
    private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/model/backpack_overlay.png");
    private final ModelPart backpackModel;
    private final ModelPart otherBackpackModel;
    private final ModelPart parentBody;
    private ModelPart model;

    public BackpackLayer(RenderLayerParent renderer, EntityModelSet entityModelSet) {
        super(renderer);
        this.backpackModel = entityModelSet.bakeLayer(BPLayers.BACKPACK);
        this.otherBackpackModel = entityModelSet.bakeLayer(BPLayers.OTHER_BACKPACK);
        this.parentBody = this.getParentBody();
    }

    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float headYaw, float headPitch) {
        ItemStack itemStack = livingEntity.getItemBySlot(EquipmentSlot.CHEST);

        if (shouldRender(itemStack)) {
            if (Services.PLATFORM.isModLoaded("vanity")) {
                ResourceLocation design = ResourceLocation.tryParse(Objects.requireNonNull(DesignHelper.getStyle(itemStack)));
                if (design == null) {
                    this.model = backpackModel;
                } else {
                    String path = design.toString();
                    if (path.equals("reliable_backpacks:test")) {
                        this.model = otherBackpackModel;
                    } else {
                        this.model = backpackModel;
                    }
                }
            } else {
                this.model = backpackModel;
            }

            if (Services.PLATFORM.isModLoaded("figura")) {
                figuraCompatStuff(poseStack, buffer, packedLight, livingEntity, partialTicks, itemStack);
            } else {
                renderBaseLayer(poseStack, buffer, packedLight, livingEntity, partialTicks, itemStack, true);
            }
        }
    }

    private void figuraCompatStuff(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float partialTicks, ItemStack itemStack) {
        Avatar avatar = AvatarManager.getAvatar(livingEntity);
        if (avatar != null) {
            boolean shouldRender = (avatar.luaRuntime != null && avatar.luaRuntime.vanilla_model.CHESTPLATE.getVisible() != null) ? avatar.luaRuntime.vanilla_model.CHESTPLATE.getVisible() : true;
            boolean render = avatar.pivotPartRender(ParentType.ChestplatePivot, (stack) -> {
                stack.scale(16.0F, 16.0F, 16.0F);
                stack.mulPose(Axis.XP.rotationDegrees(180.0F));
                stack.mulPose(Axis.YP.rotationDegrees(180.0F));
                renderBaseLayer(stack, buffer, packedLight, livingEntity, partialTicks, itemStack, false);
            });
            if (!render && shouldRender) {
                renderBaseLayer(poseStack, buffer, packedLight, livingEntity, partialTicks, itemStack, true);
            }
        } else {
            renderBaseLayer(poseStack, buffer, packedLight, livingEntity, partialTicks, itemStack, true);
        }
    }

    private void renderBaseLayer(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float partialTicks, ItemStack itemStack, boolean copyPose) {
        poseStack.pushPose();
        float lidRot = 0;
        boolean isOpen = BPDataAttatchments.getOpenCount(livingEntity) > 0;
        int openTicks = BPDataAttatchments.getOpenTicks(livingEntity);

        if (isOpen && openTicks < 10) {
            float t = ((float) openTicks + partialTicks);
            lidRot = (float) Math.pow(2, -1 * t) * Mth.sin((t - 0.75F) * 0.5F) + 1;
        } else if (openTicks == 10) {
            lidRot = 1;
        } else if (openTicks > 0) {
            float t = ((float) openTicks - partialTicks);
            lidRot = (float) -Math.pow(2, t - 10) * Mth.sin((t - 10.75F) * 0.5F);
        }

        int color = 0;
        CompoundTag displayTag = itemStack.getTagElement("display");
        if (displayTag != null && displayTag.contains("color", 99)) {
            color = displayTag.getInt("color");
        }
        ResourceLocation texture = color == 0 ? TEXTURE : BASE_TEXTURE;

        this.model.getChild("base").getChild("lid").xRot = lidRot;
        if (copyPose) {
            this.backpackModel.copyFrom(parentBody);
        }
        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(texture), false, itemStack.hasFoil());
        this.model.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        renderColoredLayer(poseStack, buffer, packedLight, itemStack);
        poseStack.popPose();
    }

    private void renderColoredLayer(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ItemStack itemStack) {
        int i = 0;
        CompoundTag displayTag = itemStack.getTagElement("display");
        if (displayTag != null && displayTag.contains("color", 99)) {
            i = displayTag.getInt("color");
        }

        if (i == 0) return;

        float r = (float) (i >> 16 & 255) / 255.0F;
        float g = (float) (i >> 8 & 255) / 255.0F;
        float b = (float) (i & 255) / 255.0F;

        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(OVERLAY_TEXTURE), false, itemStack.hasFoil());
        this.model.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
    }

    public boolean shouldRender(ItemStack stack) {
        return stack.getItem() == BPItems.BACKPACK;
    }

    protected ModelPart getParentBody() {
        return this.getParentModel().body;
    }
}