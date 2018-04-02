package mod.crystals.capability;

import mod.crystals.CrystalsMod;
import mod.crystals.seal.SealManager;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = CrystalsMod.MODID)
public class CapabilitySealManager {

    private static final ResourceLocation NAME = new ResourceLocation(CrystalsMod.MODID, "seal_manager");

    @CapabilityInject(SealManager.class)
    public static final Capability<SealManager> CAPABILITY = null;

    @SubscribeEvent
    public static void onCapabilityAttach(AttachCapabilitiesEvent<World> event) {
        SealManager manager = new SealManager(event.getObject());
        event.addCapability(NAME, new ICapabilityProvider() {

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                return capability == CAPABILITY;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                return capability == CAPABILITY ? (T) manager : null;
            }

        });
    }

    public static class Storage implements Capability.IStorage<SealManager> {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<SealManager> capability, SealManager instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<SealManager> capability, SealManager instance, EnumFacing side, NBTBase nbt) {
        }

    }

}
