package vice.sol_valheim;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;

public class DataAccessors
{
    public static final EntityDataAccessor<ItemStack> T_Item0 = SynchedEntityData.defineId(Player.class, EntityDataSerializers.ITEM_STACK);

    public static final EntityDataAccessor<ItemStack> T_Item1 = SynchedEntityData.defineId(Player.class, EntityDataSerializers.ITEM_STACK);

    public static final EntityDataAccessor<ItemStack> T_Item2 = SynchedEntityData.defineId(Player.class, EntityDataSerializers.ITEM_STACK);

    public static final EntityDataAccessor<Integer> T_Timer0 = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> T_Timer1 = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> T_Timer2 = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);


    public static EntityDataAccessor<ItemStack> getFood(int i) {
        switch (i) {
            case 0:
                return T_Item0;
            case 1:
                return T_Item1;
        }
        return T_Item2;
    }

    public static EntityDataAccessor<Integer> getTimer(int i) {
        switch (i) {
            case 0:
                return T_Timer0;
            case 1:
                return T_Timer1;
        }
        return T_Timer2;
    }
}