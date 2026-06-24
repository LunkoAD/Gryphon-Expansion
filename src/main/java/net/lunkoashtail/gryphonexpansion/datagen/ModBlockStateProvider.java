package net.lunkoashtail.gryphonexpansion.datagen;

import net.lunkoashtail.gryphonexpansion.GryphonExpansion;
import net.lunkoashtail.gryphonexpansion.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, GryphonExpansion.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.AGATE_ORE);
        blockWithItem(ModBlocks.AGATE_DEEPSLATE_ORE);
        blockWithItem(ModBlocks.AMBER_ORE);
        blockWithItem(ModBlocks.AMBER_DEEPSLATE_ORE );
        blockWithItem(ModBlocks.CINNABAR_ORE);
        blockWithItem(ModBlocks.CINNABAR_DEEPSLATE_ORE);
        blockWithItem(ModBlocks.BARLEY_STRAW_BALE);


    }

    private void blockWithItem(DeferredBlock<Block> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}