package dev.jensh.forgeweavetierfix.test;

import dev.gkissel.forgeweave.item.ForgeweaveDataComponents;
import dev.gkissel.forgeweave.modifier.ForgeweaveModifiers;
import dev.gkissel.forgeweave.modifier.ModifierApplication;
import dev.gkissel.forgeweave.modifier.ModifierEntry;
import dev.jensh.forgeweavetierfix.TierFix;
import java.nio.file.Files;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@Mod("tierfix_test")
public final class TierIntegration {
    private static final int STONE = 1, IRON = 2, DIAMOND = 3, NETHERITE = 4, RESONITE = 7;
    private static final ModifierEntry DIAMOND_MOD = entry("diamond");
    private static final ModifierEntry EMERALD_MOD = entry("emerald");

    public TierIntegration() { NeoForge.EVENT_BUS.addListener(this::run); }

    private static ModifierEntry entry(String id) {
        return new ModifierEntry(ResourceLocation.fromNamespaceAndPath("forgeweave", id), 1);
    }
    private static void check(String what, int expected, int actual) {
        if (expected != actual) throw new AssertionError(what + ": expected " + expected + " but was " + actual);
        System.out.println("TIERFIX ok " + what + " = " + actual);
    }
    private static int fold(int base, ModifierEntry... entries) {
        return ForgeweaveModifiers.foldTierIndex(base, List.of(entries));
    }
    private static ItemStack tool(Integer base, int bakedTier, ModifierEntry... entries) {
        ItemStack stack = new ItemStack(Items.STICK);
        if (base != null) stack.set(ForgeweaveDataComponents.BASE_TOOL_TIER.get(), base);
        stack.set(ForgeweaveDataComponents.MODIFIERS.get(), List.of(entries));
        stack.set(DataComponents.TOOL, new Tool(List.of(Tool.Rule.deniesDrops(ForgeweaveModifiers.tierTag(bakedTier))), 1.0f, 1));
        return stack;
    }
    private static int bakedTier(ItemStack stack) {
        return ForgeweaveModifiers.tierIndexOf(stack.get(DataComponents.TOOL).rules().get(0).blocks());
    }

    private void run(ServerStartedEvent event) {
        var server = event.getServer();
        try {
            check("tier count", 8, ForgeweaveModifiers.tierCount());
            // Higher tiers are never lowered.
            check("diamond on resonite", RESONITE, fold(RESONITE, DIAMOND_MOD));
            check("diamond on netherite", NETHERITE, fold(NETHERITE, DIAMOND_MOD));
            check("emerald on resonite", RESONITE, fold(RESONITE, EMERALD_MOD));
            check("emerald on diamond", DIAMOND, fold(DIAMOND, EMERALD_MOD));
            check("both on netherite", NETHERITE, fold(NETHERITE, DIAMOND_MOD, EMERALD_MOD));
            // The documented upgrade and its cap are unchanged.
            check("diamond on stone", IRON, fold(STONE, DIAMOND_MOD));
            check("diamond on iron", DIAMOND, fold(IRON, DIAMOND_MOD));
            check("diamond on diamond", DIAMOND, fold(DIAMOND, DIAMOND_MOD));
            check("emerald on stone", IRON, fold(STONE, EMERALD_MOD));
            check("emerald on iron", IRON, fold(IRON, EMERALD_MOD));
            check("both on stone", DIAMOND, fold(STONE, DIAMOND_MOD, EMERALD_MOD));

            // A tool baked by the unfixed mod is repaired once, then left alone.
            ItemStack broken = tool(RESONITE, DIAMOND, DIAMOND_MOD);
            if (!TierFix.repair(broken)) throw new AssertionError("downgraded tool was not repaired");
            check("repaired tool tier", RESONITE, bakedTier(broken));
            if (TierFix.repair(broken)) throw new AssertionError("healthy tool was repaired again");
            ItemStack plain = tool(RESONITE, DIAMOND);
            if (TierFix.repair(plain)) throw new AssertionError("tool without tier modifiers was touched");
            check("untouched tool tier", DIAMOND, bakedTier(plain));

            // Tools without a recorded base tier go through the per-modifier path.
            ItemStack legacy = tool(null, RESONITE, DIAMOND_MOD);
            ModifierApplication.rebake(legacy);
            check("legacy rebake on resonite", RESONITE, bakedTier(legacy));
            ItemStack legacyIron = tool(null, IRON, DIAMOND_MOD);
            ModifierApplication.rebake(legacyIron);
            check("legacy rebake on iron", DIAMOND, bakedTier(legacyIron));

            Files.writeString(server.getWorldPath(LevelResource.ROOT).resolve("../tierfix-test-passed.txt").normalize(),
                "PASS: tier folding, repair and legacy rebake\n");
        } catch (Throwable failure) {
            failure.printStackTrace();
            throw new AssertionError("Tier integration test failed", failure);
        } finally { server.halt(false); }
    }
}
