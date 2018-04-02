package mod.crystals.seal;

import mod.crystals.api.seal.ISeal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class SealBreathInstance extends AbstractSeal {

    private final float radius;

    public SealBreathInstance(ISeal seal, float radius) {
        super(seal);
        this.radius = radius;
    }

    public abstract void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz);

    public abstract void addEffect(EntityLivingBase entity);

    @Override
    public void update() {
        EnumFacing face = seal.getFace().getOpposite();
        AxisAlignedBB bounds = getAreaInFront(seal, radius, false);
        Vec3d center = new Vec3d(seal.getPos())
                .addVector(0.5, 0.5, 0.5)
                .add(new Vec3d(face.getDirectionVec()).scale(0.5));

        for (Entity entity : seal.getWorld().getEntitiesWithinAABBExcludingEntity(null, bounds)) {
            if (!(entity instanceof EntityLivingBase)) continue;
            Vec3d ePos = entity.getPositionEyes(0);
            if (ePos.squareDistanceTo(center) > radius * radius) continue;

            if (seal.getWorld().isRemote) {
                Vec3d dir = ePos.subtract(center).scale(0.05);
                Vec3d up = new Vec3d(0, 1, 0);
                Vec3d side = dir.normalize().crossProduct(up).normalize();

                for (int x = -3; x <= 3; x++) {
                    for (int y = -3; y <= 3; y++) {
                        if (x * x + y * y > 3 * 3) continue;
                        dir = ePos.add(side.scale(x / 6F).add(up.scale(y / 6F))).subtract(center).scale(0.1);
                        Vec3d pos = center.add(dir.scale(Math.random()))
                                .addVector((Math.random() - 0.5) * 0.125, (Math.random() - 0.5) * 0.125, (Math.random() - 0.5) * 0.125);
                        spawnParticle(seal.getWorld(), pos.x, pos.y, pos.z, dir.x, dir.y, dir.z);
                    }
                }
            }

            addEffect((EntityLivingBase) entity);
        }
    }

}
