package net.lunkoashtail.gryphonexpansion.item;

import net.lunkoashtail.gryphonexpansion.GryphonExpansion;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GryphonExpansion.MOD_ID);

    public static final DeferredItem<Item> BLACK_OPAL = ITEMS.registerSimpleItem("black_opal");

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
            ITEMS.registerItem("gryphon_saddle", Item::new, new Item.Properties());
    public static final DeferredItem<Item> GRYPHON_FEATHER =
            ITEMS.registerItem("gryphon_feather", Item::new, new Item.Properties());
    public static final DeferredItem<Item> GRYPHON_LOGO =
            ITEMS.registerItem("gryphon_logo", Item::new, new Item.Properties());



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}