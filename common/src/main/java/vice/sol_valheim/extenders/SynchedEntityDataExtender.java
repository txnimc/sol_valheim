package vice.sol_valheim.extenders;

import net.minecraft.network.syncher.EntityDataAccessor;

public interface SynchedEntityDataExtender {
    <T> void set(EntityDataAccessor<T> key, T value, boolean force);
}
