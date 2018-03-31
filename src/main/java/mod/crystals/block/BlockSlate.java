package mod.crystals.block;

import mod.crystals.api.NatureType;
import mod.crystals.init.CrystalsItems;
import mod.crystals.tile.TileSlate;
import mod.crystals.util.UnlistedPropertyInt;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nullable;

public class BlockSlate extends BlockBase implements ITileEntityProvider {

    public static final IProperty<Integer> DUST_COUNT = PropertyInteger.create("dusts", 0, 4);
    public static final IUnlistedProperty[] COLORS = {
            new UnlistedPropertyInt("color0"),
            new UnlistedPropertyInt("color1"),
            new UnlistedPropertyInt("color2"),
            new UnlistedPropertyInt("color3")
    };

    private static final AxisAlignedBB AABB = new AxisAlignedBB(0, 0, 0, 1, 0.5, 1);

    public BlockSlate() {
        super(Material.ROCK);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileSlate();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this)
                .add(DUST_COUNT)
                .add(COLORS)
                .build();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te == null || !(te instanceof TileSlate)) return state;
        return state.withProperty(DUST_COUNT, ((TileSlate) te).getDustCount());
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te == null || !(te instanceof TileSlate)) return state;
        int i = 0;
        for (NatureType type : ((TileSlate) te).getNatures()) {
            state = ((IExtendedBlockState) state).withProperty(COLORS[i++], type.getColor());
        }
        return state;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    @Override
    protected boolean isFull(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.isEmpty()) return false;

        TileSlate te = (TileSlate) world.getTileEntity(pos);

        if (stack.getItem() == CrystalsItems.dust) {
            boolean canPut = te.putDust(fromMeta(stack.getMetadata()));
            if (!world.isRemote && canPut && !player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
            return canPut;
        } else if (stack.getItem() == CrystalsItems.tuning_fork) {
            return te.tryForm();
        }

        return false;
    }

    private NatureType fromMeta(int metadata) {
        switch (metadata) {
            case 0:
                return NatureType.AIR;
            case 1:
                return NatureType.WATER;
            case 2:
                return NatureType.EARTH;
            case 3:
                return NatureType.FIRE;
            case 4:
                return NatureType.DISTORTED;
            case 5:
                return NatureType.VOID;
        }
        return null;
    }

}
