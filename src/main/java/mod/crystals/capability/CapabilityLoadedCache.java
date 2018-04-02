package mod.crystals.capability;

import mod.crystals.CrystalsMod;
import mod.crystals.util.SimpleManager;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = CrystalsMod.MODID)
public class CapabilityLoadedCache {

    private static final ResourceLocation NAME = new ResourceLocation(CrystalsMod.MODID, "loaded_chunk_cache");

    @CapabilityInject(LoadedChunks.class)
    public static final Capability<LoadedChunks> CAPABILITY = null;

    @SubscribeEvent
    public static void onCapabilityAttach(AttachCapabilitiesEvent<World> event) {
        LoadedChunks storage = new LoadedChunks(event.getObject());
        event.addCapability(NAME, new ICapabilityProvider() {

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                return capability == CAPABILITY;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                return capability == CAPABILITY ? (T) storage : null;
            }

        });
    }

    public static class Storage implements Capability.IStorage<LoadedChunks> {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<LoadedChunks> capability, LoadedChunks instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<LoadedChunks> capability, LoadedChunks instance, EnumFacing side, NBTBase nbt) {
        }

    }

    public static class LoadedChunks extends SimpleManager {

        private static Map<ChunkPos, Boolean> loaded = new HashMap<>();

        public LoadedChunks(World world) {
            super(world);
        }

        public boolean isLoaded(BlockPos pos) {
            World world = getWorld();
            if (world == null) return false;
            return loaded.computeIfAbsent(new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4), p -> world.isBlockLoaded(pos));
        }

        public boolean isLoaded(int x, int z) {
            World world = getWorld();
            if (world == null) return false;
            return loaded.computeIfAbsent(new ChunkPos(x, z), p -> world.isBlockLoaded(new BlockPos(x * 16, 0, z * 16)));
        }

        @Override
        protected void update(World world) {
            loaded = new HashMap<>();
        }

    }

    public static boolean isLoaded(World world, BlockPos pos) {
        return world.getCapability(CAPABILITY, null).isLoaded(pos);
    }

    public static boolean isLoaded(World world, int x, int z) {
        return world.getCapability(CAPABILITY, null).isLoaded(x, z);
    }

}
