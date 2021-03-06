package mod.crystals.client.particle;

import net.minecraft.util.math.Vec3d;

import java.awt.*;

@SuppressWarnings("unused")
public class ParticleType<T extends ParticleType.ParticleParams> {

    private static int counter = 0;

    public final int type = counter++;

    public static final ParticleType<PPosVelocity> RAIN = new ParticleType<>();
    public static final ParticleType<PPosVelocityColor> CIRCLE = new ParticleType<>();

    private ParticleType() {}

    public interface ParticleParams {}

    public static class PPosColor implements ParticleParams {
        public final Vec3d position;
        public final Color color;

        public PPosColor(Vec3d position, Color color) {
            this.position = position;
            this.color = color;
        }
    }

    public static class PPosVelocity implements ParticleParams {
        public final Vec3d position;
        public final Vec3d velocity;

        public PPosVelocity(Vec3d position, Vec3d velocity) {
            this.position = position;
            this.velocity = velocity;
        }
    }

    public static class PPosVelocityColor implements ParticleParams {
        public final Vec3d position;
        public final Vec3d velocity;
        public final Color color;

        public PPosVelocityColor(Vec3d position, Vec3d velocity, Color color) {
            this.position = position;
            this.velocity = velocity;
            this.color = color;
        }
    }

    public static PPosColor posColor(double x, double y, double z, float r, float g, float b) {
        return new PPosColor(new Vec3d(x, y, z), new Color(r, g, b));
    }

    public static PPosVelocity posVelocity(double x, double y, double z, double vX, double vY, double vZ) {
        return new PPosVelocity(new Vec3d(x, y, z), new Vec3d(vX, vY, vZ));
    }

    public static PPosVelocityColor posVelocityColor(double x, double y, double z, double vX, double vY, double vZ, float r, float g, float b) {
        return new PPosVelocityColor(new Vec3d(x, y, z), new Vec3d(vX, vY, vZ), new Color(r, g, b));
    }

}
