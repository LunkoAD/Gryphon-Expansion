package net.lunkoashtail.gryphonexpansion;

import net.lunkoashtail.gryphonexpansion.entity.GryphonEntity;
import net.lunkoashtail.gryphonexpansion.entity.ModEntities;
import net.lunkoashtail.gryphonexpansion.network.SetGryphonNamePacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Mod-bus (loading phase) event subscribers that must run on both sides.
 *
 * <p>Attribute registration must happen before any entity of that type
 * is created — the mod event bus guarantees this fires during mod load.
 *
 * <p>Packet payload registration is also a mod-bus event; the registrar
 * assigns a protocol version string so client/server version mismatches
 * are detected at connection time.
 */
@EventBusSubscriber(modid = GryphonExpansion.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEvents {

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.GRYPHON.get(), GryphonEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        // "1" is the protocol version; bump if payload shape changes between releases
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                SetGryphonNamePacket.TYPE,
                SetGryphonNamePacket.STREAM_CODEC,
                SetGryphonNamePacket::handle);
    }
}
