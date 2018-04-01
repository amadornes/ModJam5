package mod.crystals.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class TintWrapper extends WrappedModel {

    private final Map<EnumFacing, List<BakedQuad>> quads = new IdentityHashMap<>();

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
        for (BakedQuad quad : originalQuads) {
            if (quad.hasTintIndex()) {
                quads.add(quad);
            } else {
                quads.add(new BakedQuad(quad.getVertexData(), 0, quad.getFace(), quad.getSprite(),
                        quad.shouldApplyDiffuseLighting(), quad.getFormat()));
            }
        }
        return quads;
    }

}
