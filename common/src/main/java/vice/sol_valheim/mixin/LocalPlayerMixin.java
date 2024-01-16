package vice.sol_valheim.mixin;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vice.sol_valheim.SOLValheim;
import vice.sol_valheim.accessors.PlayerEntityMixinDataAccessor;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin
{
    @Inject(at = @At("HEAD"), method = "hasEnoughFoodToStartSprinting", cancellable = true)
    public void canStartSprinting(CallbackInfoReturnable<Boolean> cir)
    {
        var solPlayer = (PlayerEntityMixinDataAccessor) this;
        if (((LocalPlayer) (Object) this).tickCount < SOLValheim.Config.common.respawnGracePeriod * 20)
        {
            cir.setReturnValue(true);
            cir.cancel();
            return;
        }

        var foodData = solPlayer.sol_valheim$getFoodData();
        if (foodData == null || foodData.ItemEntries.isEmpty())
        {
            cir.setReturnValue(false);
            cir.cancel();
            return;
        }

        cir.setReturnValue(true);
        cir.cancel();
    }
}