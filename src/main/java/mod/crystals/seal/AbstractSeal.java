package mod.crystals.seal;

import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISealInstance;

import java.util.function.BiConsumer;

public abstract class AbstractSeal implements ISealInstance {

    protected final TObjectFloatMap<NatureType> energy = new TObjectFloatHashMap<>();

    private final TObjectFloatMap<NatureType> capacity = new TObjectFloatHashMap<>();
    private final TObjectFloatMap<NatureType> consumption = new TObjectFloatHashMap<>();

    public AbstractSeal() {
        addRequirements(capacity::put, consumption::put);
    }

    @Override
    public float getAccepted(NatureType type) {
        if (!capacity.containsKey(type)) return 0;
        return capacity.get(type) - energy.get(type);
    }

    @Override
    public void addNature(NatureType type, float amount) {
        energy.adjustOrPutValue(type, amount, amount);
    }

    public abstract void addRequirements(BiConsumer<NatureType, Float> capacity, BiConsumer<NatureType, Float> consumption);

    protected boolean consumeEnergy() {
        if (!consumption.forEachEntry((k, v) -> energy.get(k) > v)) return false;
        consumption.forEachEntry((k, v) -> {
            energy.adjustValue(k, -v);
            return true;
        });
        return true;
    }

    // TODO?
    // @Override
    // public NBTTagCompound writeToNBT(NBTTagCompound tag) {
    //     return tag;
    // }
    //
    // @Override
    // public void readFromNBT(NBTTagCompound tag) {
    //
    // }

}
