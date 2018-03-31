package mod.crystals.util.ray;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import mod.crystals.util.ILaserSource;

import java.util.Collection;

public class RayManager {

    private final Table<ILaserSource, ILaserSource, Ray> rays = HashBasedTable.create();

    public Ray addRay(ILaserSource from, ILaserSource to) {
        Ray ray = rays.get(from, to);
        if (ray != null) return ray;

        ray = rays.get(to, from);
        if (ray != null) return ray.getOpposite();

        ray = new Ray(from, to);
        rays.put(from, to, ray);
        return ray;
    }

    public void removeRay(ILaserSource src1, ILaserSource src2) {
        rays.remove(src1, src2);
        rays.remove(src2, src1);
    }

    public void removeAll(ILaserSource src) {
        rays.rowKeySet().remove(src);
        rays.columnKeySet().remove(src);
    }

    public Collection<Ray> getRays() {
        return rays.values();
    }

}
