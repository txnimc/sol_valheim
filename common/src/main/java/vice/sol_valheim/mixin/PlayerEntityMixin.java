package vice.sol_valheim.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vice.sol_valheim.SOLValheim;
import vice.sol_valheim.accessors.FoodDataPlayerAccessor;
import vice.sol_valheim.accessors.PlayerEntityMixinDataAccessor;
import vice.sol_valheim.ValheimFoodData;

@Mixin({Player.class})
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityMixinDataAccessor
{
    @Unique
    private static final EntityDataAccessor<ValheimFoodData> sol_valheim$DATA_ACCESSOR = SynchedEntityData.defineId(Player.class, ValheimFoodData.FOOD_DATA_SERIALIZER);

    @Shadow
    protected FoodData foodData;

    @Override
    @Unique
    public ValheimFoodData sol_valheim$getFoodData() {
        var player = (Player) (LivingEntity)this;
        return player.getEntityData().get(sol_valheim$DATA_ACCESSOR);
    }

    @Unique
    private ValheimFoodData sol_valheim$food_data = new ValheimFoodData();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) { super(entityType, level); }

    @Inject(at = {@At("HEAD")}, method = {"causeFoodExhaustion(F)V"}, cancellable = true)
    private void onAddExhaustion(float exhaustion, CallbackInfo info) {
        info.cancel();
    }

    @Inject(at = {@At("HEAD")}, method = {"getFoodData"})
    private void onGetFoodData(CallbackInfoReturnable<FoodData> cir) {
        // hack workaround for player data not being accessible in FoodData
        ((FoodDataPlayerAccessor) foodData).sol_valheim$setPlayer((Player) (LivingEntity) this);
    }

    @Inject(at = {@At("HEAD")}, method = {"eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"})
    private void onEatFood(Level world, ItemStack stack, CallbackInfoReturnable<ItemStack> info) {
        if (stack.getItem() == Items.ROTTEN_FLESH) {
            sol_valheim$food_data.clear();
            sol_valheim$trackData();
            return;
        }

        sol_valheim$food_data.eatItem(stack.getItem());
    }

    @Inject(at = {@At("HEAD")}, method = {"tick"})
    private void onTick(CallbackInfo info) {
        sol_valheim$tick();
    }

    @Unique
    private void sol_valheim$tick() {
        if (this.level().isClientSide)
            return;

        if (isDeadOrDying()) {
            sol_valheim$food_data.clear();
            sol_valheim$trackData();
            return;
        }

        if (!sol_valheim$food_data.ItemEntries.isEmpty()) {
            sol_valheim$food_data.tick();
            sol_valheim$trackData();
        }

        float maxhp = Math.min(40, (SOLValheim.Config.common.startingHealth * 2) + sol_valheim$food_data.getTotalFoodNutrition());

        Player player = (Player) (LivingEntity) this;
        player.getFoodData().setSaturation(0);

        player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxhp);
        if (getHealth() > maxhp)
            setHealth(maxhp);

        if (SOLValheim.Config.common.speedBoost > 0.01f) {
            var attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
            var speedBuff = attr.getModifier(SOLValheim.getSpeedBuffModifier().getId());
            if (maxhp >= 20 && speedBuff == null)
                attr.addTransientModifier(SOLValheim.getSpeedBuffModifier());
            else if (maxhp < 20 && speedBuff != null)
                attr.removeModifier(SOLValheim.getSpeedBuffModifier());
        }

        var timeSinceHurt = player.level().getGameTime() - ((LivingEntityDamageAccessor) this).getLastDamageStamp();
        if (timeSinceHurt > SOLValheim.Config.common.regenDelay && player.tickCount % (5 * SOLValheim.Config.common.regenSpeedModifier) == 0)
        {
            player.heal(sol_valheim$food_data.getRegenSpeed() / 20f);
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"canEat(Z)Z"}, cancellable = true)
    private void onCanConsume(boolean ignorehunger, CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(true);
        info.cancel();
    }

    @Inject(at = {@At("HEAD")}, method = {"hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"}, cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (source == this.damageSources().starve()) {
            info.setReturnValue(Boolean.FALSE);
            info.cancel();
        }
    }

    @Inject(at = {@At("TAIL")}, method = {"addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"})
    private void onWriteCustomData(CompoundTag nbt, CallbackInfo info) {
        nbt.put("sol_food_data", sol_valheim$food_data.save(new CompoundTag()));
    }

    @Inject(at = {@At("TAIL")}, method = {"readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"})
    private void onReadCustomData(CompoundTag nbt, CallbackInfo info) {
        sol_valheim$food_data = ValheimFoodData.read(nbt.getCompound("sol_food_data"));
        sol_valheim$trackData();
    }

    @Unique
    private void sol_valheim$trackData() {
        this.entityData.set(sol_valheim$DATA_ACCESSOR, sol_valheim$food_data, true);
    }

    @Inject(at = {@At("TAIL")}, method = {"defineSynchedData"})
    private void onInitDataTracker(CallbackInfo info) {
        if (sol_valheim$food_data == null)
            sol_valheim$food_data = new ValheimFoodData();
        this.entityData.define(sol_valheim$DATA_ACCESSOR, sol_valheim$food_data);
    }
}