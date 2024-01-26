package vice.sol_valheim;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.ChatFormatting;

#if PRE_CURRENT_MC_1_19_2
import dev.architectury.registry.registries.Registries;
import net.minecraft.core.Registry;
#elif POST_CURRENT_MC_1_20_1
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
#endif

import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

import java.util.List;

public class SOLValheim
{


	#if PRE_CURRENT_MC_1_19_2
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create("sol_valheim", Registry.ITEM_REGISTRY);
	public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create("sol_valheim", Registry.MOB_EFFECT_REGISTRY);
	#elif POST_CURRENT_MC_1_20_1
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create("sol_valheim", Registries.ITEM);
	public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create("sol_valheim", Registries.MOB_EFFECT);
    #endif


	public static ModConfig Config;
	public static final String MOD_ID = "sol_valheim";


	private static AttributeModifier speedBuff;
	public static AttributeModifier getSpeedBuffModifier() {
		if (speedBuff == null)
			speedBuff = new AttributeModifier("sol_valheim_speed_buff", Config.common.speedBoost, AttributeModifier.Operation.MULTIPLY_BASE);

		return speedBuff;
	}


	public static void init() {
		EntityDataSerializers.registerSerializer(ValheimFoodData.FOOD_DATA_SERIALIZER);

		AutoConfig.register(ModConfig.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
		Config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

		if (Config.common.foodConfigs.isEmpty())
		{
			System.out.println("Generating default food configs, this might take a second.");
			long startTime = System.nanoTime();

			#if PRE_CURRENT_MC_1_19_2
			Registry.ITEM.forEach(ModConfig::getFoodConfig);
			#elif POST_CURRENT_MC_1_20_1
			BuiltInRegistries.ITEM.forEach(ModConfig::getFoodConfig);
			#endif


			AutoConfig.getConfigHolder(ModConfig.class).save();

			long endTime = System.nanoTime();
			long executionTime = (endTime - startTime) / 1000000;
			System.out.println("Generating default food configs took " + executionTime + "ms.");
		}

//
//		try	{
//			var field = FoodProperties.class.getDeclaredField("canAlwaysEat");
//			field.setBoolean(Items.ROTTEN_FLESH.getFoodProperties(), true);
//		}
//		catch (Exception e) {
//			System.out.println(e);
//		}
	}



	public static void addTooltip(ItemStack item, TooltipFlag flag, List<Component> list)
	{
		var food = item.getItem();
		if (food == Items.ROTTEN_FLESH) {
			list.add(Component.literal("☠ Empties Your Stomach!").withStyle(ChatFormatting.GREEN));
			return;
		}

		var config = ModConfig.getFoodConfig(food);
		if (config == null)
			return;

		var hearts = config.getHearts() % 2 == 0 ? config.getHearts() / 2 : String.format("%.1f", (float) config.getHearts() / 2f);
		list.add(Component.literal("❤ " + hearts + " Heart" + (config.getHearts() / 2f > 1 ? "s" : "")).withStyle(ChatFormatting.RED));
		list.add(Component.literal("☀ " + String.format("%.1f", config.getHealthRegen()) + " Regen").withStyle(ChatFormatting.DARK_RED));

		var minutes = (float) config.getTime() / (20 * 60);

		list.add(Component.literal("⌚ " + String.format("%.0f", minutes)  + " Minute" + (minutes > 1 ? "s" : "")).withStyle(ChatFormatting.GOLD));

		for (var effect : config.extraEffects) {
			var eff = effect.getEffect();
			if (eff == null)
				continue;

			list.add(Component.literal("★ " + eff.getDisplayName().getString() + (effect.amplifier > 1 ? " " + effect.amplifier : "")).withStyle(ChatFormatting.GREEN));
		}

		if (item.getUseAnimation() == UseAnim.DRINK) {
			list.add(Component.literal("❄ Refreshing!").withStyle(ChatFormatting.AQUA));

		}
	}
}
