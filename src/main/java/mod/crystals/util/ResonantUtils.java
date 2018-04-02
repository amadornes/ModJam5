package mod.crystals.util;

import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import mod.crystals.api.IResonant;
import mod.crystals.api.NatureType;
import mod.crystals.capability.CapabilityCrystalCache;
import mod.crystals.capability.CapabilityLoadedCache;
import mod.crystals.init.CrystalsRegistries;
import mod.crystals.tile.TileCrystalBase;
import mod.crystals.tile.TileCrystalCreative;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.HashSet;
import java.util.Set;

public class ResonantUtils {

    public static IResonant getCrystal(IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileCrystalBase) {
            return te.getCapability(IResonant.CAPABILITY, null);
        }
        return null;
    }

    public static Set<TileCrystalBase> getCrystalsAround(World world, BlockPos pos, float radius, TileCrystalBase ignored) {
        Set<TileCrystalBase> crystals = new HashSet<>();
        ChunkPos min = new ChunkPos(pos.add(-radius, 0, -radius));
        ChunkPos max = new ChunkPos(pos.add(radius, 0, radius));
        CapabilityLoadedCache.LoadedChunks loaded = world.getCapability(CapabilityLoadedCache.CAPABILITY, null);
        for (int x = min.x; x <= max.x; x++) {
            for (int z = min.z; z <= max.z; z++) {
                // TODO: Potentially AT the chunk load check method?
                if (!loaded.isLoaded(x, z)) continue;

                Chunk c = world.getChunkFromChunkCoords(x, z);
                for (TileCrystalBase crystal : c.getCapability(CapabilityCrystalCache.CAPABILITY, null).getCrystals()) {
                    if (crystal == ignored) continue;
                    if (crystal.getPos().distanceSq(pos) < radius * radius) {
                        crystals.add(crystal);
                    }
                }
            }
        }
        return crystals;
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

    public static float getMatch(IResonant res1, IResonant res2) {
        // TODO not hardcode this?
        if (res1 == TileCrystalCreative.CreativeResonant.instance || res2 == TileCrystalCreative.CreativeResonant.instance)
            return 1.0f;
        TObjectFloatMap<NatureType> map1 = getNatureTypes(res1, false);
        TObjectFloatMap<NatureType> map2 = getNatureTypes(res2, false);
        Set<NatureType> visited = new HashSet<>();

        float dif = 0;

        for (NatureType type : map1.keySet()) {
            visited.add(type);
            dif += Math.abs(map1.get(type) - map2.get(type));
        }
        for (NatureType type : map2.keySet()) {
            if (!visited.add(type)) continue;
            dif += map2.get(type);
        }

        return 1F - (dif / 2F);
    }

}
