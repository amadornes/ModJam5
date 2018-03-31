package mod.crystals.init;

import mod.crystals.api.IResonant;
import mod.crystals.capability.CapabilityCrystalStorage;
import mod.crystals.capability.CapabilityRayManager;
import mod.crystals.capability.CapabilityResonant;
import mod.crystals.util.ray.RayManager;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CrystalsCapabilities {

    public static void register() {
        CapabilityManager.INSTANCE.register(IResonant.class,
                new CapabilityResonant.Storage(), CapabilityResonant.DefaultImpl::new);
        CapabilityManager.INSTANCE.register(CapabilityCrystalStorage.CrystalStorage.class,
                new CapabilityCrystalStorage.Storage(), CapabilityCrystalStorage.CrystalStorage::new);
        CapabilityManager.INSTANCE.register(RayManager.class,
                new CapabilityRayManager.Storage(), RayManager::new);
    }

}
