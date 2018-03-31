package mod.crystals.environment;

import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import mod.crystals.api.IEnvironmentScanner;
import mod.crystals.api.NatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import java.util.HashSet;
import java.util.Set;

public enum EnvironmentHandler {
    INSTANCE;

    private final Set<IEnvironmentScanner> scanners = new HashSet<>();

    public void init(ASMDataTable table) {
        Set<ASMDataTable.ASMData> data = table.getAll(IEnvironmentScanner.Marker.class.getName());
        for (ASMDataTable.ASMData d : data) {
            try {
                Class<?> clazz = Class.forName(d.getClassName());
                IEnvironmentScanner scanner = (IEnvironmentScanner) clazz.newInstance();
                scanners.add(scanner);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public TObjectFloatMap<NatureType> getNature(World world, BlockPos pos) {
        TObjectFloatMap<NatureType> map = new TObjectFloatHashMap<>(16, 0.75F, 0);
        for (IEnvironmentScanner scanner : scanners) {
            scanner.compute(world, pos, (type, amt) -> map.adjustOrPutValue(type, amt, amt));
        }
        float sum = 0;
        for (float amt : map.values()) sum += amt;
        if (sum != 0) {
            float totalSum = sum;
            map.transformValues(amt -> amt / totalSum);
        }
        return map;
    }

}
