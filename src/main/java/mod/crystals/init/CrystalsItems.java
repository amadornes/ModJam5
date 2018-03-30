package mod.crystals.init;

import mod.crystals.CrystalsMod;
import mod.crystals.item.ItemTuningFork;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = CrystalsMod.MODID)
public class CrystalsItems {

    public static final Item tuning_fork = new ItemTuningFork();

    @SubscribeEvent
    public static void onItemRegistration(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registry.register(tuning_fork.setRegistryName("tuning_fork"));
    }

}
