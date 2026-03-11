package com.evandev.reliable_backpacks.mixin;

import com.evandev.reliable_backpacks.BackpackWearer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements BackpackWearer {

    @Unique
    private int reliable_backpacks$openCount = 0;

    @Unique
    private int reliable_backpacks$openTicks = 0;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "baseTick", at = @At("HEAD"))
    public void baseTick(CallbackInfo ci) {
        if (this.reliable_backpacks$openCount > 0 && this.reliable_backpacks$openTicks < 10) {
            this.reliable_backpacks$openTicks++;
        }
        if (this.reliable_backpacks$openCount == 0 && this.reliable_backpacks$openTicks > 0) {
            this.reliable_backpacks$openTicks--;
        }
    }

    @Override
    public void onBackpackOpen() {
        this.reliable_backpacks$openCount++;
    }

    @Override
    public void onBackpackClose() {
        this.reliable_backpacks$openCount--;
        if (this.reliable_backpacks$openCount < 0) this.reliable_backpacks$openCount = 0;
    }

    @Override
    public int getOpenCount() {
        return this.reliable_backpacks$openCount;
    }

    @Override
    public int getOpenTicks() {
        return this.reliable_backpacks$openTicks;
    }
}