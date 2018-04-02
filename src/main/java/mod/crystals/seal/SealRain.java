package mod.crystals.seal;

import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import mod.crystals.CrystalsMod;
import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISeal;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.api.seal.SealType;
import mod.crystals.client.particle.ParticleType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import java.awt.*;

import static mod.crystals.client.particle.ParticleType.*;

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

    @Override
    public ResourceLocation getGlowyTextureLocation(TextureType type) {
        switch (type) {
            case GLOWY_BLACK:
                return new ResourceLocation(CrystalsMod.MODID, "textures/seals/pushpull/glowy_thing_black.png");
            case GLOWY_TRANSPARENT:
                return new ResourceLocation(CrystalsMod.MODID, "textures/seals/pushpull/glowy_thing.png");
            case GLOWY_SHIMMER:
                //return new ResourceLocation(CrystalsMod.MODID, "textures/seals/pushpull/glowy_thing_shimmer.png");
        }
        return null;
    }

    @Override
    public int getGlowyColor() {
        return NatureType.AIR.getColor();
    }

    private class Instance implements ISealInstance {

        private final ISeal seal;
        private final TObjectFloatMap<NatureType> energy = new TObjectFloatHashMap<>();

        private Instance(ISeal seal) {
            this.seal = seal;
        }

        @Override
        public float getAccepted(NatureType type) {
            if (type != NatureType.EARTH) return 0;
            return 50 - energy.get(type);
        }

        @Override
        public void addNature(NatureType type, float amount) {
            energy.adjustOrPutValue(type, amount, amount);
        }

        @Override
        public void update() {
            startRain(seal, false, 20 * 60 * 5);

            if (seal.getWorld().getTotalWorldTime() % 5 != 0) return;

            Color color = new Color(NatureType.AIR.getColor());

            World world = seal.getWorld();
            BlockPos pos = seal.getPos();
            for (int i = 0; i < 4; i++) {
                CrystalsMod.proxy.spawnParticle(world, ParticleType.CIRCLE,
                        posVelocityColor(
                                pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                                0, 0.1 + Math.random() * 0.05, 0,
                                color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F));
            }
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
