package mod.crystals.seal;

import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import mod.crystals.CrystalsMod;
import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISeal;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.init.CrystalsRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
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
        seal.sync();
    }

    public abstract void addRequirements(BiConsumer<NatureType, Float> capacity, BiConsumer<NatureType, Float> consumption);

    protected boolean consumeEnergy() {
        if (!consumption.forEachEntry((k, v) -> energy.get(k) >= v)) return false;
        if (!seal.getWorld().isRemote) {
            consumption.forEachEntry((k, v) -> {
                energy.adjustValue(k, -v);
                return true;
            });
            seal.sync();
        }
        return true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagCompound energyTag = new NBTTagCompound();
        tag.setTag("energy", energyTag);
        for (NatureType type : energy.keySet()) {
            float v = energy.get(type);
            energyTag.setFloat(type.getRegistryName().toString(), v);
        }
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        NBTTagCompound energyTag = tag.getCompoundTag("energy");
        energy.clear();

        for (String typeid : energyTag.getKeySet()) {
            NatureType type = CrystalsRegistries.natureRegistry.getValue(new ResourceLocation(typeid));
            if (type == null) {
                CrystalsMod.instance.logger.warn("Invalid nature type " + typeid);
                continue;
            }
            energy.put(type, energyTag.getFloat(typeid));
        }
    }

    @Override
    public void writeClientData(PacketBuffer buf) {
        buf.writeVarInt(energy.size());
        for (NatureType type : energy.keySet()) {
            buf.writeString(type.getRegistryName().toString());
            buf.writeFloat(energy.get(type));
        }
    }

    @Override
    public void readClientData(PacketBuffer buf) {
        energy.clear();
        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            String typeid = buf.readString(256);
            float v = buf.readFloat();
            NatureType type = CrystalsRegistries.natureRegistry.getValue(new ResourceLocation(typeid));
            if (type == null) continue;
            energy.put(type, v);
        }
    }

    public static AxisAlignedBB getAreaInFront(ISeal seal, float radius, boolean extend) {
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
