package mod.crystals.client.particle;

import mod.crystals.CrystalsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import static net.minecraft.client.renderer.GlStateManager.*;

public class ParticleCircle extends Particle {

    private static final ResourceLocation TEX_TRANSPARENT = new ResourceLocation(CrystalsMod.MODID, "textures/particles/circle_transparent.png");
    private static final ResourceLocation TEX_BLACK = new ResourceLocation(CrystalsMod.MODID, "textures/particles/circle_black.png");
    private static final ResourceLocation TEX_SHIMMER = new ResourceLocation(CrystalsMod.MODID, "textures/particles/circle_shimmer.png");

    public ParticleCircle(World world, double x, double y, double z, double vX, double vY, double vZ, float r, float g, float b) {
        super(world, x, y, z);
        this.motionX = vX;
        this.motionY = vY;
        this.motionZ = vZ;
        setPosition(x, y, z);
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        setRBGColorF(r, g, b);
        float size = (float) (0.05F + Math.random() * 0.05F);
        setSize(size, size);
    }

    private double interp(double v1, double v2, double dist) {
        return v1 + (v2 - v1) * dist;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        int scaledAge = (int) (((float) this.particleAge + partialTicks) * 15.0F / (float) this.particleMaxAge);

        if (scaledAge <= 15) {
            float posX = (float) (interp(this.prevPosX, this.posX, partialTicks) - interp(entityIn.lastTickPosX, entityIn.posX, partialTicks));
            float posY = (float) (interp(this.prevPosY, this.posY, partialTicks) - interp(entityIn.lastTickPosY, entityIn.posY, partialTicks));
            float posZ = (float) (interp(this.prevPosZ, this.posZ, partialTicks) - interp(entityIn.lastTickPosZ, entityIn.posZ, partialTicks));

            pushMatrix();

            GlStateManager.enableBlend();
            GlStateManager.depthMask(false);
            GlStateManager.shadeModel(GL11.GL_SMOOTH);

            disableLighting();

            color(1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            Minecraft.getMinecraft().getTextureManager().bindTexture(TEX_BLACK);
            drawQuad(buffer, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ, posX, posY, posZ, 0.5F, true);
            Minecraft.getMinecraft().getTextureManager().bindTexture(TEX_SHIMMER);
            drawQuad(buffer, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ, posX, posY, posZ, 0.1F, false);

            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            enableLighting();

            GlStateManager.shadeModel(GL11.GL_FLAT);
            GlStateManager.depthMask(true);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableBlend();

            popMatrix();
        }
    }

    public void drawQuad(BufferBuilder buffer, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ, float posX, float posY, float posZ, float alpha, boolean color) {
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer
                .pos(posX - rotationX * width - rotationXY * width, posY - rotationZ * height, posZ - rotationYZ * width - rotationXZ * width)
                .tex(1f, 1f)
                .color(color ? particleRed : 1, color ? particleGreen : 1, color ? particleBlue : 1, alpha)
                .endVertex();
        buffer
                .pos(posX - rotationX * width + rotationXY * width, posY + rotationZ * height, posZ - rotationYZ * width + rotationXZ * width)
                .tex(1f, 0f)
                .color(color ? particleRed : 1, color ? particleGreen : 1, color ? particleBlue : 1, alpha)
                .endVertex();
        buffer
                .pos(posX + rotationX * width + rotationXY * width, posY + rotationZ * height, posZ + rotationYZ * width + rotationXZ * width)
                .tex(0f, 0f)
                .color(color ? particleRed : 1, color ? particleGreen : 1, color ? particleBlue : 1, alpha)
                .endVertex();
        buffer
                .pos(posX + rotationX * width - rotationXY * width, posY - rotationZ * height, posZ + rotationYZ * width - rotationXZ * width)
                .tex(0f, 1f)
                .color(color ? particleRed : 1, color ? particleGreen : 1, color ? particleBlue : 1, alpha)
                .endVertex();
        Tessellator.getInstance().draw();
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
