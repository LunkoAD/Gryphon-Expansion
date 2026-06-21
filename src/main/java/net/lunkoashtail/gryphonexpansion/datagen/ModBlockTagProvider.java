package net.lunkoashtail.gryphonexpansion.datagen;

import net.lunkoashtail.gryphonexpansion.GryphonExpansion;
import net.lunkoashtail.gryphonexpansion.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, GryphonExpansion.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.AGATE_DEEPSLATE_ORE.get())
                .add(ModBlocks.AGATE_ORE.get())
                .add(ModBlocks.AMBER_ORE.get())
                .add(ModBlocks.AMBER_DEEPSLATE_ORE.get())
                .add(ModBlocks.CINNABAR_ORE.get())
                .add(ModBlocks.CINNABAR_DEEPSLATE_ORE.get())
;

        this.tag(BlockTags.NEEDS_IRON_TOOL);
                //.add(ModBlocks.BLACK_OPAL_ORE.get())
                //.add(ModBlocks.BLACK_OPAL_DEEPSLATE_ORE.get());

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL);
                //.add(ModBlocks.BLACK_OPAL_END_ORE.get())
                //.add(ModBlocks.BLACK_OPAL_NETHER_ORE.get());
    }
}