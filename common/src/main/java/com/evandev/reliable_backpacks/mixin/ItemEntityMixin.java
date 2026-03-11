package com.evandev.reliable_backpacks.mixin;

import com.evandev.reliable_backpacks.common.blocks.BackpackBlockEntity;
import com.evandev.reliable_backpacks.registry.BPBlocks;
import com.evandev.reliable_backpacks.registry.BPItems;
import com.evandev.reliable_backpacks.registry.BPSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static com.evandev.reliable_backpacks.common.blocks.BackpackBlock.*;

@Mixin(value = ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements TraceableEntity {

    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        ItemStack itemStack = ((ItemEntity) (Object) this).getItem();
        boolean hasContainer = itemStack.has(DataComponents.CONTAINER);
        boolean isEmpty = Objects.equals(itemStack.get(DataComponents.CONTAINER), ItemContainerContents.EMPTY);

        if (itemStack.is(BPItems.BACKPACK) && hasContainer && !isEmpty) {
            if (((ItemEntity) (Object) this).getAge() > 0) {
                ((ItemEntity) (Object) this).setExtendedLifetime();
            }

            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, 1.0, 0.9));
            if (this.isInWater() || this.isInLava()) {
                this.getDeltaMovement().add(0.0, 20.0, 0.0);
            }

            Level level = level();
            BlockPos pos = this.getOnPos();
            boolean isUnobstructed = level.getBlockState(pos.above()).canBeReplaced() &&
                    (!level.getFluidState(pos.above()).isSource() || !level.getBlockState(pos.above(2)).canBeReplaced());

            if ((!level.getBlockState(pos).is(BlockTags.REPLACEABLE) || level.getFluidState(pos).isSource()) && isUnobstructed) {

                BlockState state = BPBlocks.BACKPACK.defaultBlockState()
                        .setValue(FACING, getDirection())
                        .setValue(FLOATING, level.getFluidState(pos).isSource() && !level.getFluidState(pos.above()).isSource())
                        .setValue(WATERLOGGED, level.getFluidState(pos.above()).getType() == Fluids.WATER);

                BlockEntity blockEntity = new BackpackBlockEntity(pos.above(), state);
                blockEntity.applyComponentsFromItemStack(itemStack);

                if (!level.isClientSide) {
                    level.setBlockAndUpdate(pos.above(), state);
                    level.setBlockEntity(blockEntity);
                    level.playSound(null, pos.above(), BPSounds.BACKPACK_PLACE.value(), SoundSource.BLOCKS);
                }

                this.discard();
            }
        }
    }
}
