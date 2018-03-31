package mod.crystals.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface IEnvironmentScanner {

    void compute(World world, BlockPos pos, NatureType.Acceptor acceptor);

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Marker {
    }

}
