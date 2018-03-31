package mod.crystals.util;

import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import mod.crystals.api.IResonant;
import mod.crystals.api.NatureType;
import mod.crystals.init.CrystalsRegistries;
import mod.crystals.tile.TileCrystal;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.HashSet;
import java.util.Set;

public class ResonantUtils {

    public static IResonant getCrystal(IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileCrystal) {
            return te.getCapability(IResonant.CAPABILITY, null);
        }
        return null;
    }

    public static TObjectFloatMap<NatureType> getNatureTypes(IResonant resonant, boolean copy) {
        if (resonant instanceof IResonant.Default) {
            TObjectFloatMap<NatureType> map = ((IResonant.Default) resonant).getNatureAmounts();
            return !copy ? map : new TObjectFloatHashMap<>(map);
        } else {
            TObjectFloatMap<NatureType> map = new TObjectFloatHashMap<>();
            for (NatureType type : CrystalsRegistries.natureRegistry.getValuesCollection()) {
                float amt = resonant.getNatureAmount(type);
                if (amt < 1e-4) continue;
                map.put(type, amt);
            }
            return map;
        }
    }

    public static void balance(TObjectFloatMap<NatureType> map1, TObjectFloatMap<NatureType> map2, float res1, float res2, float amt) {
        Set<NatureType> visited = new HashSet<>();

        float totalDif = 0;
        for (NatureType type : map1.keySet()) {
            visited.add(type);
            totalDif += Math.abs(map1.get(type) * res1 - map2.get(type) * res2);
        }
        for (NatureType type : map2.keySet()) {
            if (!visited.add(type)) continue;
            totalDif += map2.get(type) * res2;
        }

        if (totalDif < 1e-4) return;

        float dif = Math.min(totalDif, amt);

        for (NatureType type : visited) {
            visited.add(type);
            float amt1 = map1.containsKey(type) ? map1.get(type) * res1 : 0;
            float amt2 = map2.containsKey(type) ? map2.get(type) * res2 : 0;
            float transfered = dif * (Math.abs(amt1 - amt2) / totalDif) / 2F;
            if (amt1 < amt2) {
                map1.adjustOrPutValue(type, transfered, transfered);
                map2.adjustValue(type, -transfered);
            } else {
                map1.adjustValue(type, -transfered);
                map2.adjustOrPutValue(type, transfered, transfered);
            }
        }
    }

}
