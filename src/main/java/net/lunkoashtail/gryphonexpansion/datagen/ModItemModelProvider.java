package net.lunkoashtail.gryphonexpansion.datagen;

import net.lunkoashtail.gryphonexpansion.GryphonExpansion;
import net.lunkoashtail.gryphonexpansion.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, GryphonExpansion.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.AMBER.get());
        basicItem(ModItems.AGATE_GEODE.get());
        basicItem(ModItems.CINNABAR.get());
        basicItem(ModItems.COOKED_HORSE_MEAT.get());
        basicItem(ModItems.RAW_HORSE_MEAT.get());
        basicItem(ModItems.GRYPHON_SADDLE.get());
        basicItem(ModItems.GRYPHON_FEATHER.get());
        basicItem(ModItems.GRYPHON_LOGO.get());
    }
}