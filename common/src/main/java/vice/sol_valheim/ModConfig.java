package vice.sol_valheim;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;


@Config(name = SOLValheim.MOD_ID)
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class ModConfig extends PartitioningSerializer.GlobalData {

    public static Common.FoodConfig getFoodConfig(Item item) {
        if(item != Items.CAKE && !item.isEdible())
            return null;

        var existing = SOLValheim.Config.common.foodConfigs.get(item.arch$registryName());
        if (existing == null){
            var food = item  == Items.CAKE
                    ? new FoodProperties.Builder().nutrition(10).saturationMod(0.7f).build()
                    : item.getFoodProperties();

            existing = new Common.FoodConfig();
            existing.nutrition = food.getNutrition();
            existing.healthRegenModifier = 1f;
            existing.saturationModifier = food.getSaturationModifier();

            var registry = item.arch$registryName().toString();
            if (registry.startsWith("farmers"))
            {
                existing.nutrition = (int) ((existing.nutrition * 1.25));
                existing.saturationModifier = existing.saturationModifier * 1.10f;
                existing.healthRegenModifier = 1.25f;
            }

            if (registry.equals("minecraft:golden_apple") || registry.equals("minecraft:enchanted_golden_apple")) {
                existing.nutrition = 10;
                existing.healthRegenModifier = 1.5f;
            }

//            if (registry.equals("minecraft:beetroot_soup")) {
//                var effectConfig = new Common.MobEffectConfig();
//                effectConfig.ID = BuiltInRegistries.MOB_EFFECT.getKey(MobEffects.MOVEMENT_SPEED).toString();
//                existing.extraEffects.add(effectConfig);
//            }

            SOLValheim.Config.common.foodConfigs.put(item.arch$registryName(), existing);
        }

        return existing;
    }


    @ConfigEntry.Category("common")
    @ConfigEntry.Gui.TransitiveObject()
    public Common common = new Common();

    @ConfigEntry.Category("client")
    @ConfigEntry.Gui.TransitiveObject()
    public Client client = new Client();

    @Config(name = "common")
    public static final class Common implements ConfigData {


        @ConfigEntry.Gui.Tooltip() @Comment("Default time in seconds that food should last per saturation level")
        public int defaultTimer = 180;

        @ConfigEntry.Gui.Tooltip() @Comment("Speed at which regeneration should occur")
        public float regenSpeedModifier = 1f;

        @ConfigEntry.Gui.Tooltip() @Comment("Time in ticks that regeneration should wait after taking damage")
        public int regenDelay = 20 * 10;

        @ConfigEntry.Gui.Tooltip() @Comment("Time in seconds after spawning before sprinting is disabled")
        public int respawnGracePeriod = 60 * 5;

        @ConfigEntry.Gui.Tooltip() @Comment("Extra speed given when your hearts are full (0 to disable)")
        public float speedBoost = 0.20f;

        @ConfigEntry.Gui.Tooltip() @Comment("Number of hearts to start with")
        public int startingHealth = 3;

        @ConfigEntry.Gui.Tooltip() @Comment("Number of food slots (range 2-5, default 3)")
        public int maxSlots = 3;

        @ConfigEntry.Gui.Tooltip() @Comment("Percentage remaining before you can eat again")
        public float eatAgainPercentage = 0.2F;

        @ConfigEntry.Gui.Tooltip(count = 5) @Comment("""
            Food nutrition and effect overrides (Auto Generated if Empty)
            - nutrition: Affects Heart Gain & Health Regen
            - saturationModifier: Affects Food Duration & Player Speed
            - healthRegenModifier: Multiplies health regen speed
            - extraEffects: Extra effects provided by eating the food. Format: { String ID, float duration, int amplifier }
        """)
        public Dictionary<ResourceLocation, FoodConfig> foodConfigs = new Hashtable<>();

        public static final class FoodConfig implements ConfigData {
            public int nutrition;
            public float saturationModifier = 1f;
            public float healthRegenModifier = 1f;
            public List<MobEffectConfig> extraEffects = new ArrayList<>();

            public int getTime() {
                var time = (int) (SOLValheim.Config.common.defaultTimer * 20 * saturationModifier * nutrition);
                return Math.max(time, 6000);
            }

            public int getHearts() {
                return Math.max(nutrition, 2);
            }

            public float getHealthRegen()
            {
                return Mth.clamp(nutrition * 0.10f * healthRegenModifier, 0.25f, 2f);
            }
        }

        public static final class MobEffectConfig implements ConfigData {
            @ConfigEntry.Gui.Tooltip() @Comment("Mob Effect ID")
            public String ID;

            @ConfigEntry.Gui.Tooltip() @Comment("Effect duration percentage (1f is the entire food duration)")
            public float duration = 1f;

            @ConfigEntry.Gui.Tooltip() @Comment("Effect Level")
            public int amplifier = 1;

            public MobEffect getEffect() {
                return SOLValheim.MOB_EFFECTS.getRegistrar().get(new ResourceLocation(ID));
            }
        }

    }

    @Config(name = "client")
    public static final class Client implements ConfigData {
        @ConfigEntry.Gui.Tooltip
        @Comment("Enlarge the currently eaten food icons")
        public boolean useLargeIcons = true;
    }
}