package mod.crystals.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.ref.WeakReference;

public abstract class SimpleManager {

    private final WeakReference<World> world;

    public SimpleManager(World world) {
        this.world = new WeakReference<>(world);
        MinecraftForge.EVENT_BUS.register(this);
    }

    protected World getWorld() {
        return world.get();
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (world.get() == null) {
            MinecraftForge.EVENT_BUS.unregister(this);
            return;
        }
        if (event.phase == TickEvent.Phase.START || event.world != world.get()) return;

        update(world.get());
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (world.get() == null) {
            MinecraftForge.EVENT_BUS.unregister(this);
            return;
        }
        if (event.phase == TickEvent.Phase.START) return;
        if (Minecraft.getMinecraft().isGamePaused()) return;

        update(world.get());
    }

    @SubscribeEvent
    public void onUnload(WorldEvent.Unload event) {
        if (event.getWorld() == world.get()) {
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }

    protected abstract void update(World world);

}
