package com.evandev.reliable_backpacks.mixin;

import com.evandev.reliable_backpacks.common.menus.BackpackMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.violetmoon.quark.api.IQuarkButtonAllowed;

@Mixin(value = BackpackMenu.class, remap = false)
public class QuarkBackpackMenuMixin implements IQuarkButtonAllowed {
}
