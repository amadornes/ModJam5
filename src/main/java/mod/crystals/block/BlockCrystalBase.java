package mod.crystals.block;

import mod.crystals.api.ILaserRayTrace;
import mod.crystals.api.IResonant;
import mod.crystals.util.UnlistedPropertyInt;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public abstract class BlockCrystalBase extends BlockBase implements ITileEntityProvider, ILaserRayTrace {

    public static final IUnlistedProperty<Integer> COLOR = new UnlistedPropertyInt("color");

    public BlockCrystalBase() {
        super(Material.GLASS);
    }

    @Override
    protected abstract BlockStateContainer createBlockState();

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te == null) return state;
        IResonant.Default resonant = (IResonant.Default) te.getCapability(IResonant.CAPABILITY, null);
        return ((IExtendedBlockState) state).withProperty(COLOR, resonant.getColor());
    }

    @Override
    public abstract void onBlockAdded(World world, BlockPos pos, IBlockState state);

    @Override
    protected boolean isFull(IBlockState state) {
        return false;
    }

}
