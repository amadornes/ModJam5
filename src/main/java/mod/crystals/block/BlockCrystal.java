package mod.crystals.block;

import mod.crystals.api.IResonant;
import mod.crystals.client.particle.ParticleTestIGuess;
import mod.crystals.tile.TileCrystal;
import mod.crystals.util.UnlistedPropertyInt;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
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

public class BlockCrystal extends BlockBase implements ITileEntityProvider {

    private static final float DIAMETER = 1.5F / 16F;
    private static final float HEIGHT = 8.5F / 16F;
    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.5 - DIAMETER, 0, 0.5 - DIAMETER, 0.5 + DIAMETER, HEIGHT, 0.5 + DIAMETER);

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
            .add(COLOR)
            .build();
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
        return AABB;
    }

    @Override
    protected boolean isFull(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        IResonant resonant = te.getCapability(IResonant.CAPABILITY, null);
        System.out.println("Client? " + world.isRemote);
        System.out.println(" > " + resonant);

        if (world.isRemote) {
            // test code
            for (int i = 0; i < 50; i++) {
                float r1 = ((float) Math.random() - 0.5f) * 0.25f;
                float r2 = ((float) Math.random() - 0.5f) * 0.25f;
                float r3 = ((float) Math.random() - 0.5f) * 0.25f;
                ParticleTestIGuess.spawnParticleAt(world, pos.getX() + 0.5 + r1, pos.getY() + 1 + r2, pos.getZ() + 0.5 + r3, 1, 1, 1);
            }
        }
        return true;
    }

}
