package mod.crystals.tile;

import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import mod.crystals.CrystalsMod;
import mod.crystals.api.IResonant;
import mod.crystals.api.NatureType;
import mod.crystals.capability.CapabilityCrystalCache;
import mod.crystals.capability.CapabilityLoadedCache;
import mod.crystals.capability.CapabilityRayManager;
import mod.crystals.client.particle.ParticleType;
import mod.crystals.crystal.ILaserSource;
import mod.crystals.crystal.Ray;
import mod.crystals.crystal.RayManager;
import mod.crystals.environment.EnvironmentHandler;
import mod.crystals.util.ResonantUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import static mod.crystals.client.particle.ParticleType.posVelocityColor;

public abstract class TileCrystalBase extends TileEntity implements ILaserSource, ITickable {

    public static final float MAX_DISTANCE = 8;
    public static final Vec3d OFFSET = new Vec3d(0.5, 0.5, 0.5);

    protected IResonant.Default resonant;
    protected Set<Ray> rays = new HashSet<>();

    protected boolean needsJoin = false;

    public TileCrystalBase(IResonant.Default resonant) {
        resonant.addChangeListener(this::onChanged);
        this.resonant = resonant;
    }

    public void doJoin() {
        if (!CapabilityLoadedCache.isLoaded(getWorld(), getPos())) {
            return;
        }

        Chunk chunk = getWorld().getChunkFromBlockCoords(getPos());
        chunk.getCapability(CapabilityCrystalCache.CAPABILITY, null).join(this);

        ResonantUtils.getCrystalsAround(getWorld(), getPos(), MAX_DISTANCE, this).forEach(this::connect);
        needsJoin = false;
    }

    protected void onChanged() {
        IBlockState state = getWorld().getBlockState(pos);
        getWorld().notifyBlockUpdate(pos, state, state, 3);
        markDirty();
    }

    protected void join() {
        needsJoin = true;
    }

    protected void leave() {
        Chunk chunk = getWorld().getChunkFromBlockCoords(getPos());
        chunk.getCapability(CapabilityCrystalCache.CAPABILITY, null).leave(this);
        disconnect();
    }

    protected void connect(TileCrystalBase crystal) {
        float match = ResonantUtils.getMatch(resonant, crystal.resonant);
        if (match < 0.8) return;

        RayManager manager = getWorld().getCapability(CapabilityRayManager.CAPABILITY, null);
        manager.addRay(this, crystal);
    }

    protected void disconnect() {
        RayManager manager = getWorld().getCapability(CapabilityRayManager.CAPABILITY, null);
        manager.removeAll(this);
    }

    @Override
    public void onConnect(ILaserSource other, Ray ray) {
        rays.add(ray);
    }

    @Override
    public void onDisconnect(ILaserSource other, Ray ray) {
        rays.remove(ray);
    }

    @Override
    public boolean shouldIgnore(BlockPos pos) {
        return pos.equals(getPos());
    }

    @Override
    public void update() {
        if (needsJoin) doJoin();
        if (!world.isRemote) return;
        if (rays.isEmpty()) return;
        if (rays.stream().noneMatch(Ray::hasLineOfSight)) return;

        Vec3d pos = getPosition(0, true);
        Color color = new Color(resonant.getColor());
        CrystalsMod.proxy.spawnParticle(world, ParticleType.CIRCLE,
            posVelocityColor(pos.x, pos.y, pos.z, 0, 0, 0,
                color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F));
    }

    public TObjectFloatMap<NatureType> visit() {
        TObjectFloatMap<NatureType> total = new TObjectFloatHashMap<>();

        Queue<Pair<TileCrystalBase, TObjectFloatMap<NatureType>>> queue = new ArrayDeque<>();
        Set<TileCrystalBase> visited = new HashSet<>();
        queue.add(Pair.of(this, resonant.getNatureAmounts()));

        while (!queue.isEmpty()) {
            Pair<TileCrystalBase, TObjectFloatMap<NatureType>> pair = queue.poll();
            pair.getKey().visit(queue, visited, pair.getValue(), total);
        }

        return total;
    }

    protected void visit(Queue<Pair<TileCrystalBase, TObjectFloatMap<NatureType>>> queue, Set<TileCrystalBase> visited,
                         TObjectFloatMap<NatureType> cap, TObjectFloatMap<NatureType> total) {
        addAvailableNatures(cap, total);
        for (Ray ray : rays) {
            if (!ray.hasLineOfSight()) continue;
            ILaserSource other = ray.getEnd();
            if (other instanceof TileCrystalBase && visited.add((TileCrystalBase) other)) {
                TileCrystalBase crystal = (TileCrystalBase) other;
                TObjectFloatMap<NatureType> newCap = new TObjectFloatHashMap<>();
                cap.forEachEntry((type, max) -> {
                    float amt = crystal.resonant.getNatureAmount(type);
                    newCap.put(type, Math.min(amt, max));
                    return true;
                });
                queue.add(Pair.of(crystal, newCap));
            }
        }
    }

    protected void addAvailableNatures(TObjectFloatMap<NatureType> cap, TObjectFloatMap<NatureType> total) {
        TObjectFloatMap<NatureType> worldNatures = EnvironmentHandler.INSTANCE.getNature(world, getPos());
        resonant.getNatureAmounts().forEachEntry((type, amt) -> {
            float max = Math.min(cap.get(type), worldNatures.get(type));
            float a = Math.min(amt, max);
            total.adjustOrPutValue(type, a, a);
            return true;
        });
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
        leave();
        join();
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
        leave();
        join();
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

    @Override
    public void validate() {
        super.validate();
        needsJoin = true;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        leave();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        needsJoin = true;
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        leave();
    }

}
