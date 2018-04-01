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

    public ParticleTestIGuess(World worldIn, double x, double y, double z, double vX, double vY, double vZ, float r, float g, float b) {
        super(worldIn, x, y, z);
        this.motionX = vX;
        this.motionY = vY;
        this.motionZ = vZ;
        setPosition(x, y, z);
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        setRBGColorF(r, g, b);
        setSize(0.3f, 0.3f);
    }

    private double interp(double v1, double v2, double dist) {
        return v1 + (v2 - v1) * dist;
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
            float posX = (float) (interp(this.prevPosX, this.posX, partialTicks) - interp(entityIn.lastTickPosX, entityIn.posX, partialTicks));
            float posY = (float) (interp(this.prevPosY, this.posY, partialTicks) - interp(entityIn.lastTickPosY, entityIn.posY, partialTicks));
            float posZ = (float) (interp(this.prevPosZ, this.posZ, partialTicks) - interp(entityIn.lastTickPosZ, entityIn.posZ, partialTicks));
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
    public void move(double x, double y, double z) {
        setPosition(posX + x, posY + y, posZ + z);
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

}
