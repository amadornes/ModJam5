package mod.crystals.api.seal;

import mod.crystals.api.NatureType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public interface ISealInstance {

    default float getAccepted(NatureType type) {
        return 0;
    }

    default void addNature(NatureType type, float amount) {
    }

    void update();

    default NBTTagCompound writeToNBT(NBTTagCompound tag) {
        return tag;
    }

    default void readFromNBT(NBTTagCompound tag) {
    }

    default void writeClientData(PacketBuffer buf) {}

    default void readClientData(PacketBuffer buf) {}

}
