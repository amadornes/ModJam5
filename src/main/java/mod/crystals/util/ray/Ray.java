package mod.crystals.util.ray;

import mod.crystals.util.ILaserSource;
import mod.crystals.util.RayTracer;
import net.minecraft.util.math.BlockPos;
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
        RayTraceResult hit = RayTracer.rayTrace(world, getStart(0), getEnd(0), this::shouldRayTrace);
        hasLOS = hit != null;
    }

    private boolean shouldRayTrace(BlockPos pos) {
        return !start.shouldIgnore(pos) && !end.shouldIgnore(pos);
    }

    public boolean hasLineOfSight() {
        return hasLOS;
    }

    public Vec3d getStart(float partialTicks) {
        return start.getPosition(partialTicks);
    }

    public Vec3d getEnd(float partialTicks) {
        return end.getPosition(partialTicks);
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
