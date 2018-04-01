package mod.crystals.seal;

import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISeal;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.api.seal.SealType;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SealHarvest extends SealType {

    private static List<Pair<BlockMatcher, HarvestOp>> harvestLogic = new ArrayList<>();

    @Override
    public Ingredient[][] createRecipe() {
        Ingredient i = new Ingredient(NatureType.DISTORTED, NatureType.EARTH); // TODO: sensible recipe
        return new Ingredient[][]{
            {i, i, i},
            {i, i, i},
            {i, i, i}
        };
    }

    @Override
    public ISealInstance instantiate(ISeal seal) {
        return new Instance(seal);
    }

    private static class Instance implements ISealInstance {

        private final ISeal seal;

        private int cooldown = 80; // prevent exploiting it

        public Instance(ISeal seal) {this.seal = seal;}

        @Override
        public void update() {
            World world = seal.getWorld();
            if (world.isRemote) return;
            if (cooldown > 0) {
                cooldown--;
            } else {
                BlockPos pos = seal.getPos();
                BlockPos center = pos.add(new BlockPos(new Vec3d(seal.getFace().getDirectionVec()).scale(3)));
                outer: for (BlockPos it : BlockPos.getAllInBox(center.add(-2, -1, -2), center.add(2, 1, 2))) {
                    IBlockState state = world.getBlockState(it);
                    for (Pair<BlockMatcher, HarvestOp> pair : harvestLogic) {
                        if (pair.getLeft().matches(world, state, it)) {
                            pair.getRight().harvest(world, state, it);
                            break outer;
                        }
                    }
                }
                cooldown = 80;
            }
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound tag) {
            tag.setByte("c", (byte) cooldown);
            return tag;
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            cooldown = tag.getByte("c") & 0xFF;
        }

    }

    @FunctionalInterface
    public interface BlockMatcher {
        public boolean matches(World world, IBlockState state, BlockPos pos);
    }

    @FunctionalInterface
    public interface HarvestOp {
        public boolean harvest(World world, IBlockState state, BlockPos pos);
    }

    // API \o/
    public static void addHarvestOverride(BlockMatcher matcher, HarvestOp op) {
        harvestLogic.add(0, Pair.of(matcher, op));
    }

    static {
        // generic harvest code
        addHarvestOverride(
            (world, state, pos) -> {
                Block block = state.getBlock();
                return block instanceof IGrowable && !((IGrowable) block).canGrow(world, pos, state, world.isRemote);
            },
            (world, state, pos) -> {
                Block block = state.getBlock();
                ItemStack item = block.getPickBlock(state, null, world, pos, null);
                NonNullList<ItemStack> items = NonNullList.create();
                block.getDrops(items, world, pos, state, 0);
                Optional<ItemStack> seedItem = items.stream().filter(it1 -> ItemStack.areItemsEqual(it1, item)).findAny();
                world.setBlockState(pos, state.getBlock().getDefaultState());
                seedItem.ifPresent(it -> it.shrink(1));
                items.forEach(it -> Block.spawnAsEntity(world, pos, it));
                return true;
            }
        );

        // pumpkins & melons
        addHarvestOverride(
            (world, state, pos) -> {
                Block block = state.getBlock();
                return block instanceof IGrowable && block instanceof BlockStem;
            },
            (world, state, pos) -> false
        );

        addHarvestOverride(
            (world, state, pos) -> {
                Block block = state.getBlock();
                return block instanceof BlockMelon || block instanceof BlockPumpkin;
            },
            (world, state, pos) -> {
                state.getBlock().dropBlockAsItem(world, pos, state, 0);
                world.setBlockToAir(pos);
                return true;
            }
        );
    }

}
