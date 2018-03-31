package mod.crystals.init;

import mod.crystals.CrystalsMod;
import mod.crystals.block.BlockCrystal;
import mod.crystals.block.BlockPost;
import mod.crystals.tile.TileCrystal;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = CrystalsMod.MODID)
public class CrystalsBlocks {

    public static final Block crystal = new BlockCrystal();
    public static final Block post = new BlockPost();

    @SubscribeEvent
    public static void onBlockRegistration(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        registerBlock(registry, crystal, TileCrystal.class, "crystal");
        registerBlock(registry, post, "post");
    }

    @SubscribeEvent
    public static void onItemRegistration(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registerItem(registry, crystal, "crystal");
        registerItem(registry, post, "post");
    }

    private static ResourceLocation registerBlock(IForgeRegistry<Block> registry, Block block, String name) {
        ResourceLocation rl = new ResourceLocation(CrystalsMod.MODID, name);
        block.setRegistryName(rl);
        block.setUnlocalizedName(rl.toString());
        registry.register(block);
        return rl;
    }

    private static void registerBlock(IForgeRegistry<Block> registry, Block block, Class<? extends TileEntity> teClass, String name) {
        ResourceLocation rl = registerBlock(registry, block, name);
        GameRegistry.registerTileEntity(teClass, rl.toString());
    }

    private static void registerItem(IForgeRegistry<Item> registry, Block block, String name) {
        ResourceLocation rl = new ResourceLocation(CrystalsMod.MODID, name);
        Item item = new ItemBlock(block);
        item.setRegistryName(rl);
        item.setUnlocalizedName(rl.toString());
        registry.register(item);
    }

}
