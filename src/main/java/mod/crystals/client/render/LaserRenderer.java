package mod.crystals.client.render;

import mod.crystals.CrystalsMod;
import mod.crystals.capability.CapabilityRayManager;
import mod.crystals.util.ray.Ray;
import mod.crystals.util.ray.RayManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(modid = CrystalsMod.MODID, value = Side.CLIENT)
public class LaserRenderer {

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        World world = Minecraft.getMinecraft().world;
        EntityPlayer player = Minecraft.getMinecraft().player;

        RayManager manager = world.getCapability(CapabilityRayManager.CAPABILITY, null);
        Vec3d look = player.getPositionEyes(event.getPartialTicks());

        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(CrystalsMod.MODID, "textures/misc/laser_transparent.png"));
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        renderLasers(event, manager, look, false);

        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(CrystalsMod.MODID, "textures/misc/laser.png"));
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        renderLasers(event, manager, look, true);

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.depthMask(true);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
    }

    private static void renderLasers(RenderWorldLastEvent event, RayManager manager, Vec3d look, boolean renderShimmer) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.setTranslation(-TileEntityRendererDispatcher.staticPlayerX, -TileEntityRendererDispatcher.staticPlayerY, -TileEntityRendererDispatcher.staticPlayerZ);
        for (Ray ray : manager.getRays()) {
            if (!ray.hasLineOfSight()) continue;

            Vec3d start = ray.getStart(event.getPartialTicks());
            Vec3d end = ray.getEnd(event.getPartialTicks());
            Vec3d startColor = ray.getStartColor(event.getPartialTicks());
            Vec3d endColor = ray.getEndColor(event.getPartialTicks());

            drawLaser(buffer, start, end, look, startColor, endColor, renderShimmer);
        }
        buffer.setTranslation(0, 0, 0);
        tessellator.draw();
    }

    private static void drawLaser(BufferBuilder buffer, Vec3d start, Vec3d end, Vec3d playerHead, Vec3d startColor, Vec3d endColor, boolean renderShimmer) {
        Vec3d dir = end.subtract(start).normalize();
        Vec3d startUp = dir.crossProduct(start.subtract(playerHead)).normalize().scale(0.0625);
        Vec3d endUp = dir.crossProduct(end.subtract(playerHead)).normalize().scale(0.0625);

        Vec3d ext1 = dir.scale(-startUp.lengthVector());
        Vec3d ext2 = dir.scale(endUp.lengthVector());

        if (renderShimmer) {
            buffer.pos(start.x + startUp.x, start.y + startUp.y, start.z + startUp.z)
                .tex(0, 0).color((float) startColor.x, (float) startColor.y, (float) startColor.z, 0.5F).endVertex();
            buffer.pos(start.x - startUp.x, start.y - startUp.y, start.z - startUp.z)
                .tex(0, 0.5).color((float) startColor.x, (float) startColor.y, (float) startColor.z, 0.5F).endVertex();
            buffer.pos(end.x - endUp.x, end.y - endUp.y, end.z - endUp.z)
                .tex(0.5, 0.5).color((float) endColor.x, (float) endColor.y, (float) endColor.z, 0.5F).endVertex();
            buffer.pos(end.x + endUp.x, end.y + endUp.y, end.z + endUp.z)
                .tex(0.5, 0).color((float) endColor.x, (float) endColor.y, (float) endColor.z, 0.5F).endVertex();

            buffer.pos(start.x + startUp.x, start.y + startUp.y, start.z + startUp.z).tex(0, 0.5).color(1, 1, 1, 0.2F).endVertex();
            buffer.pos(start.x - startUp.x, start.y - startUp.y, start.z - startUp.z).tex(0, 1).color(1, 1, 1, 0.2F).endVertex();
            buffer.pos(end.x - endUp.x, end.y - endUp.y, end.z - endUp.z).tex(0.5, 1).color(1, 1, 1, 0.2F).endVertex();
            buffer.pos(end.x + endUp.x, end.y + endUp.y, end.z + endUp.z).tex(0.5, 0.5).color(1, 1, 1, 0.2F).endVertex();

//            // first end
//            buffer.pos(start.x + startUp.x + ext1.x, start.y + startUp.y + ext1.y, start.z + startUp.z + ext1.z)
//                .tex(1, 0).color((float) startColor.x, (float) startColor.y, (float) startColor.z, 0.5F).endVertex();
//            buffer.pos(start.x - startUp.x + ext1.x, start.y - startUp.y + ext1.y, start.z - startUp.z + ext1.z)
//                .tex(0.5, 0).color((float) startColor.x, (float) startColor.y, (float) startColor.z, 0.5F).endVertex();
//            buffer.pos(start.x - startUp.x, start.y - startUp.y, start.z - startUp.z)
//                .tex(0.5, 0.25).color((float) startColor.x, (float) startColor.y, (float) startColor.z, 0.5F).endVertex();
//            buffer.pos(start.x + startUp.x, start.y + startUp.y, start.z + startUp.z)
//                .tex(1, 0.25).color((float) startColor.x, (float) startColor.y, (float) startColor.z, 0.5F).endVertex();
//
//            buffer.pos(start.x + startUp.x + ext1.x, start.y + startUp.y + ext1.y, start.z + startUp.z + ext1.z).tex(1, 0.5).color(1, 1, 1, 0.2F).endVertex();
//            buffer.pos(start.x - startUp.x + ext1.x, start.y - startUp.y + ext1.y, start.z - startUp.z + ext1.z).tex(0.5, 0.5).color(1, 1, 1, 0.2F).endVertex();
//            buffer.pos(start.x - startUp.x, start.y - startUp.y, start.z - startUp.z).tex(0.5, 0.75).color(1, 1, 1, 0.2F).endVertex();
//            buffer.pos(start.x + startUp.x, start.y + startUp.y, start.z + startUp.z).tex(1, 0.75).color(1, 1, 1, 0.2F).endVertex();
//
//            // second end
//            buffer.pos(end.x + endUp.x, end.y + endUp.y, end.z + endUp.z)
//                .tex(1, 0.25).color((float) endColor.x, (float) endColor.y, (float) endColor.z, 0.5F).endVertex();
//            buffer.pos(end.x - endUp.x, end.y - endUp.y, end.z - endUp.z)
//                .tex(0.5, 0.25).color((float) endColor.x, (float) endColor.y, (float) endColor.z, 0.5F).endVertex();
//            buffer.pos(end.x - endUp.x + ext2.x, end.y - endUp.y + ext2.y, end.z - endUp.z + ext2.z)
//                .tex(0.5, 0).color((float) endColor.x, (float) endColor.y, (float) endColor.z, 0.5F).endVertex();
//            buffer.pos(end.x + endUp.x + ext2.x, end.y + endUp.y + ext2.y, end.z + endUp.z + ext2.z)
//                .tex(1, 0).color((float) endColor.x, (float) endColor.y, (float) endColor.z, 0.5F).endVertex();
//
//            buffer.pos(end.x + endUp.x, end.y + endUp.y, end.z + endUp.z).tex(1, 0.75).color(1, 1, 1, 0.2F).endVertex();
//            buffer.pos(end.x - endUp.x, end.y - endUp.y, end.z - endUp.z).tex(0.5, 0.75).color(1, 1, 1, 0.2F).endVertex();
//            buffer.pos(end.x - endUp.x + ext2.x, end.y - endUp.y + ext2.y, end.z - endUp.z + ext2.z).tex(0.5, 0.5).color(1, 1, 1, 0.2F).endVertex();
//            buffer.pos(end.x + endUp.x + ext2.x, end.y + endUp.y + ext2.y, end.z + endUp.z + ext2.z).tex(1, 0.5).color(1, 1, 1, 0.2F).endVertex();


        } else {
            buffer.pos(start.x + startUp.x, start.y + startUp.y, start.z + startUp.z)
                .tex(0, 0.5).color((float) startColor.x, (float) startColor.y, (float) startColor.z, 0.5F).endVertex();
            buffer.pos(start.x - startUp.x, start.y - startUp.y, start.z - startUp.z)
                .tex(0, 1).color((float) startColor.x, (float) startColor.y, (float) startColor.z, 0.5F).endVertex();
            buffer.pos(end.x - endUp.x, end.y - endUp.y, end.z - endUp.z)
                .tex(0.5, 1).color((float) endColor.x, (float) endColor.y, (float) endColor.z, 0.5F).endVertex();
            buffer.pos(end.x + endUp.x, end.y + endUp.y, end.z + endUp.z)
                .tex(0.5, 0.5).color((float) endColor.x, (float) endColor.y, (float) endColor.z, 0.5F).endVertex();

//            // first end
//            buffer.pos(start.x + startUp.x + ext1.x, start.y + startUp.y + ext1.y, start.z + startUp.z + ext1.z).tex(1, 0.5).color((float) startColor.x, (float) startColor.y, (float) startColor.z, 0.5F).endVertex();
//            buffer.pos(start.x - startUp.x + ext1.x, start.y - startUp.y + ext1.y, start.z - startUp.z + ext1.z).tex(0.5, 0.5).color((float) startColor.x, (float) startColor.y, (float) startColor.z, 0.5F).endVertex();
//            buffer.pos(start.x - startUp.x, start.y - startUp.y, start.z - startUp.z).tex(0.5, 0.75).color((float) startColor.x, (float) startColor.y, (float) startColor.z, 0.5F).endVertex();
//            buffer.pos(start.x + startUp.x, start.y + startUp.y, start.z + startUp.z).tex(1, 0.75).color((float) startColor.x, (float) startColor.y, (float) startColor.z, 0.5F).endVertex();
//
//            // second end
//            buffer.pos(end.x + endUp.x, end.y + endUp.y, end.z + endUp.z).tex(1, 0.75).color((float) endColor.x, (float) endColor.y, (float) endColor.z, 0.5F).endVertex();
//            buffer.pos(end.x - endUp.x, end.y - endUp.y, end.z - endUp.z).tex(0.5, 0.75).color((float) endColor.x, (float) endColor.y, (float) endColor.z, 0.5F).endVertex();
//            buffer.pos(end.x - endUp.x + ext2.x, end.y - endUp.y + ext2.y, end.z - endUp.z + ext2.z).tex(0.5, 0.5).color((float) endColor.x, (float) endColor.y, (float) endColor.z, 0.5F).endVertex();
//            buffer.pos(end.x + endUp.x + ext2.x, end.y + endUp.y + ext2.y, end.z + endUp.z + ext2.z).tex(1, 0.5).color((float) endColor.x, (float) endColor.y, (float) endColor.z, 0.5F).endVertex();
        }
    }

}
