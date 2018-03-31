package mod.crystals.environment;

import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import mod.crystals.api.IEnvironmentScanner;
import mod.crystals.api.NatureType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

/*
 * Sorry in advance.
 */
@IEnvironmentScanner.Marker
public class BiomeEnvironmentScanner implements IEnvironmentScanner {

    @Override
    public void compute(World world, BlockPos pos, NatureType.Acceptor acceptor) {
        Biome biome = world.getBiome(pos);

        if (biome == Biomes.OCEAN || biome == Biomes.RIVER || biome == Biomes.DEEP_OCEAN) {
            acceptor.accept(NatureType.WATER, 100);
        } else if (biome == Biomes.FROZEN_OCEAN || biome == Biomes.FROZEN_RIVER) {
            acceptor.accept(NatureType.AIR, 20);
            acceptor.accept(NatureType.WATER, 80);
        } else if (biome == Biomes.BEACH || biome == Biomes.STONE_BEACH || biome == Biomes.COLD_BEACH) {
            acceptor.accept(NatureType.AIR, 20);
            acceptor.accept(NatureType.WATER, 30);
            acceptor.accept(NatureType.EARTH, 50);
        } else if (biome == Biomes.JUNGLE || biome == Biomes.JUNGLE_HILLS || biome == Biomes.JUNGLE_EDGE
                || biome == Biomes.FOREST || biome == Biomes.EXTREME_HILLS_EDGE || biome == Biomes.BIRCH_FOREST
                || biome == Biomes.BIRCH_FOREST_HILLS || biome == Biomes.ROOFED_FOREST || biome == Biomes.REDWOOD_TAIGA
                || biome == Biomes.REDWOOD_TAIGA_HILLS || biome == Biomes.EXTREME_HILLS_WITH_TREES
                || biome == Biomes.FOREST_HILLS || biome == Biomes.PLAINS || biome == Biomes.EXTREME_HILLS
                || biome == Biomes.MUTATED_PLAINS || biome == Biomes.MUTATED_EXTREME_HILLS
                || biome == Biomes.MUTATED_FOREST || biome == Biomes.MUTATED_JUNGLE
                || biome == Biomes.MUTATED_JUNGLE_EDGE || biome == Biomes.MUTATED_BIRCH_FOREST
                || biome == Biomes.MUTATED_BIRCH_FOREST_HILLS || biome == Biomes.MUTATED_ROOFED_FOREST
                || biome == Biomes.MUTATED_EXTREME_HILLS_WITH_TREES) {
            acceptor.accept(NatureType.EARTH, 100);
        } else if (biome == Biomes.SAVANNA || biome == Biomes.SAVANNA_PLATEAU || biome == Biomes.MESA
                || biome == Biomes.MESA_ROCK || biome == Biomes.MESA_CLEAR_ROCK || biome == Biomes.MUTATED_SAVANNA
                || biome == Biomes.MUTATED_SAVANNA_ROCK || biome == Biomes.MUTATED_MESA
                || biome == Biomes.MUTATED_MESA_ROCK || biome == Biomes.MUTATED_MESA_CLEAR_ROCK) {
            acceptor.accept(NatureType.EARTH, 80);
            acceptor.accept(NatureType.FIRE, 20);
        } else if (biome == Biomes.SWAMPLAND || biome == Biomes.MUTATED_SWAMPLAND) {
            acceptor.accept(NatureType.EARTH, 60);
            acceptor.accept(NatureType.WATER, 35);
            acceptor.accept(NatureType.DISTORTED, 5);
        } else if (biome == Biomes.DESERT || biome == Biomes.MUTATED_DESERT || biome == Biomes.DESERT_HILLS) {
            acceptor.accept(NatureType.AIR, 30);
            acceptor.accept(NatureType.EARTH, 50);
            acceptor.accept(NatureType.FIRE, 20);
        } else if (biome == Biomes.ICE_PLAINS
                || biome == Biomes.ICE_MOUNTAINS || biome == Biomes.MUTATED_ICE_FLATS
                || biome == Biomes.COLD_TAIGA || biome == Biomes.COLD_TAIGA_HILLS || biome == Biomes.TAIGA
                || biome == Biomes.TAIGA_HILLS || biome == Biomes.MUTATED_TAIGA_COLD || biome == Biomes.MUTATED_TAIGA
                || biome == Biomes.MUTATED_REDWOOD_TAIGA || biome == Biomes.MUTATED_REDWOOD_TAIGA_HILLS) {
            acceptor.accept(NatureType.EARTH, 70);
            acceptor.accept(NatureType.WATER, 15);
            acceptor.accept(NatureType.AIR, 15);
        } else if (biome == Biomes.MUSHROOM_ISLAND || biome == Biomes.MUSHROOM_ISLAND_SHORE) {
            acceptor.accept(NatureType.EARTH, 40);
            acceptor.accept(NatureType.WATER, 20);
            acceptor.accept(NatureType.DISTORTED, 40);
        } else if (biome == Biomes.HELL) {
            acceptor.accept(NatureType.FIRE, 60);
            acceptor.accept(NatureType.DISTORTED, 40);
        } else if (biome == Biomes.SKY || biome == Biomes.VOID) {
            acceptor.accept(NatureType.VOID, 80);
            acceptor.accept(NatureType.DISTORTED, 20);
        } else {
            TObjectFloatMap<NatureType> map = new TObjectFloatHashMap<>();

            if (biome.isSnowyBiome()) {
                map.put(NatureType.WATER, 20);
                map.put(NatureType.AIR, 20);
            }
            if (biome.getEnableSnow()) {
                map.put(NatureType.WATER, 10);
                map.put(NatureType.AIR, 10);
            }
            if (biome.isHighHumidity()) {
                map.put(NatureType.WATER, 20);
            }
            if (!biome.isSnowyBiome() && !biome.canRain()) {
                map.put(NatureType.FIRE, 10);
            }

            Material fillerMat = biome.fillerBlock.getMaterial();
            if (fillerMat == Material.ROCK || fillerMat == Material.GROUND) {
                map.put(NatureType.EARTH, 30);
            }
            Material topMat = biome.topBlock.getMaterial();
            if (topMat == Material.ROCK || topMat == Material.GROUND) {
                map.put(NatureType.EARTH, 20);
            }

            float sum = 0;
            for (float amt : map.values()) sum += amt;
            if (sum != 0) {
                float totalSum = sum;
                map.forEachEntry((type, amt) -> {
                    acceptor.accept(type, 100 * amt / totalSum);
                    return true;
                });
            }
        }
    }

}
