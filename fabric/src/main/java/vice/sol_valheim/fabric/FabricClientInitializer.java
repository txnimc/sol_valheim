package vice.sol_valheim.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import vice.sol_valheim.SOLValheim;
import vice.sol_valheim.SOLValheimClient;

public class FabricClientInitializer implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        SOLValheimClient.init();
        ItemTooltipCallback.EVENT.register(SOLValheim::addTooltip);
    }
}