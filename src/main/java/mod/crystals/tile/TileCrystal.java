package mod.crystals.tile;

import mod.crystals.api.IResonant;
import mod.crystals.capability.CapabilityResonant;
import mod.crystals.client.particle.ParticleTestIGuess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileCrystal extends TileEntity {

    private IResonant.Default resonant = (IResonant.Default) IResonant.CAPABILITY.getDefaultInstance();
    private static CapabilityResonant.Storage serializer = new CapabilityResonant.Storage();

    public TileCrystal() {
        resonant.addChangeListener(this::onChanged);
    }

    private void onChanged() {
        IBlockState state = getWorld().getBlockState(pos);
        getWorld().notifyBlockUpdate(pos, state, state, 3);
        markDirty();
    }

    @Override
    public void validate() {
        super.validate();
        // resonant.setResonance(1);
        // resonant.setNatureAmounts(EnvironmentHandler.INSTANCE.getNature(getWorld(), getPos()));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTBase tag = serializer.writeNBT(IResonant.CAPABILITY, resonant, null);
        compound.setTag("rd", tag);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTBase tag = compound.getTag("rd");
        if (tag != null) serializer.readNBT(IResonant.CAPABILITY, resonant, null, tag);
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

        if (Math.random() > 0.75) {
            float r1 = ((float) Math.random() - 0.5f) * 0.25f;
            float r2 = ((float) Math.random() - 0.5f) * 0.25f;
            float r3 = ((float) Math.random() - 0.5f) * 0.25f;
            float r = (resonant.getColor() >> 16 & 0xFF) / 256f;
            float g = (resonant.getColor() >> 8 & 0xFF) / 256f;
            float b = (resonant.getColor() & 0xFF) / 256f;
            ParticleTestIGuess.spawnParticleAt(world, pos.getX() + 0.5 + r1, pos.getY() + 1 + r2, pos.getZ() + 0.5 + r3, r, g, b);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == IResonant.CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == IResonant.CAPABILITY) return (T) resonant;
        return super.getCapability(capability, facing);
    }

}
