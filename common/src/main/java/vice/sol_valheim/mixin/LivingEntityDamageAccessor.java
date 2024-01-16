package vice.sol_valheim.accessors;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityDamageAccessor
{
    @Accessor("lastDamageStamp")
    long getLastDamageStamp();
}
