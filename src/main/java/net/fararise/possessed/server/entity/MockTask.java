package net.fararise.possessed.server.entity;

import net.minecraft.entity.ai.EntityAIBase;

public class MockTask extends EntityAIBase {
    @Override
    public boolean shouldExecute() {
        return true;
    }
}
