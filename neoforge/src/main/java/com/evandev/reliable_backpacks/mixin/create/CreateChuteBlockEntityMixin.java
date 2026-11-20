package com.evandev.reliable_backpacks.mixin.create;

import com.evandev.reliable_backpacks.common.items.BackpackItem;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = ChuteBlockEntity.class, remap = false)
public abstract class CreateChuteBlockEntityMixin {

    @Redirect(method = "findEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private <T extends Entity> List<T> reliable_backpacks$ignoreFilledBackpacks(Level level, Class<T> entityClass, AABB area) {
        return level.getEntitiesOfClass(entityClass, area, entity -> !(entity instanceof ItemEntity itemEntity && BackpackItem.hasContents(itemEntity.getItem())));
    }
}
