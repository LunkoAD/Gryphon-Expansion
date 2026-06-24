package net.lunkoashtail.gryphonexpansion.item;

import net.lunkoashtail.gryphonexpansion.GryphonExpansion;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GryphonExpansion.MOD_ID);

    public static final DeferredItem<Item> AGATE_GEODE =
            ITEMS.registerItem("agate_geode", Item::new, new Item.Properties());
    public static final DeferredItem<Item> AMBER =
            ITEMS.registerItem("amber", Item::new, new Item.Properties());
    public static final DeferredItem<Item> CINNABAR =
            ITEMS.registerItem("cinnabar", Item::new, new Item.Properties());
    public static final DeferredItem<Item> RAW_HORSE_MEAT =
            ITEMS.registerItem("raw_horse_meat", Item::new, new Item.Properties());
    public static final DeferredItem<Item> COOKED_HORSE_MEAT =
            ITEMS.registerItem("cooked_horse_meat", Item::new, new Item.Properties());
    public static final DeferredItem<Item> GRYPHON_SADDLE =
            ITEMS.registerItem("gryphon_saddle",          Item::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> IRON_GRYPHON_ARMOR =
            ITEMS.registerItem("iron_gryphon_armor",      Item::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> GOLD_GRYPHON_ARMOR =
            ITEMS.registerItem("gold_gryphon_armor",      Item::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> DIAMOND_GRYPHON_ARMOR =
            ITEMS.registerItem("diamond_gryphon_armor",   Item::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> NETHERITE_GRYPHON_ARMOR =
            ITEMS.registerItem("netherite_gryphon_armor", Item::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> GRYPHON_HARNESS =
            ITEMS.registerItem("gryphon_harness",         Item::new, new Item.Properties().stacksTo(1));
    // Registry name is plural to match the existing gryphon_saddlebags.png texture.
    public static final DeferredItem<Item> GRYPHON_SADDLEBAG =
            ITEMS.registerItem("gryphon_saddlebags", Item::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> GRYPHON_FEATHER =
            ITEMS.registerItem("gryphon_feather", Item::new, new Item.Properties());
    public static final DeferredItem<Item> GRYPHON_LOGO =
            ITEMS.registerItem("gryphon_logo", Item::new, new Item.Properties());

    public static final DeferredItem<Item> AETHERIC_CRYSTAL =
            ITEMS.registerItem("aetheric_crystal", Item::new, new Item.Properties());
    public static final DeferredItem<Item> BARLEY =
            ITEMS.registerItem("barley", Item::new, new Item.Properties());
    public static final DeferredItem<Item> BARLEY_SEEDS =
            ITEMS.registerItem("barley_seeds", Item::new, new Item.Properties());
    public static final DeferredItem<Item> CONDITIONED_FLOCKING =
            ITEMS.registerItem("conditioned_flocking", Item::new, new Item.Properties());
    public static final DeferredItem<Item> FEATHER_STUFFING_PANEL =
            ITEMS.registerItem("feather_stuffing_panel", Item::new, new Item.Properties());
    public static final DeferredItem<Item> FISH_OIL =
            ITEMS.registerItem("fish_oil", Item::new, new Item.Properties());
    public static final DeferredItem<Item> FLOCKED_WOOL_PANEL =
            ITEMS.registerItem("flocked_wool_panel", Item::new, new Item.Properties());
    public static final DeferredItem<Item> GRIFFIN_HEART =
            ITEMS.registerItem("griffin_heart", Item::new, new Item.Properties());
    public static final DeferredItem<Item> GRYPHON_MILK_BUCKET =
            ITEMS.registerItem("gryphon_milk_bucket", Item::new, new Item.Properties());
    public static final DeferredItem<Item> RIVET =
            ITEMS.registerItem("rivet", Item::new, new Item.Properties());
    public static final DeferredItem<Item> SMOKED_LEATHER =
            ITEMS.registerItem("smoked_leather", Item::new, new Item.Properties());
    public static final DeferredItem<Item> STIRRUP_HARDWARE =
            ITEMS.registerItem("stirrup_hardware", Item::new, new Item.Properties());
    public static final DeferredItem<Item> STORM_ESSENCE =
            ITEMS.registerItem("storm_essence", Item::new, new Item.Properties());
    public static final DeferredItem<Item> TREATED_LEATHER =
            ITEMS.registerItem("treated_leather", Item::new, new Item.Properties());
    public static final DeferredItem<Item> WIND_ESSENCE =
            ITEMS.registerItem("wind_essence", Item::new, new Item.Properties());



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}