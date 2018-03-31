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

    private static final ResourceLocation PARTICLE = new ResourceLocation(CrystalsMod.MODID, "textures/entity/particle.png");
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
        setSize(0.3f, 0.3f);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        pushMatrix();
        depthFunc(GL11.GL_ALWAYS);
        color(1.0F, 1.0F, 1.0F, this.particleAlpha);
        disableLighting();
        RenderHelper.disableStandardItemLighting();

        Minecraft.getMinecraft().getTextureManager().bindTexture(PARTICLE);
        int scaledAge = (int) (((float) this.particleAge + partialTicks) * 15.0F / (float) this.particleMaxAge);

        if (scaledAge <= 15) {
            float posX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
            float posY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
            float posZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
            color(1.0F, 1.0F, 1.0F, 1.0F);
            disableLighting();
            RenderHelper.disableStandardItemLighting();
            buffer.begin(GL11.GL_QUADS, VERTEX_FORMAT);
            buffer
                .pos(posX - rotationX * width - rotationXY * width, posY - rotationZ * height, posZ - rotationYZ * width - rotationXZ * width)
                .tex(1f, 1f)
                .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
                .lightmap(0, 240)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
            buffer
                .pos(posX - rotationX * width + rotationXY * width, posY + rotationZ * height, posZ - rotationYZ * width + rotationXZ * width)
                .tex(1f, 0f)
                .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
                .lightmap(0, 240)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
            buffer
                .pos(posX + rotationX * width + rotationXY * width, posY + rotationZ * height, posZ + rotationYZ * width + rotationXZ * width)
                .tex(0f, 0f)
                .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
                .lightmap(0, 240)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
            buffer
                .pos(posX + rotationX * width - rotationXY * width, posY - rotationZ * height, posZ + rotationYZ * width - rotationXZ * width)
                .tex(0f, 1f)
                .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
                .lightmap(0, 240)
                .normal(0.0F, 1.0F, 0.0F)
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
