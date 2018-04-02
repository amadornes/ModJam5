package mod.crystals.capability;

import mod.crystals.CrystalsMod;
import mod.crystals.crystal.ChunkCrystalCache;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = CrystalsMod.MODID)
public class CapabilityCrystalCache {

    private static final ResourceLocation NAME = new ResourceLocation(CrystalsMod.MODID, "crystal_cache");

    @CapabilityInject(ChunkCrystalCache.class)
    public static final Capability<ChunkCrystalCache> CAPABILITY = null;

    @SubscribeEvent
    public static void onCapabilityAttach(AttachCapabilitiesEvent<Chunk> event) {
        ChunkCrystalCache storage = new ChunkCrystalCache();
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

    public static class Storage implements Capability.IStorage<ChunkCrystalCache> {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<ChunkCrystalCache> capability, ChunkCrystalCache instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<ChunkCrystalCache> capability, ChunkCrystalCache instance, EnumFacing side, NBTBase nbt) {
        }

    }

}
