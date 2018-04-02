package mod.crystals.crystal;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import mod.crystals.util.CrystalsWorldEventListener;
import mod.crystals.util.SimpleManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RayManager extends SimpleManager {

    private Table<ILaserSource, ILaserSource, Ray> rays = HashBasedTable.create();

    private Set<Ray> needsUpdate = new HashSet<>();

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

        needsUpdate.add(ray);

        return ray;
    }

    public void removeAll(ILaserSource src) {
        rays.row(src).forEach((to, ray) -> {
            src.onDisconnect(to, ray);
            to.onDisconnect(src, ray.getOpposite());
            needsUpdate.remove(ray);
        });
        rays.column(src).forEach((to, ray) -> {
            src.onDisconnect(to, ray);
            to.onDisconnect(src, ray.getOpposite());
            needsUpdate.remove(ray);
        });

        rays.rowKeySet().remove(src);
        rays.columnKeySet().remove(src);
    }

    public Collection<Ray> getRays() {
        return rays.values();
    }

    protected void update(World world) {
        for (Ray ray : needsUpdate) {
            System.out.println("Updating ray " + ray);
            ray.update(world);
        }
        needsUpdate.clear();
    }

    public void updateRays(@Nullable BlockPos updatePos) {
        for (Ray ray : getRays()) {
            if (updatePos != null) {
                AxisAlignedBB updatePosBB = new AxisAlignedBB(updatePos);
                AxisAlignedBB rayBox = new AxisAlignedBB(
                    ray.getStart(0).x, ray.getStart(0).y, ray.getStart(0).z,
                    ray.getEnd(0).x, ray.getEnd(0).y, ray.getEnd(0).z
                );
                if (!updatePosBB.intersects(rayBox)) continue;
            }
            needsUpdate.add(ray);
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load e) {
        e.getWorld().addEventListener(new CrystalsWorldEventListener(this));
    }

}
