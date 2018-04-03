package mod.crystals.client.render;

import mod.crystals.api.seal.ISealInstance;
import mod.crystals.api.seal.SealType;
import mod.crystals.tile.TileSeal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

import static net.minecraft.client.renderer.GlStateManager.*;

public class SealRenderer extends TileEntitySpecialRenderer<TileSeal> {

    @Override
    public void render(TileSeal te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te.getSeal() == null) return;

        pushMatrix();
        translate(x + 0.5, y + 0.5, z + 0.5);
        switch (te.getFace()) {
            case DOWN:
                rotate(180, 0, 0, 1);
                break;
            case UP:
                break;
            case NORTH:
                rotate(180, 0, 1, 0);
                break;
            case SOUTH:
                break;
            case WEST:
                rotate(270, 0, 1, 0);
                break;
            case EAST:
                rotate(90, 0, 1, 0);
                break;
        }
        if (te.getFace().getAxis() != EnumFacing.Axis.Y) {
            rotate(90, 1, 0, 0);
        }
        translate(0, 0.002 - 0.5, 0);

        enableBlend();
        depthMask(false);
        shadeModel(GL11.GL_SMOOTH);
        disableLighting();

        renderAll(te.getSealType(), te.getSeal(), te.getWorld().getTotalWorldTime() + partialTicks);

        enableLighting();
        shadeModel(GL11.GL_FLAT);
        depthMask(true);
        blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        disableBlend();

        popMatrix();
    }

    private void renderAll(SealType type, ISealInstance seal, double worldTime) {
        ResourceLocation path;
        Color color = new Color(type.getTint());
        int size = type.getSize();

        blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

        path = type.getTextureLocation(SealType.TextureType.BASE);
        if (path != null) {
            Minecraft.getMinecraft().renderEngine.bindTexture(path);
            renderSeal(size, color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1);
        }

        path = type.getTextureLocation(SealType.TextureType.OVERLAY);
        if (path != null) {
            Minecraft.getMinecraft().renderEngine.bindTexture(path);
            renderSeal(size, color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1);
        }

        float rotSpeed = type.getRotationSpeed();
        if (rotSpeed != 0) {
            float amt = 500F / rotSpeed;
            float time = (float) (worldTime % amt) / amt;
            rotate(360 * time, 0, 1, 0);
        }

        path = type.getTextureLocation(SealType.TextureType.GLOW_TRANSPARENT);
        if (path != null) {
            Minecraft.getMinecraft().renderEngine.bindTexture(path);
            renderSeal(size, color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1);
        }

        path = type.getTextureLocation(SealType.TextureType.GLOW_BLACK);
        if (path != null) {
            Minecraft.getMinecraft().renderEngine.bindTexture(path);
            blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, DestFactor.ONE);
            renderSeal(size, color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1);
        }
    }

    private void renderSeal(int size, float r, float g, float b, float a) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        float d = size / 2F;

        buffer.pos(-d, 0, -d).tex(0, 0).color(r, g, b, a).endVertex();
        buffer.pos(-d, 0, d).tex(0, 1).color(r, g, b, a).endVertex();
        buffer.pos(d, 0, d).tex(1, 1).color(r, g, b, a).endVertex();
        buffer.pos(d, 0, -d).tex(1, 0).color(r, g, b, a).endVertex();

        tessellator.draw();
    }

}
