package mod.crystals.init;

import mod.crystals.CrystalsMod;
import mod.crystals.api.seal.SealType;
import mod.crystals.seal.*;
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
        registry.register(new SealThunder().setRegistryName("thunder"));
        registry.register(new SealLocalRain().setRegistryName("local_rain"));
        registry.register(new SealPull().setRegistryName("pull"));
        registry.register(new SealPush().setRegistryName("push"));
    }

}
