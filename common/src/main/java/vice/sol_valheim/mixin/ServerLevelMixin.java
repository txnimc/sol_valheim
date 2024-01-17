package vice.sol_valheim.mixin;

import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vice.sol_valheim.SOLValheim;
import vice.sol_valheim.accessors.PlayerEntityMixinDataAccessor;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public class ServerLevelMixin
{
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setDayTime(J)V"), method = "tick")
    public void onSleep(BooleanSupplier hasTimeLeft, CallbackInfo ci)
    {
        if (!SOLValheim.Config.common.passTicksDuringNight)
            return;

        var level = (ServerLevel) (Object) this;
        var dayTime = level.getLevelData().getDayTime();

        var l = dayTime + 24000L;
        var newTime = l - l % 24000L;

        var passedTicks = Math.max(0, newTime - dayTime);
        if (passedTicks == 0)
            return;

        for (var player : level.players()) {
            var foodData = ((PlayerEntityMixinDataAccessor) player).sol_valheim$getFoodData();
            if (foodData.DrinkSlot != null) {
                foodData.DrinkSlot.ticksLeft = (int) Math.max(1200, foodData.DrinkSlot.ticksLeft - passedTicks);
            }
            for (var item : foodData.ItemEntries)
            {
                item.ticksLeft = (int) Math.max(1200, item.ticksLeft - passedTicks);
            }
        }
    }
}