package mod.crystals.client.particle;

import mod.crystals.block.BlockSeal;
import mod.crystals.block.BlockSealExt;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleRain extends Particle {

    public ParticleRain(World worldIn, double x, double y, double z, double vX, double vY, double vZ) {
        super(worldIn, x, y, z);
        this.motionX = vX;
        this.motionY = vY;
        this.motionZ = vZ;
        this.particleRed = 0.0F;
        this.particleGreen = 0.0F;
        this.particleBlue = 1.0F;
        this.setParticleTextureIndex(113);
        this.setSize(0.01F, 0.01F);
        this.particleGravity = 0.06F;
        this.particleMaxAge = 1000;
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.particleRed = 0.2F;
        this.particleGreen = 0.3F;
        this.particleBlue = 1.0F;

        this.motionY -= (double) this.particleGravity;

        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX = Math.copySign(Math.max(0, Math.abs(motionX) - 0.05), motionX);
        this.motionY *= 0.9800000190734863D;
        this.motionZ = Math.copySign(Math.max(0, Math.abs(motionZ) - 0.05), motionZ);

        if (this.particleMaxAge-- <= 0) {
            this.setExpired();
        }

        if (this.onGround) {
            this.setExpired();
            this.world.spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);

            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }

        BlockPos blockpos = new BlockPos(this.posX, this.posY, this.posZ);
        IBlockState iblockstate = this.world.getBlockState(blockpos);
        Material material = iblockstate.getMaterial();

        Block block = iblockstate.getBlock();
        if (!(block instanceof BlockSeal || block instanceof BlockSealExt) && (material.isLiquid() || material.isSolid())) { // FIXME make this not hardcoded
            double d0 = 0.0D;

            if (block instanceof BlockLiquid) {
                d0 = (double) BlockLiquid.getLiquidHeightPercent(iblockstate.getValue(BlockLiquid.LEVEL));
            }

            double d1 = (double) (MathHelper.floor(this.posY) + 1) - d0;

            if (this.posY < d1) {
                this.setExpired();
            }
        }
    }

}
