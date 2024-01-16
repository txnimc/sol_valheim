package vice.sol_valheim.mixin;
@Mixin(LocalPlayer.class)
public class LocalPlayerMixin
{
    @Inject(at = @At("HEAD"), method = "")
    public void render()
    {
        
    }
}