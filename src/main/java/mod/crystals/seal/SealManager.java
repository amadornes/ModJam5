package mod.crystals.seal;

import gnu.trove.map.TObjectFloatMap;
import mod.crystals.CrystalsMod;
import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.network.PacketSealFX;
import mod.crystals.tile.TileCrystalBase;
import mod.crystals.tile.TileSeal;
import mod.crystals.util.ResonantUtils;
import mod.crystals.util.SimpleManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SealManager extends SimpleManager {

    private Set<TileSeal> seals = new HashSet<>();
    private Set<TileSeal> nextSeals = seals;

    public SealManager(World world) {
        super(world);
    }

    public void add(TileSeal seal) {
        if(nextSeals == seals){
            nextSeals = new HashSet<>(seals);
        }
        nextSeals.add(seal);
    }

    public void remove(TileSeal seal) {
        if(nextSeals == seals){
            nextSeals = new HashSet<>(seals);
        }
        nextSeals.remove(seal);
    }

    @Override
    protected void update(World world) {
        if (world.isRemote) return;

        Map<TileCrystalBase, TObjectFloatMap<NatureType>> natures = new HashMap<>();

        for (TileSeal seal : seals) {
            if (seal.getSeal() == null) continue;
            ISealInstance instance = seal.getSeal();
            for (TileCrystalBase crystal : findCrystals(seal)) {
                TObjectFloatMap<NatureType> cNatures = natures.computeIfAbsent(crystal, TileCrystalBase::visit);
                cNatures.forEachEntry((type, max) -> {
                    float accepted = instance.getAccepted(type);
                    if (accepted == 0) return true;
                    float amt = Math.min(accepted, max);
                    instance.addNature(type, amt);
                    spawnParticles(seal, crystal, type, amt);
                    return true;
                });
            }
        }

        seals = nextSeals;
    }

    private void spawnParticles(TileSeal seal, TileCrystalBase crystal, NatureType type, float amt) {
        Color color = new Color(type.getColor());
        World world = seal.getWorld();
        int count = Math.min(1 + (int) Math.sqrt(amt), 5);

        Vec3d center = new Vec3d(seal.getPos())
                .addVector(0.5, 0.5, 0.5)
                .add(new Vec3d(seal.getFace().getOpposite().getDirectionVec()).scale(0.5));
        Vec3d cPos = crystal.getPosition(0, true);
        Vec3d vec = center.subtract(cPos);
        Vec3d mot = vec.normalize().scale(0.1);

        CrystalsMod.net.sendToAllAround(PacketSealFX.create(count, cPos, mot, color),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), cPos.x, cPos.y, cPos.z, 32.0));
    }

    private Iterable<TileCrystalBase> findCrystals(TileSeal seal) {
        return ResonantUtils.getCrystalsAround(seal.getWorld(), seal.getPos(), 5, null);
    }

}
