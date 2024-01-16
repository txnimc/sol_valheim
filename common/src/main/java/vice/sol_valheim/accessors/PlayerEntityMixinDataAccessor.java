package vice.sol_valheim;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import vice.sol_valheim.ValheimFoodData;

public interface PlayerEntityMixinDataAccessor
{
    ValheimFoodData sol_valheim$getFoodData();
}
