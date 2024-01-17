package vice.sol_valheim;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.platform.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import vice.sol_valheim.accessors.PlayerEntityMixinDataAccessor;

import java.util.stream.Stream;

public class FoodHUD implements ClientGuiEvent.RenderHud
{
    static Minecraft client;

    private static int HudHeight = 10;

    public FoodHUD() {
        ClientGuiEvent.RENDER_HUD.register(this);
        client = Minecraft.getInstance();
    }

    @Override
    public void renderHud(GuiGraphics graphics, float tickDelta) {
        if (client.player == null)
            return;

        var solPlayer = (PlayerEntityMixinDataAccessor) client.player;

        var foodData = solPlayer.sol_valheim$getFoodData();
        if (foodData == null)
            return;

        boolean useLargeIcons = SOLValheim.Config.client.useLargeIcons;

        int width = client.getWindow().getGuiScaledWidth() / 2 + 91;
        int height = client.getWindow().getGuiScaledHeight() - 39 - (useLargeIcons ? 6 : 0);

        int offset = 1;
        int size = useLargeIcons ? 14 : 9;

        for (var food : foodData.ItemEntries) {
            renderFoodSlot(graphics, food, width, size, offset, height, useLargeIcons);
            offset++;
        }

        if (foodData.DrinkSlot != null)
            renderFoodSlot(graphics, foodData.DrinkSlot, width, size, offset, height, useLargeIcons);
    }

    private static void renderFoodSlot(GuiGraphics graphics, ValheimFoodData.EatenFoodItem food, int width, int size, int offset, int height, boolean useLargeIcons)
    {
        var foodConfig = ModConfig.getFoodConfig(food.item);
        if (foodConfig == null)
            return;

        var isDrink = food.item.getDefaultInstance().getUseAnimation() == UseAnim.DRINK;
        int bgColor = isDrink ? FastColor.ARGB32.color(96, 52, 104, 163) : FastColor.ARGB32.color(96, 0, 0, 0);
        int yellow = FastColor.ARGB32.color(255, 255, 191, 0);

        int startWidth = width - (size * offset) - offset + 1;
        float ticksLeftPercent = Float.min(1.0F, (float) food.ticksLeft / foodConfig.getTime());
        int barHeight = Integer.max(1, (int)((size + 2f) * ticksLeftPercent));
        int barColor = ticksLeftPercent < SOLValheim.Config.common.eatAgainPercentage ?
                FastColor.ARGB32.color(180, 255, 10, 10) :
                FastColor.ARGB32.color(96, 0, 0, 0);

        graphics.fill(startWidth, height, startWidth + size, height + size, bgColor);
        graphics.fill(startWidth, Integer.max(height, height - barHeight + size), startWidth + size, height + size, barColor);

        graphics.pose().pushPose();
        var scale = useLargeIcons ? 0.75f : 0.5f;
        graphics.pose().scale(scale, scale, scale);
        graphics.pose().translate(startWidth * (useLargeIcons ? 0.3333f : 1f), height * (useLargeIcons ? 0.3333f : 1f), 0f);

        if (food.item == Items.CAKE && Platform.isModLoaded("farmersdelight"))
        {
            var cakeSlice = SOLValheim.ITEMS.getRegistrar().get(new ResourceLocation("farmersdelight:cake_slice"));
            graphics.renderItem(new ItemStack(cakeSlice == null ? food.item : cakeSlice, 1), startWidth + 1, height + 1);
        }
        else
        {
            graphics.renderItem(new ItemStack(food.item, 1), startWidth + 1, height + 1);
        }

        graphics.pose().pushPose();
        graphics.pose().translate(0.0f, 0.0f, 200.0f);

        var time = (float) food.ticksLeft / (20 * 60);
        var isSeconds = false;
        if (time < 1f)
        {
            isSeconds = true;
            time =  (float) food.ticksLeft / 20;
        }

        var minutes = String.format("%.0f", time);
        graphics.drawString(client.font, minutes, startWidth + (minutes.length() > 1 ? 6 : 12), height + 10, isSeconds ? FastColor.ARGB32.color(255, 237, 57, 57) : CommonColors.WHITE);

        if (!foodConfig.extraEffects.isEmpty())
            graphics.drawString(client.font, "+" + foodConfig.extraEffects.size(), startWidth + 6, height, yellow);

        graphics.pose().popPose();
        graphics.pose().popPose();
    }


}
