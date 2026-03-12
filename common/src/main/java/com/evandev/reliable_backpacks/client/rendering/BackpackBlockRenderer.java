package com.evandev.reliable_backpacks.client.rendering;

import com.evandev.reliable_backpacks.Constants;
import com.evandev.reliable_backpacks.common.blocks.BackpackBlock;
import com.evandev.reliable_backpacks.common.blocks.BackpackBlockEntity;
import com.evandev.reliable_backpacks.platform.Services;
import com.evandev.reliable_backpacks.registry.BPLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class BackpackBlockRenderer implements BlockEntityRenderer<BackpackBlockEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/entity/backpack.png");
    private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/entity/backpack_overlay.png");
    private static final ResourceLocation BASE_TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/entity/backpack_base.png");
    private final ModelPart base;
    private final ModelPart lid;

    public BackpackBlockRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart backpack = context.bakeLayer(BPLayers.BACKPACK_BLOCK);
        this.base = backpack.getChild("base");
        this.lid = base.getChild("lid");
    }

    @Override
    public void render(BackpackBlockEntity blockEntity, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        boolean isFloating = blockEntity.getBlockState().getValue(BackpackBlock.FLOATING);
        float dir = blockEntity.getBlockState().getValue(BackpackBlock.FACING).toYRot();
        float lidRot = 0;
        float baseRotX = 0;
        float baseRotZ = 0;
        float basePosY = 24;

        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-dir));
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0.0F, isFloating ? -0.8F : -1.0F, 0.0F);

        if (blockEntity.open && blockEntity.openTicks < 10) {
            float t = ((float) blockEntity.openTicks + partialTick);
            lidRot = (float) Math.pow(2, -1 * t) * Mth.sin((t - 0.75F) * 0.7F) + 1;
        } else if (blockEntity.openTicks == 10) {
            lidRot = 1;
        } else if (blockEntity.openTicks > 0) {
            float t = ((float) blockEntity.openTicks - partialTick);
            lidRot = (float) -Math.pow(2, t - 10) * Mth.sin((t - 10.75F) * 0.7F);
        }

        if (blockEntity.placeTicks <= 3 && blockEntity.newlyPlaced) {
            float t = ((float) blockEntity.placeTicks + partialTick) / 4;
            basePosY = t * t * 4 + 20;
        }
        if (blockEntity.placeTicks <= 7 && blockEntity.newlyPlaced) {
            float t = ((float) blockEntity.placeTicks + partialTick) / 8;
            baseRotX = Mth.sin(t * 10) * 0.1F * (1 - t);
            baseRotZ = Mth.cos(t * 10) * 0.1F * (1 - t);
        }

        if (isFloating) {
            float t = blockEntity.floatTicks + partialTick;
            basePosY += Mth.sin((t + 20) * Mth.DEG_TO_RAD * 4) * 0.75F;
            baseRotX += Mth.sin(t * Mth.DEG_TO_RAD * 4) * 0.02F;
            baseRotZ += Mth.cos(t * Mth.DEG_TO_RAD * 4) * 0.02F;
        }

        this.lid.xRot = lidRot * 1.5F;
        this.base.xRot = baseRotX;
        this.base.zRot = baseRotZ;
        this.base.y = basePosY;

        renderBaseLayer(blockEntity, poseStack, buffer, packedLight, packedOverlay);
        if (blockEntity.getColor() != 0) {
            renderColoredLayer(poseStack, buffer, packedLight, packedOverlay);
        }
        poseStack.popPose();
    }

    private void renderBaseLayer(BackpackBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ResourceLocation location = blockEntity.getColor() == 0 ? TEXTURE : BASE_TEXTURE;
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(location));
        this.base.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }

    private void renderColoredLayer(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (Services.PLATFORM.isModLoaded("iris")) {
            irisCompatStuff();
        }

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(OVERLAY_TEXTURE));
        this.base.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }

    private void irisCompatStuff() {
        if (WorldRenderingSettings.INSTANCE.getItemIds() != null) {
            CapturedRenderingState.INSTANCE.setCurrentRenderedItem(WorldRenderingSettings.INSTANCE.getItemIds().applyAsInt(new NamespacedId(BackpackBlockRenderer.OVERLAY_TEXTURE.getNamespace(), BackpackBlockRenderer.OVERLAY_TEXTURE.getPath())));
        }
    }
}