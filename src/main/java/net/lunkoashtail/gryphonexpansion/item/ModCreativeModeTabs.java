package net.lunkoashtail.gryphonexpansion.item;

import net.lunkoashtail.gryphonexpansion.GryphonExpansion;
import net.lunkoashtail.gryphonexpansion.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GryphonExpansion.MOD_ID);

    public static final Supplier<CreativeModeTab> BLACK_OPAL_ITEMS_TAB =
            CREATIVE_MODE_TABS.register("gryphon_expansion_items_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.gryphonexpansion.gryphon_expansion_items_tab"))
                    .icon(() -> new ItemStack(ModItems.GRYPHON_LOGO.get()))
                    .displayItems((pParameters, pOutput) -> {
                        // ── Gryphon gear ─────────────────────────────────────────
                        pOutput.accept(ModItems.GRYPHON_LOGO);
                        pOutput.accept(ModItems.GRYPHON_SADDLE);
                        pOutput.accept(ModItems.IRON_GRYPHON_ARMOR);
                        pOutput.accept(ModItems.GOLD_GRYPHON_ARMOR);
                        pOutput.accept(ModItems.DIAMOND_GRYPHON_ARMOR);
                        pOutput.accept(ModItems.NETHERITE_GRYPHON_ARMOR);
                        pOutput.accept(ModItems.GRYPHON_HARNESS);
                        pOutput.accept(ModItems.GRYPHON_SADDLEBAG);
                        // ── Gryphon drops & consumables ───────────────────────────
                        pOutput.accept(ModItems.GRYPHON_FEATHER);
                        pOutput.accept(ModItems.GRYPHON_MILK_BUCKET);
                        pOutput.accept(ModItems.GRIFFIN_HEART);
                        pOutput.accept(ModItems.COOKED_HORSE_MEAT);
                        pOutput.accept(ModItems.RAW_HORSE_MEAT);
                        pOutput.accept(ModItems.BARLEY);
                        pOutput.accept(ModItems.BARLEY_SEEDS);
                        // ── Crafting materials ────────────────────────────────────
                        pOutput.accept(ModItems.CONDITIONED_FLOCKING);
                        pOutput.accept(ModItems.FEATHER_STUFFING_PANEL);
                        pOutput.accept(ModItems.FLOCKED_WOOL_PANEL);
                        pOutput.accept(ModItems.FISH_OIL);
                        pOutput.accept(ModItems.SMOKED_LEATHER);
                        pOutput.accept(ModItems.TREATED_LEATHER);
                        pOutput.accept(ModItems.STIRRUP_HARDWARE);
                        pOutput.accept(ModItems.RIVET);
                        pOutput.accept(ModItems.AETHERIC_CRYSTAL);
                        pOutput.accept(ModItems.STORM_ESSENCE);
                        pOutput.accept(ModItems.WIND_ESSENCE);
                        // ── Gems & raw materials ──────────────────────────────────
                        pOutput.accept(ModItems.AMBER);
                        pOutput.accept(ModItems.AGATE_GEODE);
                        pOutput.accept(ModItems.CINNABAR);
                        // ── Blocks ────────────────────────────────────────────────
                        pOutput.accept(ModBlocks.AMBER_ORE);
                        pOutput.accept(ModBlocks.AMBER_DEEPSLATE_ORE);
                        pOutput.accept(ModBlocks.AGATE_ORE);
                        pOutput.accept(ModBlocks.AGATE_DEEPSLATE_ORE);
                        pOutput.accept(ModBlocks.CINNABAR_ORE);
                        pOutput.accept(ModBlocks.CINNABAR_DEEPSLATE_ORE);
                        pOutput.accept(ModBlocks.BARLEY_STRAW_BALE);
                    }).build());

    //public static final Supplier<CreativeModeTab> BLACK_OPAL_BLOCKS_TAB =
    //        CREATIVE_MODE_TABS.register("black_opal_blocks_tab", () -> CreativeModeTab.builder()
    //                .title(Component.translatable("itemGroup.gryphonexpansion.black_opal_blocks_tab"))
    //                .icon(() -> new ItemStack(ModItems.RAW_BLACK_OPAL.get()))
    //                .icon(() -> new ItemStack(ModBlocks.BLACK_OPAL_BLOCK))
    //                .withTabsBefore(ResourceLocation.fromNamespaceAndPath(GryphonExpansion.MOD_ID, "black_opal_items_tab"))
    //                .displayItems((pParameters, pOutput) -> {
    //                    pOutput.accept(Blocks.ANDESITE);
    //                    pOutput.accept(Blocks.DEEPSLATE_DIAMOND_ORE);
    //                    pOutput.accept(ModBlocks.BLACK_OPAL_BLOCK);
    //                    pOutput.accept(ModBlocks.RAW_BLACK_OPAL_BLOCK);
    //                }).build());



    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}