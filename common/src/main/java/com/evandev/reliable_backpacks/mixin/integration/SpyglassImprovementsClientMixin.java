package com.evandev.reliable_backpacks.mixin.integration;

import com.evandev.reliable_backpacks.registry.BPItems;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import me.juancarloscp52.spyglass_improvements.client.SpyglassImprovementsClient;
import me.juancarloscp52.spyglass_improvements.mixin.MinecraftClientInvoker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("spyglass_improvements")
@Mixin(value = SpyglassImprovementsClient.class, remap = false)
public abstract class SpyglassImprovementsClientMixin {

    @Shadow(remap = false)
    public static KeyMapping useSpyglass;

    @Shadow(remap = false)
    private void forceUseSpyglass(LocalPlayer player) {
    }

    @Inject(method = "onClientTick", at = @At("HEAD"), remap = false)
    public void onClientTick(Minecraft client, CallbackInfo ci) {
        if (client.player != null &&
                client.gameMode != null && useSpyglass.isDown() &&
                ((MinecraftClientInvoker) client).getItemUseCooldown() == 0 &&
                !client.player.isUsingItem() &&
                client.player.getItemBySlot(EquipmentSlot.CHEST).is(BPItems.BACKPACK) &&
                client.player.getItemBySlot(EquipmentSlot.CHEST).hasTag() &&
                client.player.getItemBySlot(EquipmentSlot.CHEST).getTag().contains("BlockEntityTag")
        ) {
            CompoundTag tag = client.player.getItemBySlot(EquipmentSlot.CHEST).getTagElement("BlockEntityTag");
            if (tag != null && tag.contains("Items", 9)) {
                NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
                ContainerHelper.loadAllItems(tag, items);
                if (items.stream().anyMatch(o -> o.is(Items.SPYGLASS))) {
                    forceUseSpyglass(client.player);
                }
            }
        }
    }
}