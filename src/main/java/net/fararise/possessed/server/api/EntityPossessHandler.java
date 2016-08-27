package net.fararise.possessed.server.api;

import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public interface EntityPossessHandler {
    void onUpdate(PossessivePlayer possessivePlayer, EntityPlayer player);

    default void onDeath(PossessivePlayer possessivePlayer, EntityPlayer player) {
    }

    default void onClickBlock(PossessivePlayer possessivePlayer, EntityPlayer player) {
    }

    default void onClickAir(PossessivePlayer possessivePlayer, EntityPlayer player) {
    }

    default double getSpeed(PossessivePlayer possessivePlayer, EntityPlayer player) {
        return -1.0;
    }

    Class<? extends EntityLivingBase> getEntityClass();

    default void onJump(PossessivePlayer possessivePlayer, EntityPlayer player) {
    }
}
