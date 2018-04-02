package mod.crystals.seal;

import gnu.trove.map.TObjectFloatMap;
import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.tile.TileCrystal;
import mod.crystals.tile.TileSeal;
import mod.crystals.util.ResonantUtils;
import mod.crystals.util.SimpleManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SealManager extends SimpleManager {

    private Set<TileSeal> seals = new HashSet<>();

    public SealManager(World world) {
        super(world);
    }

    public void add(TileSeal seal) {
        Set<TileSeal> seals = new HashSet<>(this.seals);
        seals.add(seal);
        this.seals = seals;
    }

    public void remove(TileSeal seal) {
        Set<TileSeal> seals = new HashSet<>(this.seals);
        seals.remove(seal);
        this.seals = seals;
    }

    @Override
    protected void update(World world) {
        Map<TileCrystal, TObjectFloatMap<NatureType>> natures = new HashMap<>();

        for (TileSeal seal : seals) {
            if (seal.getSeal() == null) continue;
            ISealInstance instance = seal.getSeal();
            for (TileCrystal crystal : findCrystals(seal)) {
                TObjectFloatMap<NatureType> cNatures = natures.computeIfAbsent(crystal, TileCrystal::visit);
                cNatures.forEachEntry((type, max) -> {
                    float accepted = instance.getAccepted(type);
                    if (accepted == 0) return true;
                    instance.addNature(type, Math.min(accepted, max * 5));
                    return true;
                });
            }
        }
    }

    private Iterable<TileCrystal> findCrystals(TileSeal seal) {
        return ResonantUtils.getCrystalsAround(seal.getWorld(), seal.getPos(), 5, null);
    }

}
