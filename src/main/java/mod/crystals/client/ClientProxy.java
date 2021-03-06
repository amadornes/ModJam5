package mod.crystals.client;

import mod.crystals.CommonProxy;
import mod.crystals.CrystalsMod;
import mod.crystals.api.IResonant;
import mod.crystals.block.BlockCrystalBase;
import mod.crystals.block.BlockSlate;
import mod.crystals.client.particle.ParticleCircle;
import mod.crystals.client.particle.ParticleRain;
import mod.crystals.client.particle.ParticleType;
import mod.crystals.client.particle.ParticleType.ParticleParams;
import mod.crystals.client.render.FloatingCrystalRenderer;
import mod.crystals.client.render.SealRenderer;
import mod.crystals.init.CrystalsBlocks;
import mod.crystals.init.CrystalsItems;
import mod.crystals.item.ItemDust;
import mod.crystals.tile.TileCrystal;
import mod.crystals.tile.TileSeal;
import mod.crystals.util.IBlockAdvancedOutline;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ClientProxy extends CommonProxy {
    private Minecraft mc;

    private Map<ParticleType, BiFunction<World, ParticleParams, Particle>> particleGenerators = new HashMap<>();

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        mc = Minecraft.getMinecraft();
        OBJLoader.INSTANCE.addDomain(CrystalsMod.MODID);

        registerParticle(ParticleType.RAIN, (world, p) -> new ParticleRain(world, p.position.x, p.position.y, p.position.z, p.velocity.x, p.velocity.y, p.velocity.z));
        registerParticle(ParticleType.CIRCLE, (world, p) -> new ParticleCircle(world, p.position.x, p.position.y, p.position.z, p.velocity.x, p.velocity.y, p.velocity.z, p.color.getRed() / 255f, p.color.getGreen() / 255f, p.color.getBlue() / 255f));
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);

        BlockColors blockColors = mc.getBlockColors();
        blockColors.registerBlockColorHandler((state, world, pos, index) -> {
            if (index == 1) return 0xFFFFFF;
            Integer color = ((IExtendedBlockState) state).getValue(BlockCrystalBase.COLOR);
            if (color == null) return 0x000000;
            return color;
        }, CrystalsBlocks.crystal);
        blockColors.registerBlockColorHandler((state, world, pos, index) -> {
            if (index < 0 || index >= 4) return 0x000000;
            Integer color = (Integer) ((IExtendedBlockState) state).getValue(BlockSlate.COLORS[index]);
            if (color == null) return 0x000000;
            return color;
        }, CrystalsBlocks.slate);

        ItemColors itemColors = mc.getItemColors();
        IResonant.Default tintResonant = (IResonant.Default) IResonant.CAPABILITY.getDefaultInstance();
        itemColors.registerItemColorHandler((stack, index) -> {
            if (!stack.hasTagCompound()) return 0xFFFFFF;
            IResonant.CAPABILITY.readNBT(tintResonant, null, stack.getSubCompound("BlockEntityTag").getCompoundTag("rd"));
            return tintResonant.getColor();
        }, CrystalsBlocks.crystal);
        itemColors.registerItemColorHandler((stack, index) -> ItemDust.getType(stack).getColor(), CrystalsItems.dust);

        ClientRegistry.bindTileEntitySpecialRenderer(TileCrystal.class, new FloatingCrystalRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileSeal.class, new SealRenderer());
    }

    @Override
    public <T extends ParticleParams> void spawnParticle(@Nonnull World world, @Nonnull ParticleType<T> type, @Nonnull T params) {
        super.spawnParticle(world, type, params);
        float chance = (3 - Minecraft.getMinecraft().gameSettings.particleSetting) / 3F;
        if (world.rand.nextFloat() > chance) return;
        Optional.ofNullable(particleGenerators.get(type))
            .map(it -> it.apply(world, params))
            .ifPresent(mc.effectRenderer::addEffect);
    }

    @SubscribeEvent
    public void onModelRegister(ModelRegistryEvent event) {
        addModel(CrystalsBlocks.crystal, 0, "inventory");
        addModel(CrystalsBlocks.crystal_creative, 0, "inventory");
        addModel(CrystalsBlocks.slate, 0, "inventory");
        addModel(CrystalsBlocks.post, 0, "inventory");
        addModel(CrystalsBlocks.seal, 0, "inventory");
        addItemModel(CrystalsItems.tuning_fork, 0, "tuning_fork");
        addItemModel(CrystalsItems.tuning_fork, 1, "tuning_fork_vibrating");
        addModel(CrystalsItems.dust, 0, "inventory");
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        wrap(event, new ModelResourceLocation(CrystalsBlocks.crystal.getRegistryName(), "variant=floating"), TintWrapper::new);
        wrap(event, new ModelResourceLocation(CrystalsBlocks.crystal.getRegistryName(), "variant=down"), TintWrapper::new);
        wrap(event, new ModelResourceLocation(CrystalsBlocks.crystal.getRegistryName(), "variant=up"), TintWrapper::new);
        wrap(event, new ModelResourceLocation(CrystalsBlocks.crystal.getRegistryName(), "variant=north"), TintWrapper::new);
        wrap(event, new ModelResourceLocation(CrystalsBlocks.crystal.getRegistryName(), "variant=south"), TintWrapper::new);
        wrap(event, new ModelResourceLocation(CrystalsBlocks.crystal.getRegistryName(), "variant=west"), TintWrapper::new);
        wrap(event, new ModelResourceLocation(CrystalsBlocks.crystal.getRegistryName(), "variant=east"), TintWrapper::new);
        wrap(event, new ModelResourceLocation(CrystalsBlocks.crystal.getRegistryName(), "inventory"), TintWrapper::new);
    }

    private void wrap(ModelBakeEvent event, ModelResourceLocation name, Function<IBakedModel, ? extends IBakedModel> wrapper) {
        IBakedModel model = event.getModelRegistry().getObject(name);
        if (model == null) return;
        event.getModelRegistry().putObject(name, wrapper.apply(model));
    }

    private void addModel(Block block, int meta, String name) {
        addModel(Item.getItemFromBlock(block), meta, name);
    }

    private void addModel(Item item, int meta, String name) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), name));
    }

    private void addItemModel(Item item, int meta, String name) {
        ResourceLocation res = new ResourceLocation(item.getRegistryName().getResourceDomain(), name);
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(res, "inventory"));
    }

    @SubscribeEvent
    public void drawBlockOutline(DrawBlockHighlightEvent e) {
        BlockPos pos = e.getTarget().getBlockPos();
        if (pos == null) return;
        World world = e.getPlayer().world;
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof IBlockAdvancedOutline) {
            ((IBlockAdvancedOutline) block).getOutlineBoxes(world, pos, state).stream()
                .map(it -> it.offset(pos))
                .forEach(it -> drawSelectionBox(world, e.getPlayer(), pos, it, e.getPartialTicks()));
            e.setCanceled(true);
        }
    }

    private void drawSelectionBox(World world, EntityPlayer player, BlockPos pos, AxisAlignedBB aabb, float partialTicks) {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        IBlockState iblockstate = world.getBlockState(pos);

        if (iblockstate.getMaterial() != Material.AIR && world.getWorldBorder().contains(pos)) {
            double d3 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
            double d4 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
            double d5 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
            RenderGlobal.drawSelectionBoundingBox(aabb.grow(0.0020000000949949026D).offset(-d3, -d4, -d5), 0.0F, 0.0F, 0.0F, 0.4F);
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    @SuppressWarnings("unchecked")
    private <T extends ParticleParams> void registerParticle(ParticleType<T> type, BiFunction<World, T, Particle> generator) {
        particleGenerators.put(type, (BiFunction<World, ParticleParams, Particle>) generator);
    }

}
