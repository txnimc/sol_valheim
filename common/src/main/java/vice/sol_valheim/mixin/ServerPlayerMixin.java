package vice.sol_valheim.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.UseAnim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vice.sol_valheim.accessors.PlayerEntityMixinDataAccessor;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin
{
    @Inject(at = @At("HEAD"), method = {"completeUsingItem"})
    public void render(CallbackInfo ci)
    {
        var player = (ServerPlayer) (Object) this;
        var useItem = player.getUseItem();
        if (!useItem.isEmpty() && player.isUsingItem() && useItem.getUseAnimation() == UseAnim.DRINK)
        {
            ((PlayerEntityMixinDataAccessor) player).sol_valheim$getFoodData().eatItem(useItem.getItem());
        }
    }
}