package mod.crystals.seal;

import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISeal;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.api.seal.SealType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;

public class SealRain extends SealType {

    @Override
    public int getSize() {
        return 3;
    }

    @Override
    public int getTint() {
        return NatureType.WATER.getColor();
    }

    @Override
    public Ingredient[][] createRecipe() {
        Ingredient cloud = new Ingredient(NatureType.WATER, NatureType.AIR, NatureType.DISTORTED);
        Ingredient rain = new Ingredient(NatureType.WATER, NatureType.AIR);
        return new Ingredient[][]{
                {cloud, cloud, cloud},
                {rain, rain, rain},
                {rain, rain, rain}
        };
    }

    @Override
    public ISealInstance instantiate(ISeal seal) {
        return new Instance(seal);
    }

    private class Instance extends AbstractSeal {

        public Instance(ISeal seal) {
            super(seal);
        }

        @Override
        public void addRequirements(BiConsumer<NatureType, Float> capacity, BiConsumer<NatureType, Float> consumption) {
            capacity.accept(NatureType.WATER, 5000F);
            capacity.accept(NatureType.AIR, 5000F);
            capacity.accept(NatureType.DISTORTED, 2500F);
            consumption.accept(NatureType.WATER, 5000F);
            consumption.accept(NatureType.AIR, 5000F);
            consumption.accept(NatureType.DISTORTED, 2500F);
        }

        @Override
        public void update() {
            startRain(seal, false, 20 * 60 * 5, this::consumeEnergy);
        }

    }

    public static void startRain(ISeal seal, boolean thunder, int duration, BooleanSupplier consume) {
        World world = seal.getWorld();
        if (world.isRemote) return;
        if (world.isRaining()) return;
        for (EntityItem item : world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(seal.getPos()))) {
            ItemStack stack = item.getItem();
            if (stack.isEmpty()) continue;
            if (stack.getItem() == Items.WATER_BUCKET) {
                if (!consume.getAsBoolean()) return;
                WorldInfo info = world.getWorldInfo();
                info.setCleanWeatherTime(0);
                info.setRainTime(duration);
                info.setThunderTime(duration);
                info.setRaining(true);
                info.setThundering(thunder);
                item.setDead();
                return;
            }
        }
    }

}
