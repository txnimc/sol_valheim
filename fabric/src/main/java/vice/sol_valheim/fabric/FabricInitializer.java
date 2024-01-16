package vice.sol_valheim.fabric;

import vice.sol_valheim.SOLValheim;
import net.fabricmc.api.ModInitializer;

public class FabricInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        SOLValheim.init();
    }
}