package mod.crystals.seal;

import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISeal;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.api.seal.SealType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import java.util.function.BiConsumer;

public class SealPraiseTheSun extends SealType {

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public int getTint() {
        return NatureType.FIRE.getColor();
    }

    @Override
    public Ingredient[][] createRecipe() {
        Ingredient center = new Ingredient(NatureType.FIRE, NatureType.DISTORTED);
        Ingredient fire = new Ingredient(NatureType.FIRE);
        return new Ingredient[][]{
                {fire, fire, fire},
                {fire, center, fire},
                {fire, fire, fire}
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
            capacity.accept(NatureType.FIRE, 50000F);
            capacity.accept(NatureType.DISTORTED, 25000F);
            consumption.accept(NatureType.FIRE, 50000F);
            consumption.accept(NatureType.DISTORTED, 25000F);
        }

        @Override
        public void update() {
            World world = seal.getWorld();
            if (world.isRemote) return;
            if (!world.isRaining()) return;

            int sunflowers = 0;
            for (EntityItem item : world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(seal.getPos()))) {
                ItemStack stack = item.getItem();
                if (stack.isEmpty()) continue;
                if (stack.getItem() == Item.getItemFromBlock(Blocks.DOUBLE_PLANT) && stack.getMetadata() == 0) {
                    sunflowers += stack.getCount();
                }
                if (sunflowers >= 64) {
                    break;
                }
            }

            if (sunflowers < 64) return;
            if (!consumeEnergy()) return;

            for (EntityItem item : world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(seal.getPos()))) {
                ItemStack stack = item.getItem();
                if (stack.isEmpty()) continue;
                if (stack.getItem() == Item.getItemFromBlock(Blocks.DOUBLE_PLANT) && stack.getMetadata() == 0) {
                    if (stack.getCount() <= sunflowers) {
                        sunflowers -= stack.getCount();
                        item.setDead();
                    } else {
                        stack.shrink(sunflowers);
                        break;
                    }
                }
            }

            WorldInfo info = world.getWorldInfo();
            info.setCleanWeatherTime(20 * 60 * 5);
            info.setRainTime(0);
            info.setThunderTime(0);
            info.setRaining(false);
            info.setThundering(false);
        }

    }

}
