package mod.crystals;

import mod.crystals.api.NatureType;
import mod.crystals.client.particle.ParticleType;
import mod.crystals.client.particle.ParticleType.ParticleParams;
import mod.crystals.creativetab.CreativeTabCrystals;
import mod.crystals.environment.CrystalsWorldGenerator;
import mod.crystals.network.PacketSealData;
import mod.crystals.network.PacketSealFX;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class CommonProxy {

    public Map<NatureType, Integer> dustMetaMap = new HashMap<>();

    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);
        CreativeTabCrystals.init();

        if (e.getSide() == Side.CLIENT) {
            CrystalsMod.net.registerMessage(PacketSealData.Handler.class, PacketSealData.class, 0, Side.CLIENT);
            CrystalsMod.net.registerMessage(PacketSealFX.Handler.class, PacketSealFX.class, 1, Side.CLIENT);
        } else {
            CrystalsMod.net.registerMessage(PacketSealData.HandlerServer.class, PacketSealData.class, 0, Side.CLIENT);
            CrystalsMod.net.registerMessage(PacketSealFX.HandlerServer.class, PacketSealFX.class, 1, Side.CLIENT);
        }

        GameRegistry.registerWorldGenerator(new CrystalsWorldGenerator(), 2);
    }

    public void init(FMLInitializationEvent e) {}

    public void postInit(FMLPostInitializationEvent e) {}

    public <T extends ParticleParams> void spawnParticle(@Nonnull World world, @Nonnull ParticleType<T> type, @Nonnull T params) {}

}
