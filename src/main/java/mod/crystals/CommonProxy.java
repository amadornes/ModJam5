package mod.crystals;

import mod.crystals.api.NatureType;
import mod.crystals.client.particle.ParticleType;
import mod.crystals.client.particle.ParticleType.ParticleParams;
import mod.crystals.creativetab.CreativeTabCrystals;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class CommonProxy {

    public Map<NatureType, Integer> dustMetaMap = new HashMap<>();

    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);
        CreativeTabCrystals.init();
    }

    public void init(FMLInitializationEvent e) {}

    public void postInit(FMLPostInitializationEvent e) {}

    public <T extends ParticleParams> void spawnParticle(@Nonnull World world, @Nonnull ParticleType<T> type, @Nonnull T params) {}

}
