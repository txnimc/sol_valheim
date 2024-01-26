package vice.sol_valheim.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vice.sol_valheim.SOLValheim;
import vice.sol_valheim.accessors.PlayerEntityMixinDataAccessor;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin
{

    #if PRE_CURRENT_MC_1_19_2
    @ModifyVariable(at = @At("STORE"), method = "aiStep", ordinal = 4)
    public boolean canStartSprinting(boolean bool)
    {
        var solPlayer = (PlayerEntityMixinDataAccessor) this;
        var mayFly = ((LocalPlayer) (Object) this).getAbilities().mayfly;

        if (mayFly || ((LocalPlayer) (Object) this).tickCount < SOLValheim.Config.common.respawnGracePeriod * 20)
        {
            return true;
        }

        var foodData = solPlayer.sol_valheim$getFoodData();
        if (foodData == null || foodData.ItemEntries.isEmpty())
        {
            return false;
        }

        return bool;
    }

    #elif POST_CURRENT_MC_1_20_1
    @Inject(at = @At("HEAD"), method = "hasEnoughFoodToStartSprinting", cancellable = true)
    public void canStartSprinting(CallbackInfoReturnable<Boolean> cir)
    {
        var solPlayer = (PlayerEntityMixinDataAccessor) this;
        var mayFly = ((LocalPlayer) (Object) this).getAbilities().mayfly;

        if (mayFly || ((LocalPlayer) (Object) this).tickCount < SOLValheim.Config.common.respawnGracePeriod * 20)
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
    #endif

}