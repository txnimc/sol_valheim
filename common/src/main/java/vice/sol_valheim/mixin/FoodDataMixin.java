package vice.sol_valheim.mixin;
@Mixin(FoodData.class)
public class FoodDataMixin
{
    @Inject(at = @At("HEAD"), method = "")
    public void render()
    {
        
    }
}