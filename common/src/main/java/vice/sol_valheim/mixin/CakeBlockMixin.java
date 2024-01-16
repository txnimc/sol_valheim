package vice.sol_valheim.mixin;
@Mixin(CakeBlock.class)
public class CakeBlockMixin
{
    @Inject(at = @At("HEAD"), method = "")
    public void render()
    {
        
    }
}