package net.lunkoashtail.gryphonexpansion.network;

import net.lunkoashtail.gryphonexpansion.GryphonExpansion;
import net.lunkoashtail.gryphonexpansion.entity.GryphonEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * C→S packet: the owner has edited the Gryphon's name in the GUI.
 *
 * <p>Sent every keystroke from {@code GryphonScreen}'s {@code EditBox} responder.
 * The server validates ownership before applying the name change.
 *
 * <p>Registration: call {@link #register} inside a
 * {@code RegisterPayloadHandlersEvent} subscriber on the mod event bus.
 */
public record SetGryphonNamePacket(int entityId, String name)
        implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SetGryphonNamePacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(GryphonExpansion.MOD_ID, "set_gryphon_name"));

    /**
     * StreamCodec serializes the packet to/from the byte buffer.
     * Order must match: entityId first, name second.
     */
    public static final StreamCodec<FriendlyByteBuf, SetGryphonNamePacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT,        SetGryphonNamePacket::entityId,
                    ByteBufCodecs.STRING_UTF8, SetGryphonNamePacket::name,
                    SetGryphonNamePacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * Executed on the server's main thread (via {@code enqueueWork}).
     * Validates that the sender owns the target Gryphon before renaming it.
     */
    public static void handle(SetGryphonNamePacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer serverPlayer)) return;

            Entity entity = serverPlayer.serverLevel().getEntity(packet.entityId());
            if (!(entity instanceof GryphonEntity gryphon)) return;
            if (!gryphon.isOwnedBy(serverPlayer)) return;

            if (packet.name().isBlank()) {
                gryphon.setCustomName(null);
                gryphon.setCustomNameVisible(false);
            } else {
                gryphon.setCustomName(Component.literal(packet.name().strip()));
                gryphon.setCustomNameVisible(true);
            }
        });
    }
}
