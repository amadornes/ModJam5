package mod.crystals.client.render;

import mod.crystals.tile.TileCrystal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

public class FloatingCrystalRenderer extends TileEntitySpecialRenderer<TileCrystal> {

    @Override
    public void render(TileCrystal te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te.getBlockMetadata() != 0) return;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

        BlockPos pos = te.getPos();

        double time = te.getWorld().getTotalWorldTime() + partialTicks + (pos.getX() ^ pos.getY() ^ pos.getZ());
        float off = (int) (time % 80) / 80F;

        buffer.setTranslation(x - pos.getX(), y + 0.25 + 0.03125 * Math.sin(Math.PI * off * 2) - pos.getY(), z - pos.getZ());
        renderTileEntityFast(te, x, y, z, partialTicks, destroyStage, partialTicks, buffer);
        buffer.setTranslation(0, 0, 0);

        tessellator.draw();

        RenderHelper.enableStandardItemLighting();
    }

    @Override
    public void renderTileEntityFast(TileCrystal te, double x, double y, double z, float partialTicks, int destroyStage, float partial, BufferBuilder buffer) {
        BlockRendererDispatcher brd = Minecraft.getMinecraft().getBlockRendererDispatcher();

        IBlockState state = te.getWorld().getBlockState(te.getPos()).getActualState(te.getWorld(), te.getPos());
        IBakedModel model = brd.getModelForState(state);
        IBlockState eState = state.getBlock().getExtendedState(state, te.getWorld(), te.getPos());

        brd.getBlockModelRenderer().renderModel(te.getWorld(), model, eState, te.getPos(), buffer, false, 0);
    }
}
