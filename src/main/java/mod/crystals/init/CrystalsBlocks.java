package mod.crystals.init;

import mod.crystals.CrystalsMod;
import mod.crystals.block.BlockCrystal;
import mod.crystals.tile.TileCrystal;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = CrystalsMod.MODID)
public class CrystalsBlocks {

    public static final Block crystal = new BlockCrystal();

    @SubscribeEvent
    public static void onBlockRegistration(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        registry.register(crystal.setRegistryName("crystal"));
        GameRegistry.registerTileEntity(TileCrystal.class, CrystalsMod.MODID + ":crystal");
    }

    @SubscribeEvent
    public static void onItemRegistration(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registry.register(new ItemBlock(crystal).setRegistryName("crystal"));
    }

}
