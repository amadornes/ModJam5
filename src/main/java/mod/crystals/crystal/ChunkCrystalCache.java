package mod.crystals.crystal;

import mod.crystals.tile.TileCrystalBase;

import java.util.HashSet;
import java.util.Set;

public class ChunkCrystalCache {

    private final Set<TileCrystalBase> crystals = new HashSet<>();

    public void join(TileCrystalBase crystal) {
        crystals.add(crystal);
    }

    public void leave(TileCrystalBase crystal) {
        crystals.remove(crystal);
    }

    public Set<TileCrystalBase> getCrystals() {
        return crystals;
    }

}