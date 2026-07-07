package com.evandev.reliable_backpacks.mixin.client;

import com.evandev.reliable_backpacks.client.gui.BackpackScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.violetmoon.quark.api.IQuarkButtonAllowed;

@Mixin(value = BackpackScreen.class, remap = false)
public class QuarkBackpackScreenMixin implements IQuarkButtonAllowed {
}
