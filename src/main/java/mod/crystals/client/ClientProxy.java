package mod.crystals.client;

import mod.crystals.CommonProxy;
import mod.crystals.CrystalsMod;
import mod.crystals.block.BlockCrystal;
import mod.crystals.block.BlockSlate;
import mod.crystals.init.CrystalsBlocks;
import mod.crystals.init.CrystalsItems;
import mod.crystals.tile.TileCrystal;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.function.Function;

public class ClientProxy extends CommonProxy {
    private Minecraft mc;

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        mc = Minecraft.getMinecraft();
        OBJLoader.INSTANCE.addDomain(CrystalsMod.MODID);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        BlockColors blockColors = mc.getBlockColors();
        blockColors.registerBlockColorHandler((state, world, pos, index) -> {
            if(index == 1) return 0xFFFFFF;
            Integer color = ((IExtendedBlockState) state).getValue(BlockCrystal.COLOR);
            if (color == null) return 0x000000;
            return color;
        }, CrystalsBlocks.crystal);
        blockColors.registerBlockColorHandler((state, world, pos, index) -> {
            if(index < 0 || index >= 4) return 0x000000;
            Integer color = (Integer) ((IExtendedBlockState) state).getValue(BlockSlate.COLORS[index]);
            if (color == null) return 0x000000;
            return color;
        }, CrystalsBlocks.slate);
        ClientRegistry.bindTileEntitySpecialRenderer(TileCrystal.class, new FloatingCrystalRenderer());
    }

    @Override
    public void spawnParticle(Object particle) {
        super.spawnParticle(particle);
        if (particle instanceof Particle) {
            mc.effectRenderer.addEffect((Particle) particle);
        }
    }

    @SubscribeEvent
    public void onModelRegister(ModelRegistryEvent event) {
        addModel(CrystalsBlocks.crystal, 0, "inventory");
        addModel(CrystalsBlocks.slate, 0, "inventory");
        addItemModel(CrystalsItems.tuning_fork, 0, "tuning_fork");
        addItemModel(CrystalsItems.tuning_fork, 1, "tuning_fork_vibrating");
        addModel(CrystalsItems.dust, 0, "air");
        addModel(CrystalsItems.dust, 1, "water");
        addModel(CrystalsItems.dust, 2, "earth");
        addModel(CrystalsItems.dust, 3, "fire");
        addModel(CrystalsItems.dust, 4, "distorted");
        addModel(CrystalsItems.dust, 5, "void");
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        wrap(event, new ModelResourceLocation(CrystalsBlocks.crystal.getRegistryName(), "variant=floating"), TintWrapper::new);
        wrap(event, new ModelResourceLocation(CrystalsBlocks.crystal.getRegistryName(), "variant=ground"), TintWrapper::new);
    }

    private void wrap(ModelBakeEvent event, ModelResourceLocation name, Function<IBakedModel, ? extends IBakedModel> wrapper) {
        IBakedModel model = event.getModelRegistry().getObject(name);
        if (model == null) return;
        event.getModelRegistry().putObject(name, wrapper.apply(model));
    }

    private void addModel(Block block, int meta, String name) {
        addModel(Item.getItemFromBlock(block), meta, name);
    }

    private void addModel(Item item, int meta, String name) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), name));
    }

    private void addItemModel(Item item, int meta, String name) {
        ResourceLocation res = new ResourceLocation(item.getRegistryName().getResourceDomain(), name);
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(res, "inventory"));
    }

}
