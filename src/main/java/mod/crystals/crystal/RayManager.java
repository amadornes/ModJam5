package mod.crystals.crystal;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import mod.crystals.util.CrystalsWorldEventListener;
import mod.crystals.util.SimpleManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RayManager extends SimpleManager {

    private Table<ILaserSource, ILaserSource, Ray> rays = HashBasedTable.create();

    private Set<Ray> updated = new HashSet<>();
    private Set<Ray> otherUpdate = new HashSet<>();

    public RayManager(World world) {
        super(world);
    }

    public Ray addRay(ILaserSource from, ILaserSource to) {
        Ray ray = rays.get(from, to);
        if (ray != null) return ray;

        ray = rays.get(to, from);
        if (ray != null) return ray.getOpposite();

        ray = new Ray(from, to);

        rays.put(from, to, ray);

        from.onConnect(to, ray);
        to.onConnect(from, ray.getOpposite());

        updated.add(ray);

        return ray;
    }

    public void removeAll(ILaserSource src) {
        rays.row(src).forEach((to, ray) -> {
            src.onDisconnect(to, ray);
            to.onDisconnect(src, ray.getOpposite());
            updated.remove(ray);
        });
        rays.column(src).forEach((to, ray) -> {
            src.onDisconnect(to, ray);
            to.onDisconnect(src, ray.getOpposite());
            updated.remove(ray);
        });

        rays.rowKeySet().remove(src);
        rays.columnKeySet().remove(src);
    }

    public Collection<Ray> getRays() {
        return rays.values();
    }

    protected void update(World world) {
        Set<Ray> tmp = updated;
        updated = otherUpdate;
        otherUpdate = tmp;

        for (Ray ray : otherUpdate) {
            ray.update(world);
        }
        otherUpdate.clear();
    }

    public void updateRays(@Nullable BlockPos updatePos) {
        for (Ray ray : getRays()) {
            if (updatePos != null) {
                AxisAlignedBB updatePosBB = new AxisAlignedBB(updatePos);
                Vec3d start = ray.getStart(0, false);
                Vec3d end = ray.getEnd(0, false);
                AxisAlignedBB rayBox = new AxisAlignedBB(start.x, start.y, start.z,end.x, end.y, end.z);
                if (!updatePosBB.intersects(rayBox)) continue;
            }
            updated.add(ray);
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load e) {
        e.getWorld().addEventListener(new CrystalsWorldEventListener(this));
    }

}
