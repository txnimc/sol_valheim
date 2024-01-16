package vice.sol_valheim.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import vice.sol_valheim.SOLValheimClient;

@Mod.EventBusSubscriber(modid = "sol_valheim", value = {Dist.CLIENT}, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeClientInitializer {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        SOLValheimClient.init();
    }
}