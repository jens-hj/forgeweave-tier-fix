package dev.jensh.forgeweavetierfix;

import dev.gkissel.forgeweave.api.modifier.Modifier;
import dev.gkissel.forgeweave.item.ForgeweaveDataComponents;
import dev.gkissel.forgeweave.modifier.ForgeweaveModifiers;
import dev.gkissel.forgeweave.modifier.ModifierApplication;
import dev.gkissel.forgeweave.modifier.ModifierEntry;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

@Mod("forgeweave_tier_fix")
public final class TierFix {
    private static final ResourceLocation DIAMOND = ResourceLocation.fromNamespaceAndPath("forgeweave", "diamond");
    private static final ResourceLocation EMERALD = ResourceLocation.fromNamespaceAndPath("forgeweave", "emerald");

    public TierFix() {
        NeoForge.EVENT_BUS.addListener(TierFix::onEquipmentChange);
    }

    /**
     * Forgeweave's Diamond and Emerald modifiers raise a tool one mining tier up to a cap, but
     * compute min(tier + 1, cap), which lowers any tool already above the cap. Never lower.
     */
    public static int clamp(Modifier modifier, int tier, int result) {
        if (modifier == ForgeweaveModifiers.DIAMOND || modifier == ForgeweaveModifiers.EMERALD) {
            return Math.max(tier, result);
        }
        return result;
    }

    /**
     * The mining tier is baked into the item when a modifier is applied, so tools modified before
     * this fix was installed still carry the lowered tier. Re-bake those when they are held.
     *
     * @return true if the stack was repaired
     */
    public static boolean repair(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Integer base = stack.get(ForgeweaveDataComponents.BASE_TOOL_TIER.get());
        Tool tool = stack.get(DataComponents.TOOL);
        if (base == null || base < 0 || tool == null) return false;
        List<ModifierEntry> entries = ForgeweaveModifiers.of(stack);
        if (entries.stream().noneMatch(e -> e.id().equals(DIAMOND) || e.id().equals(EMERALD))) return false;
        int expected = ForgeweaveModifiers.foldTierIndex(base, entries);
        for (Tool.Rule rule : tool.rules()) {
            if (rule.speed().isPresent()) continue;
            int baked = ForgeweaveModifiers.tierIndexOf(rule.blocks());
            if (baked < 0 || baked == expected) return false;
            ModifierApplication.rebake(stack);
            return true;
        }
        return false;
    }

    private static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof Player && !event.getEntity().level().isClientSide()) {
            repair(event.getTo());
        }
    }
}
