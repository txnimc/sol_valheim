package vice.sol_valheim.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vice.sol_valheim.accessors.FoodDataPlayerAccessor;
import vice.sol_valheim.accessors.PlayerEntityMixinDataAccessor;

@Mixin(FoodData.class)
public class FoodDataMixin implements FoodDataPlayerAccessor
{
    @Unique
    private Player sol_valheim$player;

    @Override
    public Player sol_valheim$getPlayer() { return sol_valheim$player;}

    @Override
    public void sol_valheim$setPlayer(Player player) { sol_valheim$player = player; }

    @Inject(at = @At("HEAD"), method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;)V")
    public void onEatFood(Item item, ItemStack stack, CallbackInfo ci)
    {
        if (sol_valheim$player == null)
        {
            System.out.println("sol_valheim$player is null in FoodData, this should never happen!");
            return;
        }

        var foodData = ((PlayerEntityMixinDataAccessor) sol_valheim$player).sol_valheim$getFoodData();
        if (foodData.canEat(item))
            foodData.eatItem(item);
    }
}