package mod.crystals.init;

import mod.crystals.api.IResonant;
import mod.crystals.capability.CapabilityCrystalCache;
import mod.crystals.capability.CapabilityLoadedCache;
import mod.crystals.capability.CapabilityRayManager;
import mod.crystals.capability.CapabilityResonant;
import mod.crystals.capability.CapabilitySealManager;
import mod.crystals.crystal.ChunkCrystalCache;
import mod.crystals.crystal.RayManager;
import mod.crystals.seal.SealManager;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CrystalsCapabilities {

    public static void register() {
        CapabilityManager.INSTANCE.register(IResonant.class,
                new CapabilityResonant.Storage(), CapabilityResonant.DefaultImpl::new);
        CapabilityManager.INSTANCE.register(ChunkCrystalCache.class,
                new CapabilityCrystalCache.Storage(), ChunkCrystalCache::new);
        CapabilityManager.INSTANCE.register(RayManager.class,
                new CapabilityRayManager.Storage(), () -> null);
        CapabilityManager.INSTANCE.register(SealManager.class,
                new CapabilitySealManager.Storage(), () -> null);
        CapabilityManager.INSTANCE.register(CapabilityLoadedCache.LoadedChunks.class,
                new CapabilityLoadedCache.Storage(), () -> null);
    }

}
