package net.lunkoashtail.gryphonexpansion.datagen;

import net.lunkoashtail.gryphonexpansion.block.ModBlocks;
import net.lunkoashtail.gryphonexpansion.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.BARLEY_STRAW_BALE.get());

        this.add(ModBlocks.AMBER_ORE.get(),
                block -> createOreDrop(ModBlocks.AMBER_ORE.get(), ModItems.AMBER.get()));
        this.add(ModBlocks.AMBER_DEEPSLATE_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.AMBER_DEEPSLATE_ORE.get(), ModItems.AMBER.get(), 1, 2));
        this.add(ModBlocks.CINNABAR_ORE.get(),
                block -> createOreDrop(ModBlocks.CINNABAR_ORE.get(), ModItems.CINNABAR.get()));
        this.add(ModBlocks.CINNABAR_DEEPSLATE_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.CINNABAR_DEEPSLATE_ORE.get(), ModItems.CINNABAR.get(), 1, 2));
        this.add(ModBlocks.AGATE_ORE.get(),
                block -> createOreDrop(ModBlocks.AGATE_ORE.get(), ModItems.AGATE_GEODE.get()));
        this.add(ModBlocks.AGATE_DEEPSLATE_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.AGATE_DEEPSLATE_ORE.get(), ModItems.AGATE_GEODE.get(), 1, 2));
    }

    protected LootTable.Builder createMultipleOreDrops(Block pBlock, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(pBlock, this.applyExplosionDecay(pBlock,
                LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}