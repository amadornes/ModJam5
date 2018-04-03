package mod.crystals.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mod.crystals.tile.TileSeal;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.ArrayUtils;

public class PacketSealData implements IMessage {

    public BlockPos pos;
    public byte[] data;

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        pos = pb.readBlockPos();
        data = pb.readByteArray();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.writeBlockPos(pos);
        pb.writeByteArray(data);
    }

    public static PacketSealData create(BlockPos pos, PacketBuffer data) {
        PacketSealData packet = new PacketSealData();
        packet.pos = pos;
        packet.data = ArrayUtils.subarray(data.array(), data.arrayOffset(), data.writerIndex() + data.arrayOffset());
        return packet;
    }

    public static class Handler implements IMessageHandler<PacketSealData, IMessage> {
        @Override
        public IMessage onMessage(PacketSealData message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) throw new IllegalStateException("This packet is SERVER->CLIENT");

            Minecraft mc = Minecraft.getMinecraft();
            World world = mc.world;
            TileEntity te = world.getTileEntity(message.pos);
            if (te instanceof TileSeal) {
                ((TileSeal) te).getSeal().readClientData(new PacketBuffer(Unpooled.wrappedBuffer(message.data)));
            }

            return null;
        }
    }

    public static class HandlerServer implements IMessageHandler<PacketSealData, IMessage> {
        @Override
        public IMessage onMessage(PacketSealData message, MessageContext ctx) {
            return null;
        }
    }

}
