package mod.crystals.client;

import mod.crystals.CommonProxy;
import mod.crystals.CrystalsMod;
import mod.crystals.block.BlockCrystal;
import mod.crystals.init.CrystalsBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.function.Function;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        OBJLoader.INSTANCE.addDomain(CrystalsMod.MODID);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        Minecraft mc = Minecraft.getMinecraft();
        BlockColors blockColors = mc.getBlockColors();
        blockColors.registerBlockColorHandler((state, world, pos, index) -> {
            Integer color = ((IExtendedBlockState) state).getValue(BlockCrystal.COLOR);
            if (color == null) return 0x000000;
            return color;
        }, CrystalsBlocks.crystal);
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        wrap(event, new ModelResourceLocation(CrystalsBlocks.crystal.getRegistryName(), "normal"), TintWrapper::new);
    }

    private void wrap(ModelBakeEvent event, ModelResourceLocation name, Function<IBakedModel, ? extends IBakedModel> wrapper) {
        IBakedModel model = event.getModelRegistry().getObject(name);
        if (model == null) return;
        event.getModelRegistry().putObject(name, wrapper.apply(model));
    }

}
