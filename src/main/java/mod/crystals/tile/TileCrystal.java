package mod.crystals.tile;

import mod.crystals.api.IResonant;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class TileCrystal extends TileEntity {

    // TODO: Save to NBT
    // TODO: Sync
    private IResonant.Default resonant = (IResonant.Default) IResonant.CAPABILITY.getDefaultInstance();

    public TileCrystal() {
        resonant.addChangeListener(this::onChanged);
    }

    private void onChanged() {
        getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());
        markDirty();
    }

    @Override
    public void validate() {
        super.validate();
        //resonant.setResonance(1);
        //resonant.setNatureAmounts(EnvironmentHandler.INSTANCE.getNature(getWorld(), getPos()));
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == IResonant.CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == IResonant.CAPABILITY) return (T) resonant;
        return super.getCapability(capability, facing);
    }

}
