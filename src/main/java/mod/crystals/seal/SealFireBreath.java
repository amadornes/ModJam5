package mod.crystals.seal;

import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISeal;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.api.seal.SealType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.function.BiConsumer;

public class SealFireBreath extends SealType {

    private static final float RADIUS = 5;

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public Ingredient[][] createRecipe() {
        Ingredient fire = new Ingredient(NatureType.AIR, NatureType.FIRE);
        Ingredient pushFire = new Ingredient(NatureType.AIR, NatureType.FIRE, NatureType.DISTORTED);
        return new Ingredient[][]{
                {null, fire, null},
                {fire, pushFire, fire},
                {null, fire, null}
        };
    }

    @Override
    public ISealInstance instantiate(ISeal seal) {
        return new Instance(seal);
    }

    private static class Instance extends SealBreathInstance {

        public Instance(ISeal seal) {
            super(seal, RADIUS);
        }

        @Override
        public void addRequirements(BiConsumer<NatureType, Float> capacity, BiConsumer<NatureType, Float> consumption) {
        }

        @Override
        public void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
            world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, vx, vy, vz);
        }

        @Override
        public void addEffect(EntityLivingBase entity) {
            entity.setFire(1);
        }

    }

}
