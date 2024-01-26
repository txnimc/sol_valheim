package vice.sol_valheim.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vice.sol_valheim.SOLValheim;


@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = "sol_valheim", value = {Dist.CLIENT}, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEvents {
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onItemTooltip(ItemTooltipEvent event) {
        SOLValheim.addTooltip(event.getItemStack(), event.getFlags(), event.getToolTip());
    }

    @SubscribeEvent
    public static void onRenderGUI(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() == VanillaGuiOverlay.FOOD_LEVEL.type())
            event.setCanceled(true);
    }
}
