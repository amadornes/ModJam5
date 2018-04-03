package mod.crystals.seal;

import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISeal;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.api.seal.SealType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.function.BiConsumer;

public class SealIceBreath extends SealType {

    private static final float RADIUS = 5;

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public Ingredient[][] createRecipe() {
        Ingredient ice = new Ingredient(NatureType.AIR, NatureType.WATER);
        Ingredient pushIce = new Ingredient(NatureType.AIR, NatureType.WATER, NatureType.DISTORTED);
        return new Ingredient[][]{
                {null, ice, null},
                {ice, pushIce, ice},
                {null, ice, null}
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
            world.spawnParticle(EnumParticleTypes.CLOUD, x, y, z, vx, vy, vz);
        }

        @Override
        public void addEffect(EntityLivingBase entity) {
            entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20, 3, true, false));
        }

    }

}
