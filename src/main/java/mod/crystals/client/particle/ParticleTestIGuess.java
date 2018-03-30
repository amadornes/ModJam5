package mod.crystals.client.particle;

import mod.crystals.CrystalsMod;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ParticleTestIGuess extends Particle {
    // look at https://github.com/Draco18s/ReasonableRealism/blob/1.12.1/src/main/java/com/draco18s/ores/client/ProspectorParticle.java for how to add custom particles
    // more than this is not going to happen right now
    // also, can we reuse existing particle textures? then this would be way easier

    private static final ResourceLocation PARTICLE = new ResourceLocation(CrystalsMod.MODID, "textures/entity/particles.png");

    private ParticleTestIGuess(World worldIn, double posXIn, double posYIn, double posZIn) {
        super(worldIn, posXIn, posYIn, posZIn, 0.0, 0.0, 0.0);
    }

    private ParticleTestIGuess(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    public static void spawnParticleAt(World world, double x, double y, double z, double vX, double vY, double vZ) {
        Particle particle = new ParticleTestIGuess(world, x, y, z, vX, vY, vZ);
        CrystalsMod.proxy.spawnParticle(particle);
    }
}
