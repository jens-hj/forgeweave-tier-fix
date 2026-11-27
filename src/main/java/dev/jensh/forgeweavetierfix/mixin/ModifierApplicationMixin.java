package dev.jensh.forgeweavetierfix.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.gkissel.forgeweave.api.modifier.Modifier;
import dev.gkissel.forgeweave.modifier.ModifierApplication;
import dev.jensh.forgeweavetierfix.TierFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ModifierApplication.class, remap = false)
abstract class ModifierApplicationMixin {
    @WrapOperation(method = "retuneToolTier",
        at = @At(value = "INVOKE", target = "Ldev/gkissel/forgeweave/api/modifier/Modifier;toolTierIndex(II)I"))
    private static int tierfix$neverLower(Modifier modifier, int level, int tier, Operation<Integer> original) {
        return TierFix.clamp(modifier, tier, original.call(modifier, level, tier));
    }
}
