package mod.crystals.init;

import mod.crystals.CrystalsMod;
import mod.crystals.api.NatureType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = CrystalsMod.MODID)
public class CrystalsNatures {

    @SubscribeEvent
    public static void onBlockRegistration(RegistryEvent.Register<NatureType> event) {
        IForgeRegistry<NatureType> registry = event.getRegistry();
        registry.register(new NatureType().setRegistryName("air"));
        registry.register(new NatureType().setRegistryName("water"));
        registry.register(new NatureType().setRegistryName("earth"));
        registry.register(new NatureType().setRegistryName("fire"));
        registry.register(new NatureType().setRegistryName("distorted"));
        registry.register(new NatureType().setRegistryName("void"));
    }

}
