package mod.crystals;

import mod.crystals.environment.EnvironmentHandler;
import mod.crystals.init.CrystalsCapabilities;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = CrystalsMod.MODID)
public class CrystalsMod {

    public static final String MODID = "crystals";

    @Instance
    public static CrystalsMod instance;

    @SidedProxy(serverSide = "mod.crystals.CommonProxy", clientSide = "mod.crystals.client.ClientProxy")
    public static CommonProxy proxy;

    public Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        EnvironmentHandler.INSTANCE.init(event.getAsmData());

        proxy.preInit(event);
        CrystalsCapabilities.register();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

}
