package mod.crystals.crystal;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import mod.crystals.util.SimpleManager;
import net.minecraft.world.World;

import java.util.Collection;

public class RayManager extends SimpleManager {

    private Table<ILaserSource, ILaserSource, Ray> rays = HashBasedTable.create();

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

        return ray;
    }

    public void removeAll(ILaserSource src) {
        rays.row(src).forEach((to, ray) -> {
            src.onDisconnect(to, ray);
            to.onDisconnect(src, ray.getOpposite());
        });
        rays.column(src).forEach((to, ray) -> {
            src.onDisconnect(to, ray);
            to.onDisconnect(src, ray.getOpposite());
        });

        rays.rowKeySet().remove(src);
        rays.columnKeySet().remove(src);
    }

    public Collection<Ray> getRays() {
        return rays.values();
    }

    protected void update(World world) {
        for (Ray ray : getRays()) {
            ray.update(world);
        }
    }

}
