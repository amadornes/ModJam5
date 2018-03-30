package mod.crystals.tile;

import mod.crystals.api.IResonant;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class TileCrystal extends TileEntity {

    private IResonant resonant = IResonant.CAPABILITY.getDefaultInstance();

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