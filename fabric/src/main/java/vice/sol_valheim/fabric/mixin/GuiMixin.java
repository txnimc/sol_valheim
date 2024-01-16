package vice.sol_valheim.fabric.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Gui.class)
public abstract class GuiMixin
{
    @Shadow
    protected abstract int getVehicleMaxHearts(LivingEntity livingEntity);

    @Redirect(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;getVehicleMaxHearts(Lnet/minecraft/world/entity/LivingEntity;)I"))
    private int sol_valheim$getVehicleMaxHearts(Gui instance, LivingEntity vehicle)
    {
         var original = getVehicleMaxHearts(vehicle);
         if (original > 0)
             return original;

         return -1;
    }
}

