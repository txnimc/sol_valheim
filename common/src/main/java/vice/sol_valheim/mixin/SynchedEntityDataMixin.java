package vice.sol_valheim.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import vice.sol_valheim.extenders.SynchedEntityDataExtender;

@Mixin(SynchedEntityData.class)
public abstract class SynchedEntityDataMixin implements SynchedEntityDataExtender {
    #if PRE_CURRENT_MC_1_19_2

    @Override
    public <T> void set(EntityDataAccessor<T> key, T value, boolean force) {
        SynchedEntityData thisObject = (SynchedEntityData)(Object)this;

        SynchedEntityData.DataItem<T> dataItem = thisObject.getItem(key);
        if (force || ObjectUtils.notEqual(value, dataItem.getValue())) {
            dataItem.setValue(value);
            thisObject.entity.onSyncedDataUpdated(key);
            dataItem.setDirty(true);
            thisObject.isDirty = true;
        }
    }
    #endif
}
