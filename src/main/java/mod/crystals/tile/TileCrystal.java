package mod.crystals.tile;

import mod.crystals.api.IResonant;
import mod.crystals.capability.CapabilityCrystalStorage;
import mod.crystals.capability.CapabilityRayManager;
import mod.crystals.capability.CapabilityResonant;
import mod.crystals.util.ILaserSource;
import mod.crystals.util.ResonantUtils;
import mod.crystals.util.ray.RayManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class TileCrystal extends TileEntity implements ILaserSource {

    private static final float MAX_DISTANCE_SQ = 8 * 8;
    private static final Vec3d OFFSET = new Vec3d(0.5, 0.5, 0.5);

    private IResonant.Default resonant = (IResonant.Default) IResonant.CAPABILITY.getDefaultInstance();
    private static CapabilityResonant.Storage serializer = new CapabilityResonant.Storage();

    private boolean ignoreJoin = false;

    public TileCrystal(boolean ignoreJoin) {
        this();
        this.ignoreJoin = ignoreJoin;
    }

    public TileCrystal() {
        resonant.addChangeListener(this::onChanged);
    }

    public void doJoin() {
        ignoreJoin = false;
        join();
    }

    private void onChanged() {
        IBlockState state = getWorld().getBlockState(pos);
        getWorld().notifyBlockUpdate(pos, state, state, 3);
        markDirty();
    }

    private void join() {
        if (ignoreJoin) return;
        if (!getWorld().isBlockLoaded(getPos())) return;

        Chunk chunk = getWorld().getChunkFromBlockCoords(getPos());
        chunk.getCapability(CapabilityCrystalStorage.CAPABILITY, null).join(this);

        Set<TileCrystal> crystals = new HashSet<>();
        ChunkPos chunkPos = new ChunkPos(getPos());
        // TODO: Optimize to only check chunks in the correct range
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (!getWorld().isBlockLoaded(getPos().add(16 * x, 0, 16 * z))) continue;

                Chunk c = getWorld().getChunkFromChunkCoords(chunkPos.x + x, chunkPos.z + z);
                for (TileCrystal crystal : c.getCapability(CapabilityCrystalStorage.CAPABILITY, null).getCrystals()) {
                    if (crystal == this) continue;
                    if (crystal.getPos().distanceSq(getPos()) < MAX_DISTANCE_SQ) {
                        connect(crystal);
                    }
                }
            }
        }
    }

    private void leave() {
        Chunk chunk = getWorld().getChunkFromBlockCoords(getPos());
        chunk.getCapability(CapabilityCrystalStorage.CAPABILITY, null).leave(this);
        disconnect();
    }

    private void connect(TileCrystal crystal) {
        float match = ResonantUtils.getMatch(resonant, crystal.resonant);
        if (match < 0.8) return;

        RayManager manager = getWorld().getCapability(CapabilityRayManager.CAPABILITY, null);
        manager.addRay(this, crystal);
    }

    private void disconnect() {
        RayManager manager = getWorld().getCapability(CapabilityRayManager.CAPABILITY, null);
        manager.removeAll(this);
    }

    @Override
    public Vec3d getPosition(float partialTicks) {
        if (getBlockMetadata() == 0) {
            BlockPos pos = getPos();
            double time = getWorld().getTotalWorldTime() + partialTicks + (pos.getX() ^ pos.getY() ^ pos.getZ());
            float off = (int) (time % 80) / 80F;
            return OFFSET.add(new Vec3d(getPos())).addVector(0, 0.25 + 0.03125 * Math.sin(Math.PI * off * 2), 0);
        } else {
            return OFFSET.add(new Vec3d(getPos()));
        }
    }

    @Override
    public Vec3d getColor(float partialTicks) {
        Color color = new Color(resonant.getColor());
        return new Vec3d(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
    }

    @Override
    public boolean shouldIgnore(BlockPos pos) {
        return pos.equals(getPos());
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
        join();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        leave();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        join();
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        leave();
    }

}
