package vice.sol_valheim.forge;

import dev.architectury.platform.forge.EventBuses;
import vice.sol_valheim.SOLValheim;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SOLValheim.MOD_ID)
public class ForgeInitializer
{
    public ForgeInitializer() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(SOLValheim.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        SOLValheim.init();
    }
}