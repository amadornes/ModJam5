package mod.crystals.crystal;

import mod.crystals.util.RayTracer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Ray {

    private final ILaserSource start, end;

    private boolean hasLOS;
    private Ray opposite;

    public Ray(ILaserSource start, ILaserSource end) {
        this.start = start;
        this.end = end;
    }

    public void update(World world) {
        RayTraceResult hit = RayTracer.rayTraceLaser(world, getStart(0, false), getEnd(0, false));
        hasLOS = hit == null;
    }

    public boolean hasLineOfSight() {
        return hasLOS;
    }

    public ILaserSource getStart() {
        return start;
    }

    public ILaserSource getEnd() {
        return end;
    }

    public Vec3d getStart(float partialTicks, boolean render) {
        return start.getPosition(partialTicks, render);
    }

    public Vec3d getEnd(float partialTicks, boolean render) {
        return end.getPosition(partialTicks, render);
    }

    public Vec3d getStartColor(float partialTicks) {
        return start.getColor(partialTicks);
    }

    public Vec3d getEndColor(float partialTicks) {
        return end.getColor(partialTicks);
    }

    public Ray getOpposite() {
        if (opposite == null) {
            return opposite = new OppositeRay(this);
        }
        return opposite;
    }

    private static class OppositeRay extends Ray {

        private final Ray parent;

        private OppositeRay(Ray parent) {
            super(parent.end, parent.start);
            this.parent = parent;
        }

        @Override
        public void update(World world) {
            throw new IllegalStateException("This should not be called manually >_>");
        }

        @Override
        public boolean hasLineOfSight() {
            return parent.hasLineOfSight();
        }

    }

}
