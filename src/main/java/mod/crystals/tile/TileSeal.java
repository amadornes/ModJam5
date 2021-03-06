package mod.crystals.tile;

import io.netty.buffer.Unpooled;
import mod.crystals.CrystalsMod;
import mod.crystals.api.seal.ISeal;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.api.seal.SealType;
import mod.crystals.block.BlockSealExt;
import mod.crystals.capability.CapabilitySealManager;
import mod.crystals.init.CrystalsBlocks;
import mod.crystals.init.CrystalsRegistries;
import mod.crystals.network.PacketSealData;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileSeal extends TileEntity implements ITickable {

    private final Host host = new Host();

    private SealType type;
    private ISealInstance seal;

    private boolean needsSync = false;

    @Override
    public void update() {
        if (seal == null) return;
        seal.update();
        if (needsSync && !world.isRemote) {
            PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
            seal.writeClientData(buf);
            PacketSealData packet = PacketSealData.create(getPos(), buf);
            CrystalsMod.net.sendToDimension(packet, world.provider.getDimension()); // FIXME range check thingy?
        }
        needsSync = false;
        markDirty(); // do we need this? idk
    }

    public void setSeal(SealType type) {
        if (type != this.type || this.seal == null) {
            this.type = type;
            this.seal = type.instantiate(host);
        }
    }

    @Override
    public void validate() {
        super.validate();
        getWorld().getCapability(CapabilitySealManager.CAPABILITY, null).add(this);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        getWorld().getCapability(CapabilitySealManager.CAPABILITY, null).remove(this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        getWorld().getCapability(CapabilitySealManager.CAPABILITY, null).add(this);
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        getWorld().getCapability(CapabilitySealManager.CAPABILITY, null).remove(this);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (type != null) {
            tag.setString("type", type.getRegistryName().toString());
            tag.setTag("data", seal.writeToNBT(new NBTTagCompound()));
        }
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("type")) {
            SealType type = CrystalsRegistries.sealTypeRegistry.getValue(new ResourceLocation(tag.getString("type")));
            setSeal(type);
            seal.readFromNBT(tag.getCompoundTag("data"));
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public Iterable<BlockPos> getSealBox() {
        return BlockSealExt.getSealBounds(getSealType().getSize(), getFace(), getPos());
    }

    // @Nullable
    // @Override
    // public SPacketUpdateTileEntity getUpdatePacket() {
    //     return null;
    // }

    @Override
    @Nonnull
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        if (type != null) {
            tag.setString("type", type.getRegistryName().toString());
            PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
            seal.writeClientData(buf);
            tag.setByteArray("data", PacketSealData.create(getPos(), buf).data);
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
        if (tag.hasKey("type")) {
            SealType type = CrystalsRegistries.sealTypeRegistry.getValue(new ResourceLocation(tag.getString("type")));
            setSeal(type);
            if (tag.hasKey("data"))
                getSeal().readClientData(new PacketBuffer(Unpooled.wrappedBuffer(tag.getByteArray("data"))));
        }
        // getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());
    }

    // @Override
    // public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
    //     handleUpdateTag(pkt.getNbtCompound());
    // }

    @Nullable
    public SealType getSealType() {
        return type;
    }

    public ISealInstance getSeal() {
        return seal;
    }

    public EnumFacing getFace() {
        IBlockState blockState = getWorld().getBlockState(getPos());
        if (blockState.getBlock() != CrystalsBlocks.seal) return EnumFacing.DOWN;
        return blockState.getValue(BlockDirectional.FACING);
    }

    private class Host implements ISeal {

        @Override
        public World getWorld() {
            return TileSeal.this.getWorld();
        }

        @Override
        public BlockPos getPos() {
            return TileSeal.this.getPos();
        }

        @Override
        public EnumFacing getFace() {
            return TileSeal.this.getFace();
        }

        @Override
        public void sync() {
            needsSync = true;
        }

    }

}
