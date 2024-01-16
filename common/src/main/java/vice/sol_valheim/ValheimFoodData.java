package vice.sol_valheim;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ValheimFoodData
{
    public static final EntityDataSerializer<ValheimFoodData> FOOD_DATA_SERIALIZER = new EntityDataSerializer<>(){
        @Override
        public void write(FriendlyByteBuf buffer, ValheimFoodData value)
        {
            buffer.writeNbt(value.save(new CompoundTag()));
        }

        @Override
        public ValheimFoodData read(FriendlyByteBuf buffer) {

            return ValheimFoodData.read(buffer.readNbt());
        }

        @Override
        public ValheimFoodData copy(ValheimFoodData value)
        {
            var ret = new ValheimFoodData();
            ret.ItemEntries = value.ItemEntries.stream().map(d -> new EatenFoodItem(d.item, d.ticksLeft)).collect(Collectors.toCollection(ArrayList::new));
            return ret;
        }

    };

    public List<EatenFoodItem> ItemEntries = new ArrayList<>();
    public int MaxItemSlots = SOLValheim.Config.common.maxSlots;

    public void eatItem(Item food)
    {
        if (food == Items.ROTTEN_FLESH)
            return;

        var config = ModConfig.getFoodConfig(food);
        if (config == null)
            return;

        var existing = getEatenFood(food);
        if (existing != null)
        {
            if (!existing.canEatEarly())
                return;

            existing.ticksLeft = config.getTime();
            return;
        }

        if (ItemEntries.size() < MaxItemSlots)
        {
            ItemEntries.add(new EatenFoodItem(food, config.getTime()));
            return;
        }

        for (var item : ItemEntries)
        {
            if (item.canEatEarly())
            {
                item.ticksLeft = config.getTime();
                item.item = food;
                return;
            }
        }
    }

    public boolean canEat(Item food)
    {
        if (food == Items.ROTTEN_FLESH)
            return true;

        var existing = getEatenFood(food);
        if (existing != null)
            return existing.canEatEarly();

        if (ItemEntries.size() < MaxItemSlots)
            return true;

        return ItemEntries.stream().anyMatch(EatenFoodItem::canEatEarly);
    }

    public EatenFoodItem getEatenFood(Item food) {
        return ItemEntries.stream()
                .filter((item) -> item.item == food)
                .findFirst()
                .orElse(null);
    }


    public void clear()
    {
        ItemEntries.clear();
    }


    public void tick()
    {
        for (var item : ItemEntries)
        {
            item.ticksLeft--;
        }

        ItemEntries.removeIf(item -> item.ticksLeft <= 0);
        ItemEntries.sort(Comparator.comparingInt(a -> a.ticksLeft));
    }


    public float getTotalFoodNutrition()
    {
        float nutrition = 0f;
        for (var item : ItemEntries)
        {
            ModConfig.Common.FoodConfig food = ModConfig.getFoodConfig(item.item);
            if (food == null)
                continue;

            nutrition += food.getHearts();
        }

        return nutrition;
    }


    public float getRegenSpeed()
    {
        float regen = 0.25f;
        for (var item : ItemEntries)
        {
            ModConfig.Common.FoodConfig food = ModConfig.getFoodConfig(item.item);
            if (food == null)
                continue;

            regen += food.getHealthRegen();
        }

        return regen;
    }


    public CompoundTag save(CompoundTag tag) {
        int count = 0;
        tag.putInt("max_slots", MaxItemSlots);
        tag.putInt("count", ItemEntries.size());
        for (var item : ItemEntries)
        {
            tag.putString("id" + count, item.item.arch$registryName().toString());
            tag.putInt("ticks" + count, item.ticksLeft);
            count++;
        }

        return tag;
    }

    public static ValheimFoodData read(CompoundTag tag) {
        var instance = new ValheimFoodData();
        instance.MaxItemSlots = tag.getInt("max_slots");

        var size = tag.getInt("count");
        for (int count = 0; count < size; count++)
        {
            var str = tag.getString("id" + count);
            var ticks = tag.getInt("ticks" + count);
            var item = SOLValheim.ITEMS.getRegistrar().get(new ResourceLocation(str));

            instance.ItemEntries.add(new EatenFoodItem(item, ticks));
        }

        return instance;
    }


    public static class EatenFoodItem {
        public Item item;
        public int ticksLeft;

        public boolean canEatEarly() {
            if (ticksLeft < 1200)
                return true;

            var config = ModConfig.getFoodConfig(item);
            if (config == null)
                return false;

            return ((float) this.ticksLeft / config.getTime()) < SOLValheim.Config.common.eatAgainPercentage;
        }

        public EatenFoodItem(Item item, int ticksLeft)
        {
            this.item = item;
            this.ticksLeft = ticksLeft;
        }
    }
}