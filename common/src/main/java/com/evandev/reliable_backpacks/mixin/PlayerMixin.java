package com.evandev.reliable_backpacks.mixin;

import com.evandev.reliable_backpacks.platform.Services;
import com.evandev.reliable_backpacks.registry.BPItems;
import com.evandev.reliable_backpacks.registry.BPSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (player.level().isClientSide()) return;

        for (int i = 0; i < player.getInventory().items.size(); i++) {
            ItemStack stack = player.getInventory().items.get(i);
            if (isNonEmptyBackpack(stack)) {
                player.getInventory().items.set(i, ItemStack.EMPTY);
                handleEjectedBackpack(player, stack);
            }
        }
    }

    @Unique
    private void handleEjectedBackpack(Player player, ItemStack stack) {
        if (Services.PLATFORM.canEquipBackpack(player)) {
            Services.PLATFORM.equipBackpack(player, stack);
            player.level().playSound(null, player.blockPosition(), BPSounds.BACKPACK_EQUIP, SoundSource.PLAYERS, 1.0F, 1.0F);
        } else {
            player.drop(stack, true, false);
        }
    }

    @Unique
    private boolean isNonEmptyBackpack(ItemStack stack) {
        if (!stack.is(BPItems.BACKPACK)) return false;

        boolean hasContainer = stack.hasTag() && stack.getTag().contains("BlockEntityTag");
        if (!hasContainer) return false;

        CompoundTag bet = stack.getTag().getCompound("BlockEntityTag");
        return bet.contains("Items") && !bet.getList("Items", 10).isEmpty();
    }
}