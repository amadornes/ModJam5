package mod.crystals.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class TintWrapper extends WrappedModel {

    private final Map<EnumFacing, List<BakedQuad>> quads = new IdentityHashMap<>();
    private final ItemOverrideList overrides = new ItemOverrideList(Collections.emptyList()) {
        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
            return TintWrapper.this;
        }
    };

    public TintWrapper(IBakedModel parent) {
        super(parent);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return quads.computeIfAbsent(side, s -> genQuads(state, side, rand));
    }

    private List<BakedQuad> genQuads(IBlockState state, EnumFacing side, long rand) {
        List<BakedQuad> originalQuads = super.getQuads(state, side, rand);
        List<BakedQuad> quads = new ArrayList<>();
        GlowPipeline pipeline = new GlowPipeline();
        for (BakedQuad quad : originalQuads) {
            if (quad.hasTintIndex()) {
                quads.add(quad);
            } else {
                VertexFormat format = new VertexFormat(quad.getFormat()).addElement(DefaultVertexFormats.TEX_2S);
                UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
                pipeline.setParent(builder);
                quad.pipe(pipeline);
                pipeline.setQuadTint(0);
                quads.add(builder.build());
            }
        }
        return quads;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return overrides;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        Pair<? extends IBakedModel, Matrix4f> pair = super.handlePerspective(cameraTransformType);
        if (pair != null) {
            return Pair.of(this, pair.getValue());
        }
        return null;
    }

    private static class GlowPipeline implements IVertexConsumer {

        private IVertexConsumer parent;

        public void setParent(IVertexConsumer parent) {
            this.parent = parent;
        }

        @Override
        public VertexFormat getVertexFormat() {
            return parent.getVertexFormat();
        }

        @Override
        public void setQuadTint(int tint) {
            parent.setQuadTint(tint);
        }

        @Override
        public void setQuadOrientation(EnumFacing orientation) {
            parent.setQuadOrientation(orientation);
        }

        @Override
        public void setApplyDiffuseLighting(boolean diffuse) {
            parent.setApplyDiffuseLighting(diffuse);
        }

        @Override
        public void setTexture(TextureAtlasSprite texture) {
            parent.setTexture(texture);
        }

        @Override
        public void put(int element, float... data) {
            VertexFormatElement e = parent.getVertexFormat().getElement(element);
            if (e == DefaultVertexFormats.TEX_2S) {
                parent.put(element, 15 * 32.0f / 0xffff, 15 * 32.0f / 0xffff);
                return;
            }
            parent.put(element, data);
        }

    }

}
