package net.lunkoashtail.gryphonexpansion;

import net.lunkoashtail.gryphonexpansion.block.ModBlocks;
import net.lunkoashtail.gryphonexpansion.entity.ModEntities;
import net.lunkoashtail.gryphonexpansion.menu.ModMenus;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.lunkoashtail.gryphonexpansion.item.ModCreativeModeTabs;
import net.lunkoashtail.gryphonexpansion.item.ModItems;
import net.minecraft.world.item.CreativeModeTabs;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(GryphonExpansion.MOD_ID)
public class GryphonExpansion {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "gryphonexpansion";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public GryphonExpansion(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ModCreativeModeTabs.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        ModMenus.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.GRYPHON_FEATHER);
            event.accept(ModItems.GRYPHON_LOGO);
            event.accept(ModItems.AMBER);
            event.accept(ModItems.AGATE_GEODE);
            event.accept(ModItems.CINNABAR);
            event.accept(ModItems.CONDITIONED_FLOCKING);
            event.accept(ModItems.FEATHER_STUFFING_PANEL);
            event.accept(ModItems.FISH_OIL);
            event.accept(ModItems.STIRRUP_HARDWARE);
            event.accept(ModItems.SMOKED_LEATHER);
            event.accept(ModItems.STORM_ESSENCE);
            event.accept(ModItems.AETHERIC_CRYSTAL);
            event.accept(ModItems.GRIFFIN_HEART);
            event.accept(ModItems.TREATED_LEATHER);
            event.accept(ModItems.RIVET);
            event.accept(ModItems.FLOCKED_WOOL_PANEL);
            event.accept(ModItems.WIND_ESSENCE);
        }
        if(event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(ModItems.COOKED_HORSE_MEAT);
            event.accept(ModItems.RAW_HORSE_MEAT);
            event.accept(ModItems.BARLEY);
            event.accept(ModItems.BARLEY_SEEDS);
            event.accept(ModItems.GRYPHON_MILK_BUCKET);
        }
        if(event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.GRYPHON_SADDLE);
            event.accept(ModItems.IRON_GRYPHON_ARMOR);
            event.accept(ModItems.GOLD_GRYPHON_ARMOR);
            event.accept(ModItems.DIAMOND_GRYPHON_ARMOR);
            event.accept(ModItems.NETHERITE_GRYPHON_ARMOR);
            event.accept(ModItems.GRYPHON_HARNESS);
            event.accept(ModItems.GRYPHON_SADDLEBAG);
        }
        if(event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(ModBlocks.AGATE_ORE);
            event.accept(ModBlocks.AGATE_DEEPSLATE_ORE);
            event.accept(ModBlocks.AMBER_ORE);
            event.accept(ModBlocks.AMBER_DEEPSLATE_ORE);
            event.accept(ModBlocks.CINNABAR_ORE);
            event.accept(ModBlocks.CINNABAR_DEEPSLATE_ORE);
            event.accept(ModBlocks.BARLEY_STRAW_BALE);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = GryphonExpansion.MOD_ID, value = Dist.CLIENT)
    static class ClientEvents {
        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
