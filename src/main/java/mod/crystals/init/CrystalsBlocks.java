package mod.crystals.init;

import mod.crystals.CrystalsMod;
import mod.crystals.block.*;
import mod.crystals.item.ItemSeal;
import mod.crystals.tile.TileCrystal;
import mod.crystals.tile.TileCrystalCreative;
import mod.crystals.tile.TileSeal;
import mod.crystals.tile.TileSlate;
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
    public static final Block crystal_creative = new BlockCrystalCreative();
    public static final Block post = new BlockPost();
    public static final Block slate = new BlockSlate();
    public static final Block seal = new BlockSeal();

    @SubscribeEvent
    public static void onBlockRegistration(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        registerBlock(registry, crystal, TileCrystal.class, "crystal");
        registerBlock(registry, crystal_creative, TileCrystalCreative.class, "crystal_creative");
        registerBlock(registry, post, "post");
        registerBlock(registry, slate, TileSlate.class, "slate");
        registerBlock(registry, seal, TileSeal.class, "seal");
    }

    @SubscribeEvent
    public static void onItemRegistration(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registerItem(registry, crystal, "crystal");
        registerItem(registry, crystal_creative, "crystal_creative");
        registerItem(registry, post, "post");
        registerItem(registry, slate, "slate");
        CrystalsItems.registerItem(registry, new ItemSeal(seal), "seal");
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

    private static ItemBlock registerItem(IForgeRegistry<Item> registry, Block block, String name) {
        ResourceLocation rl = new ResourceLocation(CrystalsMod.MODID, name);
        ItemBlock item = new ItemBlock(block);
        item.setRegistryName(rl);
        item.setUnlocalizedName(rl.toString());
        registry.register(item);
        return item;
    }

}
