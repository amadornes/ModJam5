package mod.crystals.api.seal;

import net.minecraft.nbt.NBTTagCompound;

public interface ISealInstance {

    void update();

    default NBTTagCompound writeToNBT(NBTTagCompound tag){
        return tag;
    }

    default void readFromNBT(NBTTagCompound tag){
    }

}
