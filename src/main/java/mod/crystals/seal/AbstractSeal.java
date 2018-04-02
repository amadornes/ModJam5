package mod.crystals.seal;

import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISeal;
import mod.crystals.api.seal.ISealInstance;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.function.BiConsumer;

public abstract class AbstractSeal implements ISealInstance {

    protected final TObjectFloatMap<NatureType> energy = new TObjectFloatHashMap<>();
    private final TObjectFloatMap<NatureType> capacity = new TObjectFloatHashMap<>();
    private final TObjectFloatMap<NatureType> consumption = new TObjectFloatHashMap<>();

    protected final ISeal seal;

    public AbstractSeal(ISeal seal) {
        this.seal = seal;
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

    public static AxisAlignedBB getAreaInFront(ISeal seal, float radius, boolean extend){
        EnumFacing face = seal.getFace().getOpposite();
        AxisAlignedBB bounds = new AxisAlignedBB(seal.getPos());
        float depth = extend ? radius * 2 : radius;
        switch (face.getAxis()) {
            case X:
                bounds = bounds.expand(0, -radius, -radius).expand(0, radius, radius)
                        .expand(face.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE ? depth : -depth, 0, 0);
                break;
            case Y:
                bounds = bounds.expand(-radius, 0, -radius).expand(radius, 0, radius)
                        .expand(0, face.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE ? depth : -depth, 0);
                break;
            case Z:
                bounds = bounds.expand(-radius, -radius, 0).expand(radius, radius, 0)
                        .expand(0, 0, face.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE ? depth : -depth);
                break;
        }
        return bounds;
    }

}
