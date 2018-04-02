package mod.crystals.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;

public class TileSealExt extends TileEntity {

    private Vec3i offset = BlockPos.ORIGIN;

    public void setOffset(Vec3i offset) {
        this.offset = offset;
        markDirty();
    }

    public Vec3i getOffset() {
        return offset;
    }

    @Override
    @Nonnull
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
        readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("oX", offset.getX());
        compound.setInteger("oY", offset.getY());
        compound.setInteger("oZ", offset.getZ());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        offset = new Vec3i(
            compound.getInteger("oX"),
            compound.getInteger("oY"),
            compound.getInteger("oZ")
        );
    }

}
