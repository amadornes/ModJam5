package mod.crystals.network;

import io.netty.buffer.ByteBuf;
import mod.crystals.CrystalsMod;
import mod.crystals.client.particle.ParticleType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.awt.*;

import static mod.crystals.client.particle.ParticleType.posVelocityColor;

public class PacketSealFX implements IMessage {

    public int count;
    public Vec3d cPos;
    public Vec3d mot;
    public Color color;

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        count = pb.readVarInt();
        cPos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        mot = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        color = new Color(buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.writeVarInt(count);
        pb.writeDouble(cPos.x);
        pb.writeDouble(cPos.y);
        pb.writeDouble(cPos.z);
        pb.writeDouble(mot.x);
        pb.writeDouble(mot.y);
        pb.writeDouble(mot.z);
        pb.writeInt(color.getRGB());
    }

    public static PacketSealFX create(int count, Vec3d cPos, Vec3d mot, Color color) {
        PacketSealFX packet = new PacketSealFX();
        packet.count = count;
        packet.cPos = cPos;
        packet.mot = mot;
        packet.color = color;
        return packet;
    }

    public static class Handler implements IMessageHandler<PacketSealFX, IMessage> {

        @Override
        public IMessage onMessage(PacketSealFX message, MessageContext ctx) {
            for (int i = 0; i < message.count; i++) {
                CrystalsMod.proxy.spawnParticle(Minecraft.getMinecraft().world, ParticleType.CIRCLE,
                    posVelocityColor(message.cPos.x, message.cPos.y, message.cPos.z, message.mot.x, message.mot.y, message.mot.z,
                        message.color.getRed() / 255F, message.color.getGreen() / 255F, message.color.getBlue() / 255F));
            }
            return null;
        }

    }

    public static class HandlerServer implements IMessageHandler<PacketSealFX, IMessage> {
        @Override
        public IMessage onMessage(PacketSealFX message, MessageContext ctx) {
            return null;
        }
    }
}
