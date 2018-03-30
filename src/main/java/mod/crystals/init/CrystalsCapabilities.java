package mod.crystals.init;

import mod.crystals.api.IResonant;
import mod.crystals.capability.CapabilityResonant;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CrystalsCapabilities {

    public static void register() {
        CapabilityManager.INSTANCE.register(IResonant.class, new CapabilityResonant.Storage(), CapabilityResonant.DefaultImpl::new);
    }

}
