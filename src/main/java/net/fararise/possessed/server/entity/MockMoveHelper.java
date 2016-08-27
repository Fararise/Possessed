package net.fararise.possessed.server.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityMoveHelper;

public class MockMoveHelper extends EntityMoveHelper {
    public MockMoveHelper(EntityLiving entity) {
        super(entity);
    }

    @Override
    public void onUpdateMoveHelper() {
    }
}
