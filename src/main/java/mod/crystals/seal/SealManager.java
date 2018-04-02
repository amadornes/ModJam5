package mod.crystals.seal;

import gnu.trove.map.TObjectFloatMap;
import mod.crystals.CrystalsMod;
import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.client.particle.ParticleType;
import mod.crystals.tile.TileCrystalBase;
import mod.crystals.tile.TileSeal;
import mod.crystals.util.ResonantUtils;
import mod.crystals.util.SimpleManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static mod.crystals.client.particle.ParticleType.*;

public class SealManager extends SimpleManager {

    private Set<TileSeal> seals = new HashSet<>();

    public SealManager(World world) {
        super(world);
    }

    public void add(TileSeal seal) {
        seals.add(seal);
    }

    public void remove(TileSeal seal) {
        seals.remove(seal);
    }

    @Override
    protected void update(World world) {
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
                    if (world.isRemote) {
                        spawnParticles(seal, crystal, type, amt);
                    }
                    return true;
                });
            }
        }
    }

    private void spawnParticles(TileSeal seal, TileCrystalBase crystal, NatureType type, float amt) {
        Color color = new Color(type.getColor());
        World world = seal.getWorld();
        int count = Math.min(1 + (int) Math.sqrt(amt), 5);

        Vec3d center = new Vec3d(seal.getPos())
                .addVector(0.5, 0.5, 0.5)
                .add(new Vec3d(seal.getFace().getOpposite().getDirectionVec()).scale(0.5));
        Vec3d cPos = crystal.getPosition(0);
        Vec3d vec = center.subtract(cPos);
        Vec3d mot = vec.normalize().scale(0.1);

        for (int i = 0; i < count; i++) {
            CrystalsMod.proxy.spawnParticle(world, ParticleType.CIRCLE,
                    posVelocityColor(cPos.x, cPos.y, cPos.z, mot.x, mot.y, mot.z,
                            color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F));
        }
    }

    private Iterable<TileCrystalBase> findCrystals(TileSeal seal) {
        return ResonantUtils.getCrystalsAround(seal.getWorld(), seal.getPos(), 5, null);
    }

}
