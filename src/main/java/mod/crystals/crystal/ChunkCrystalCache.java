package mod.crystals.crystal;

import mod.crystals.tile.TileCrystal;

import java.util.HashSet;
import java.util.Set;

public class ChunkCrystalCache {

    private final Set<TileCrystal> crystals = new HashSet<>();

    public void join(TileCrystal crystal) {
        crystals.add(crystal);
    }

    public void leave(TileCrystal crystal) {
        crystals.remove(crystal);
    }

    public Set<TileCrystal> getCrystals() {
        return crystals;
    }

}