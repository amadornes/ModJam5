package mod.crystals.client.particle;

import mod.crystals.CrystalsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import static net.minecraft.client.renderer.GlStateManager.*;

public class ParticleTestIGuess extends Particle {

    private static final ResourceLocation PARTICLE = new ResourceLocation(CrystalsMod.MODID, "textures/entity/particles.png");
    private static final VertexFormat VERTEX_FORMAT = new VertexFormat()
        .addElement(DefaultVertexFormats.POSITION_3F)
        .addElement(DefaultVertexFormats.TEX_2F)
        .addElement(DefaultVertexFormats.COLOR_4UB)
        .addElement(DefaultVertexFormats.TEX_2S)
        .addElement(DefaultVertexFormats.NORMAL_3B)
        .addElement(DefaultVertexFormats.PADDING_1B);

    private ParticleTestIGuess(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, float r, float g, float b) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn);
        this.motionX = xSpeedIn;
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn;
        setRBGColorF(r, g, b);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        pushMatrix();
        depthFunc(GL11.GL_ALWAYS);
        color(1.0F, 1.0F, 1.0F, this.particleAlpha);
        disableLighting();
        RenderHelper.disableStandardItemLighting();

        Minecraft.getMinecraft().getTextureManager().bindTexture(PARTICLE);
        int i = (int) (((float) this.particleAge + partialTicks) * 15.0F / (float) this.particleMaxAge);

        if (i <= 15) {
            float f = 0f;
            float f1 = 1f;
            float f2 = 0f;
            float f3 = 1f;
            float f4 = .3f;
            float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
            float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
            float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
            color(1.0F, 1.0F, 1.0F, 1.0F);
            disableLighting();
            RenderHelper.disableStandardItemLighting();
            buffer.begin(7, VERTEX_FORMAT);
            buffer
                .pos(f5 - rotationX * f4 - rotationXY * f4, f6 - rotationZ * f4, f7 - rotationYZ * f4 - rotationXZ * f4)
                .tex(f1, f3)
                .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
                .lightmap(0, 240)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
            buffer
                .pos(f5 - rotationX * f4 + rotationXY * f4, f6 + rotationZ * f4, f7 - rotationYZ * f4 + rotationXZ * f4)
                .tex(f1, f2)
                .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
                .lightmap(0, 240)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
            buffer
                .pos(f5 + rotationX * f4 + rotationXY * f4, f6 + rotationZ * f4, f7 + rotationYZ * f4 + rotationXZ * f4)
                .tex(f, f2)
                .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
                .lightmap(0, 240).normal(0.0F, 1.0F, 0.0F)
                .endVertex();
            buffer
                .pos(f5 + rotationX * f4 - rotationXY * f4, f6 - rotationZ * f4, f7 + rotationYZ * f4 - rotationXZ * f4)
                .tex(f, f3)
                .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
                .lightmap(0, 240).normal(0.0F, 1.0F, 0.0F)
                .endVertex();
            Tessellator.getInstance().draw();
            enableLighting();
        }
        depthFunc(GL11.GL_LEQUAL);
        popMatrix();
        enableLighting();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    public static void spawnParticleAt(World world, double x, double y, double z, float r, float g, float b) {
        Particle particle = new ParticleTestIGuess(world, x, y, z, 0, 0, 0, r, g, b);
        CrystalsMod.proxy.spawnParticle(particle);
    }

}
