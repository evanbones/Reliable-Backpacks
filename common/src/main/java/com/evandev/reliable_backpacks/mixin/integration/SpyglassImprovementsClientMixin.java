package com.evandev.reliable_backpacks.mixin.integration;

import com.evandev.reliable_backpacks.platform.Services;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import me.juancarloscp52.spyglass_improvements.client.SpyglassImprovementsClient;
import me.juancarloscp52.spyglass_improvements.mixin.MinecraftClientInvoker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
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
    protected abstract void forceUseSpyglass(LocalPlayer player);

    @Inject(method = "onClientTick", at = @At("HEAD"), remap = false)
    public void onClientTick(Minecraft client, CallbackInfo ci) {
        if (client.player != null &&
                client.gameMode != null && useSpyglass.isDown() &&
                ((MinecraftClientInvoker) client).getItemUseCooldown() == 0 &&
                !client.player.isUsingItem()
        ) {
            ItemStack backpack = Services.PLATFORM.getBackpack(client.player);

            if (backpack.getTag() != null && !backpack.isEmpty() && backpack.hasTag() && backpack.getTag().contains("BlockEntityTag")) {
                CompoundTag tag = backpack.getTagElement("BlockEntityTag");
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
}