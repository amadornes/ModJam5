package mod.crystals.tile;

import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import mod.crystals.api.IResonant;
import mod.crystals.api.NatureType;
import mod.crystals.crystal.ILaserSource;
import mod.crystals.crystal.Ray;
import mod.crystals.init.CrystalsRegistries;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.Queue;
import java.util.Set;

public class TileCrystalCreative extends TileCrystalBase {

    public TileCrystalCreative(boolean ignoreJoin) {
        super(CreativeResonant.instance, ignoreJoin);
    }

    public TileCrystalCreative() {
        super(CreativeResonant.instance);
    }

    @Override
    public Vec3d getPosition(float partialTicks) {
        return new Vec3d(pos).add(OFFSET);
    }

    @Override
    public Vec3d getColor(float partialTicks) {
        Color color = new Color(CreativeResonant.instance.getColor());
        return new Vec3d(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0);
    }

    @Override
    protected void visit(Queue<Pair<TileCrystalBase, TObjectFloatMap<NatureType>>> queue, Set<TileCrystalBase> visited, TObjectFloatMap<NatureType> cap, TObjectFloatMap<NatureType> total) {
        cap.forEachKey(it -> {
            total.adjustOrPutValue(it, 1000, 1000);
            return true;
        });

        for (Ray ray : rays) {
            if (!ray.hasLineOfSight()) continue;
            ILaserSource other = ray.getEnd();
            if (other instanceof TileCrystalBase && visited.add((TileCrystalBase) other)) {
                TileCrystalBase crystal = (TileCrystalBase) other;
                TObjectFloatMap<NatureType> newCap = new TObjectFloatHashMap<>();
                cap.forEachEntry((type, max) -> {
                    float amt = crystal.resonant.getNatureAmount(type);
                    newCap.put(type, Math.min(amt, max));
                    return true;
                });
                queue.add(Pair.of(crystal, newCap));
            }
        }
    }

    public static class CreativeResonant implements IResonant.Default {
        public static final CreativeResonant instance = new CreativeResonant();

        private CreativeResonant() {}

        @Override
        public float getNatureAmount(NatureType natureType) {
            return 1.0f;
        }

        @Override
        public float getResonance() {
            return 1.0f;
        }

        @Override
        public void setNatureAmounts(TObjectFloatMap<NatureType> natureAmounts) {}

        @Override
        public void setResonance(float resonance) {}

        @Override
        public TObjectFloatMap<NatureType> getNatureAmounts() {
            TObjectFloatMap<NatureType> map = new TObjectFloatHashMap<>();
            CrystalsRegistries.natureRegistry.getValuesCollection().forEach(it -> map.put(it, 1.0f));
            return map;
        }

        @Override
        public int getColor() {
            return 0xca489f;
        }

        @Override
        public void addChangeListener(Runnable listener) {}
    }
}
