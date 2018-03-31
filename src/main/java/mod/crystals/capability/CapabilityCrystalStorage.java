package mod.crystals.capability;

import mod.crystals.CrystalsMod;
import mod.crystals.tile.TileCrystal;
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
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = CrystalsMod.MODID)
public class CapabilityCrystalStorage {

    private static final ResourceLocation NAME = new ResourceLocation(CrystalsMod.MODID, "crystal_storage");

    @CapabilityInject(CrystalStorage.class)
    public static final Capability<CrystalStorage> CAPABILITY = null;

    @SubscribeEvent
    public static void onCapabilityAttach(AttachCapabilitiesEvent<Chunk> event) {
        CrystalStorage storage = new CrystalStorage();
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

    public static class Storage implements Capability.IStorage<CrystalStorage> {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<CrystalStorage> capability, CrystalStorage instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<CrystalStorage> capability, CrystalStorage instance, EnumFacing side, NBTBase nbt) {
        }

    }

    public static class CrystalStorage {

        private final Set<TileCrystal> crystals = new HashSet<>();

        public void join(TileCrystal crystal) {
            crystals.add(crystal);
        }

        public void leave(TileCrystal crystal) {
            crystals.remove(crystal);
        }

        public Set<TileCrystal> getCrystals() {
            return crystals;
        }

    }

}
