package mod.crystals.client;

import mod.crystals.CrystalsMod;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = CrystalsMod.MODID, value = Side.CLIENT)
public class LaserRenderer {

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event){

    }

}
