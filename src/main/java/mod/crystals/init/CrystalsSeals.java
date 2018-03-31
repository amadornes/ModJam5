package mod.crystals.init;

import mod.crystals.CrystalsMod;
import mod.crystals.api.SealType;
import mod.crystals.seal.SealRain;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = CrystalsMod.MODID)
public class CrystalsSeals {

    @SubscribeEvent
    public static void onSealRegistration(RegistryEvent.Register<SealType> event) {
        IForgeRegistry<SealType> registry = event.getRegistry();

        registry.register(new SealRain().setRegistryName("rain"));
    }

}
