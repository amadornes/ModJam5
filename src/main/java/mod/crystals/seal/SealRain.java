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

public class SealRain extends SealType {

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

    private class Instance implements ISealInstance {

        private final ISeal seal;

        private Instance(ISeal seal) {
            this.seal = seal;
        }

        @Override
        public void update() {
            startRain(seal, false, 20 * 60 * 5);
        }

    }

    public static void startRain(ISeal seal, boolean thunder, int duration) {
        World world = seal.getWorld();
        if (world.isRemote) return;
        if (world.isRaining()) return;
        for (EntityItem item : world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(seal.getPos()))) {
            ItemStack stack = item.getItem();
            if (stack.isEmpty()) continue;
            if (stack.getItem() == Items.WATER_BUCKET) {
                WorldInfo info = world.getWorldInfo();
                info.setCleanWeatherTime(0);
                info.setRainTime(duration);
                info.setThunderTime(duration);
                info.setRaining(true);
                info.setThundering(thunder);
                item.setDead();
                break;
            }
        }
    }

}
