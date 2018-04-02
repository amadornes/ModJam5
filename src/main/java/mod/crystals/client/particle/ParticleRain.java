package mod.crystals.client.particle;

import mod.crystals.CrystalsMod;
import mod.crystals.block.BlockSeal;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleRain extends Particle {

    private static final ResourceLocation PARTICLE = new ResourceLocation(CrystalsMod.MODID, "textures/entity/particle.png");
    private static final VertexFormat VERTEX_FORMAT = new VertexFormat()
        .addElement(DefaultVertexFormats.POSITION_3F)
        .addElement(DefaultVertexFormats.TEX_2F)
        .addElement(DefaultVertexFormats.COLOR_4UB)
        .addElement(DefaultVertexFormats.TEX_2S)
        .addElement(DefaultVertexFormats.NORMAL_3B)
        .addElement(DefaultVertexFormats.PADDING_1B);

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

        if (!(iblockstate.getBlock() instanceof BlockSeal) && (material.isLiquid() || material.isSolid())) { // FIXME make this not hardcoded
            double d0 = 0.0D;

            if (iblockstate.getBlock() instanceof BlockLiquid) {
                d0 = (double) BlockLiquid.getLiquidHeightPercent(iblockstate.getValue(BlockLiquid.LEVEL));
            }

            double d1 = (double) (MathHelper.floor(this.posY) + 1) - d0;

            if (this.posY < d1) {
                this.setExpired();
            }
        }
    }

}
