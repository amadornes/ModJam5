package mod.crystals.block;

import mod.crystals.api.IResonant;
import mod.crystals.tile.TileCrystal;
import mod.crystals.util.UnlistedPropertyInt;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nullable;

public class BlockCrystal extends BlockBase implements ITileEntityProvider {

    private static final float DIAMETER_GROUND = 2F / 16F;
    private static final float DIAMETER_FLOATING = 1.5F / 16F;
    private static final float HEIGHT = 8.5F / 16F;
    private static final AxisAlignedBB AABB_GROUND = new AxisAlignedBB(0.5 - DIAMETER_GROUND, 0, 0.5 - DIAMETER_GROUND, 0.5 + DIAMETER_GROUND, HEIGHT, 0.5 + DIAMETER_GROUND);
    private static final AxisAlignedBB AABB_FLOATING = new AxisAlignedBB(0.5 - DIAMETER_FLOATING, 0.5 - HEIGHT / 2F, 0.5 - DIAMETER_FLOATING, 0.5 + DIAMETER_FLOATING, 0.5 + HEIGHT / 2F, 0.5 + DIAMETER_FLOATING);

    public static final IProperty<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);
    public static final IUnlistedProperty<Integer> COLOR = new UnlistedPropertyInt("color");

    public BlockCrystal() {
        super(Material.GLASS);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileCrystal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this)
                .add(VARIANT)
                .add(COLOR)
                .build();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(VARIANT, Variant.VALUES[meta]);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te == null) return state;
        IResonant.Default resonant = (IResonant.Default) te.getCapability(IResonant.CAPABILITY, null);
        return ((IExtendedBlockState) state).withProperty(COLOR, resonant.getColor());
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(VARIANT)) {
            case GROUND:
                return AABB_GROUND;
            case FLOATING:
                return AABB_FLOATING;
        }
        return FULL_BLOCK_AABB;
    }

    @Override
    protected boolean isFull(IBlockState state) {
        return false;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        world.setTileEntity(pos, new TileCrystal(true));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileCrystal crystal = (TileCrystal) world.getTileEntity(pos);
        crystal.doJoin();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(VARIANT, placer.isSneaking() ? Variant.GROUND : Variant.FLOATING);
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        switch (state.getValue(VARIANT)) {
            case GROUND:
                return layer == BlockRenderLayer.SOLID;
            case FLOATING:
                return false;
        }
        return super.canRenderInLayer(state, layer);
    }

    public enum Variant implements IStringSerializable {
        FLOATING,
        GROUND;

        public static final Variant[] VALUES = values();

        @Override
        public String getName() {
            return name().toLowerCase();
        }

    }

}
