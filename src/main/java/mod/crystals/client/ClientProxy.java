package mod.crystals.client;

import mod.crystals.CommonProxy;
import mod.crystals.CrystalsMod;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        OBJLoader.INSTANCE.addDomain(CrystalsMod.MODID);
    }

}
