package mod.crystals.tile;

import mod.crystals.api.seal.ISeal;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.api.seal.SealType;
import mod.crystals.init.CrystalsRegistries;
import net.minecraft.block.BlockDirectional;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
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

    @Override
    public void update() {
        if (seal == null) return;
        seal.update();
    }

    private void setSeal(SealType type) {
        this.type = type;
        this.seal = type.instantiate(host);
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

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    @Nonnull
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
        readFromNBT(tag);
        getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
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
            return getWorld().getBlockState(getPos()).getValue(BlockDirectional.FACING);
        }

    }

}
