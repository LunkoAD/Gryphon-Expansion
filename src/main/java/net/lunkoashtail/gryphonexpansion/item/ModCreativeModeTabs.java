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
                        pOutput.accept(ModItems.GRYPHON_SADDLE);
                        pOutput.accept(ModItems.COOKED_HORSE_MEAT);
                        pOutput.accept(ModItems.RAW_HORSE_MEAT);
                        pOutput.accept(ModItems.GRYPHON_FEATHER);


                        pOutput.accept(ModBlocks.AGATE_ORE);
                        pOutput.accept(ModBlocks.AGATE_DEEPSLATE_ORE);
                        pOutput.accept(ModBlocks.AMBER_ORE);
                        pOutput.accept(ModBlocks.AMBER_DEEPSLATE_ORE);
                        pOutput.accept(ModBlocks.CINNABAR_ORE);
                        pOutput.accept(ModBlocks.CINNABAR_DEEPSLATE_ORE);

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