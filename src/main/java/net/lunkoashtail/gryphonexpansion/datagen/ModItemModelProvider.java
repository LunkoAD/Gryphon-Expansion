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
        basicItem(ModItems.IRON_GRYPHON_ARMOR.get());
        basicItem(ModItems.GOLD_GRYPHON_ARMOR.get());
        basicItem(ModItems.DIAMOND_GRYPHON_ARMOR.get());
        basicItem(ModItems.NETHERITE_GRYPHON_ARMOR.get());
        basicItem(ModItems.GRYPHON_HARNESS.get());
        basicItem(ModItems.GRYPHON_SADDLEBAG.get());  // uses gryphon_saddlebags.png
        basicItem(ModItems.GRYPHON_FEATHER.get());
        basicItem(ModItems.GRYPHON_LOGO.get());

        basicItem(ModItems.CONDITIONED_FLOCKING.get());
        basicItem(ModItems.AETHERIC_CRYSTAL.get());
        basicItem(ModItems.FLOCKED_WOOL_PANEL.get());
        basicItem(ModItems.STORM_ESSENCE.get());
        basicItem(ModItems.WIND_ESSENCE.get());
        basicItem(ModItems.TREATED_LEATHER.get());
        basicItem(ModItems.STIRRUP_HARDWARE.get());
        basicItem(ModItems.RIVET.get());
        basicItem(ModItems.GRYPHON_MILK_BUCKET.get());
        basicItem(ModItems.GRIFFIN_HEART.get());
        basicItem(ModItems.FISH_OIL.get());
        basicItem(ModItems.FEATHER_STUFFING_PANEL.get());
        basicItem(ModItems.BARLEY.get());
        basicItem(ModItems.BARLEY_SEEDS.get());
        basicItem(ModItems.SMOKED_LEATHER.get());
    }
}