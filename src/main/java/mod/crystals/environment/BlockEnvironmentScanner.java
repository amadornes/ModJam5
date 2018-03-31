package mod.crystals.environment;

import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import mod.crystals.api.IEnvironmentScanner;
import mod.crystals.api.NatureType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

@IEnvironmentScanner.Marker
public class BlockEnvironmentScanner implements IEnvironmentScanner {

    private static final int RADIUS = 5;
    private static final int MAX_DIST = RADIUS * 3;

    @Override
    public void compute(World world, BlockPos pos, NatureType.Acceptor acceptor) {
        TObjectFloatMap<NatureType> map = new TObjectFloatHashMap<>();

        for (BlockPos p : BlockPos.getAllInBoxMutable(pos.add(-RADIUS, -RADIUS, -RADIUS), pos.add(RADIUS, RADIUS, RADIUS))) {
            if (!world.isBlockLoaded(p)) continue;
            IBlockState state = world.getBlockState(p);
            Material mat = state.getMaterial();

            Vec3i dist = p.subtract(pos);
            float weight = ((MAX_DIST - Math.abs(dist.getX()) - Math.abs(dist.getY()) - Math.abs(dist.getZ())) / (float) MAX_DIST);

            if (mat == Material.GRASS || mat == Material.GROUND || mat == Material.CLAY || mat == Material.IRON
                    || mat == Material.PLANTS || mat == Material.LEAVES || mat == Material.VINE || mat == Material.WOOD) {
                map.put(NatureType.EARTH, 20 * weight);
            } else if (mat == Material.ROCK || mat == Material.CACTUS) {
                map.put(NatureType.EARTH, 15 * weight);
                map.put(NatureType.DISTORTED, 5 * weight);
            } else if (mat == Material.SAND) {
                map.put(NatureType.AIR, 10 * weight);
                map.put(NatureType.EARTH, 10 * weight);
            } else if (mat == Material.ICE || mat == Material.PACKED_ICE || mat == Material.SNOW || mat == Material.CRAFTED_SNOW) {
                map.put(NatureType.AIR, 10 * weight);
                map.put(NatureType.WATER, 10 * weight);
            } else if (mat == Material.WATER) {
                map.put(NatureType.WATER, 20 * weight);
            } else if (mat == Material.LAVA) {
                map.put(NatureType.FIRE, 20 * weight);
            }
        }

        float sum = 0;
        for (float amt : map.values()) sum += amt;
        if (sum != 0) {
            float totalSum = sum;
            map.forEachEntry((type, amt) -> {
                acceptor.accept(type, 80 * amt / totalSum);
                return true;
            });
        }
    }

}
