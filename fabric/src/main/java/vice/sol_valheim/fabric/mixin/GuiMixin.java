package vice.sol_valheim.fabric.mixin;
@Mixin(Gui.class)
public class GuiMixin
{
    @Inject(at = @At("HEAD"), method = "")
    public void render()
    {
        
    }
}