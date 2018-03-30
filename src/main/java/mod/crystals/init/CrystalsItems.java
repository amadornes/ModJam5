package mod.crystals.init;

import mod.crystals.CrystalsMod;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = CrystalsMod.MODID)
public class CrystalsItems {

    @SubscribeEvent
    public static void onItemRegistration(RegistryEvent.Register<Item> event) {

    }

}
