package mod.crystals.block;

import mod.crystals.api.seal.SealType;
import mod.crystals.init.CrystalsBlocks;
import mod.crystals.init.CrystalsRegistries;
import mod.crystals.tile.TileSeal;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockSeal extends BlockBase implements ITileEntityProvider {

    public BlockSeal() {
        super(Material.ROCK);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
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

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
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
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
        if (advanced != ITooltipFlag.TooltipFlags.ADVANCED) return;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) return;
        if (!tag.hasKey("type")) return;
        tooltip.add("Seal type: " + tag.getString("type"));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) return;
        if (!tag.hasKey("type")) return;
        SealType type = CrystalsRegistries.sealTypeRegistry.getValue(new ResourceLocation(tag.getString("type")));
        TileSeal te = (TileSeal) world.getTileEntity(pos);
        te.setSeal(type);
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
