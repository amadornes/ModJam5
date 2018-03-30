package mod.crystals.item;

import mod.crystals.api.IResonant;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemTuningFork extends ItemBase {

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        IResonant resonant = IResonant.CAPABILITY.getDefaultInstance();
        if (nbt != null) {
            IResonant.CAPABILITY.readNBT(resonant, null, nbt);
        }
        return new ICapabilityProvider() {
            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                return capability == IResonant.CAPABILITY;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                return capability == IResonant.CAPABILITY ? (T) resonant : null;
            }
        };
    }

}
