package mod.crystals.block;

import mod.crystals.api.seal.SealType;
import mod.crystals.init.CrystalsBlocks;
import mod.crystals.init.CrystalsRegistries;
import mod.crystals.tile.TileSeal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static java.lang.Math.abs;

public class BlockSeal extends BlockBase implements ITileEntityProvider {

    public static final int SEAL_RADIUS = 1;

    public BlockSeal() {
        super(Material.ROCK);
        setHardness(5);
        setResistance(10);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileSeal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this)
            .add(BlockDirectional.FACING)
            .build();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BlockDirectional.FACING).ordinal();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(BlockDirectional.FACING, facing);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos) {
        return null;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    protected boolean isFull(IBlockState state) {
        return false;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (SealType type : CrystalsRegistries.sealTypeRegistry) {
            items.add(createStack(type));
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileSeal te = (TileSeal) world.getTileEntity(pos);
        return createStack(te.getSealType());
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileSeal te = (TileSeal) world.getTileEntity(pos);
        drops.add(createStack(te.getSealType()));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        Vec3d front = new Vec3d(state.getValue(BlockDirectional.FACING).getDirectionVec());
        Vec3d off1 = new Vec3d(front.y, front.z, front.x).scale(SEAL_RADIUS);
        Vec3d off2 = new Vec3d(front.z, front.x, front.y).scale(SEAL_RADIUS);
        Vec3d off3 = off1.add(off2);
        Vec3d front1 = front.scale(15 / 16F);
        return FULL_BLOCK_AABB
            .grow(abs(off3.x), abs(off3.y), abs(off3.z))
            .contract(front1.x, front1.y, front1.z);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
        if (advanced != ITooltipFlag.TooltipFlags.ADVANCED) return;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) return;
        if (!tag.hasKey("type")) return;
        tooltip.add("Seal type: " + tag.getString("type"));
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
        return super.canPlaceBlockOnSide(world, pos, side) && BlockSealExt.canPlaceAt(world, pos, side, SEAL_RADIUS);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, world, pos, blockIn, fromPos);
        if (!BlockSealExt.checkIntegrity(world, pos)) {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
            return;
        }
        if (!BlockSealExt.checkForSupport(world, pos)) {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) return;
        if (!tag.hasKey("type")) return;
        SealType type = CrystalsRegistries.sealTypeRegistry.getValue(new ResourceLocation(tag.getString("type")));
        TileSeal te = (TileSeal) world.getTileEntity(pos);
        te.setSeal(type);
        BlockSealExt.placeSealExt(world, pos);

    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return false;
    }

    public static ItemStack createStack(SealType type) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("type", type.getRegistryName().toString());
        ItemStack stack = new ItemStack(CrystalsBlocks.seal);
        stack.setTagCompound(tag);
        return stack;
    }

}
