package net.fararise.possessed.server.api;

import net.fararise.possessed.server.possessive.PossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public interface EntityPossessHandler {
    Map<EntityPlayer, NBTTagCompound> PLAYER_DATA = new HashMap<>();

    void onUpdate(PossessivePlayer possessivePlayer, EntityPlayer player);

    ResourceLocation getIdentifier();

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

    default void serialize(EntityPlayer player, NBTTagCompound compound) {
        NBTTagCompound data = PLAYER_DATA.get(player);
        if (data != null) {
            compound.merge(data);
        }
    }

    default void deserialize(EntityPlayer player, NBTTagCompound compound) {
        PLAYER_DATA.put(player, compound);
    }

    default NBTTagCompound getData(EntityPlayer player) {
        return PLAYER_DATA.get(player);
    }

    default boolean isEventHandler() {
        return false;
    }

    default boolean isActive(EntityPlayer player) {
        PossessivePlayer possessivePlayer = PossessHandler.get(player);
        if (possessivePlayer != null) {
            if (possessivePlayer.hasHandler(this)) {
                return true;
            }
        }
        return false;
    }
}
