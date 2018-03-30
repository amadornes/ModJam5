package mod.crystals.init;

import mod.crystals.CrystalsMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = CrystalsMod.MODID)
public class CrystalsBlocks {

    @SubscribeEvent
    public static void onBlockRegistration(RegistryEvent.Register<Block> event) {

    }

    @SubscribeEvent
    public static void onItemRegistration(RegistryEvent.Register<Item> event) {

    }

}
