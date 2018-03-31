package mod.crystals.util.ray;

import mod.crystals.util.IPosition;
import mod.crystals.util.RayTracer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Ray {

    private final IPosition start, end;
    private boolean hasLOS;

    public Ray(IPosition start, IPosition end) {
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

}
