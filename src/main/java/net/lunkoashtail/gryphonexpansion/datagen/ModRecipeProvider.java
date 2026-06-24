package net.lunkoashtail.gryphonexpansion.datagen;

import net.lunkoashtail.gryphonexpansion.block.ModBlocks;
import net.lunkoashtail.gryphonexpansion.item.ModItems;
import net.lunkoashtail.gryphonexpansion.GryphonExpansion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, pRegistries);
    }

    @Override
    protected void buildRecipes(RecipeOutput pRecipeOutput) {
        List<ItemLike> AGATE_SMELTABLES = List.of(ModItems.AGATE_GEODE,
                ModBlocks.AGATE_ORE, ModBlocks.AGATE_DEEPSLATE_ORE);
        List<ItemLike> AMBER_SMELTABLES = List.of(ModItems.AMBER,
                ModBlocks.AMBER_ORE, ModBlocks.AMBER_DEEPSLATE_ORE);
        List<ItemLike> CINNABAR_SMELTABLES = List.of(ModItems.CINNABAR,
                ModBlocks.CINNABAR_ORE, ModBlocks.CINNABAR_DEEPSLATE_ORE);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModBlocks.BARLEY_STRAW_BALE.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.BARLEY.get())
                .unlockedBy("has_barley_straw_bale", has(ModItems.BARLEY.get())).save(pRecipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.BARLEY.get(), 9)
                .requires(ModBlocks.BARLEY_STRAW_BALE.get())
                .unlockedBy("has_barley_straw_bale", has(ModBlocks.BARLEY_STRAW_BALE.get())).save(pRecipeOutput);


        oreSmelting(pRecipeOutput, AMBER_SMELTABLES, RecipeCategory.MISC, ModItems.AMBER.get(), 0.25f, 200, "amber");
        oreBlasting(pRecipeOutput, AGATE_SMELTABLES, RecipeCategory.MISC, ModItems.AGATE_GEODE.get(), 0.25f, 100, "agate_geode");
        oreBlasting(pRecipeOutput, CINNABAR_SMELTABLES, RecipeCategory.MISC, ModItems.CINNABAR.get(), 0.25f, 100, "cinnabar");
    }

    protected static void oreSmelting(RecipeOutput pRecipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(pRecipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(RecipeOutput pRecipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pRecipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput pRecipeOutput, RecipeSerializer<T> pCookingSerializer, AbstractCookingRecipe.Factory<T> factory,
                                                                       List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer, factory).group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pRecipeOutput, GryphonExpansion.MOD_ID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }
}