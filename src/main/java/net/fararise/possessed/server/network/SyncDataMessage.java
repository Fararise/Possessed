package net.fararise.possessed.server.network;

import io.netty.buffer.ByteBuf;
import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.capability.PossessCapability;
import net.fararise.possessed.server.possessive.PossessionExperience;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Map;

public class SyncDataMessage implements IMessage {
    private int playerId;
    private float possessTime;
    private PossessionExperience experience;

    public SyncDataMessage() {
    }

    public SyncDataMessage(EntityPlayer player, float possessTime, PossessionExperience experience) {
        this.playerId = player.getEntityId();
        this.possessTime = possessTime;
        this.experience = experience;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.playerId = buffer.readInt();
        this.possessTime = buffer.readFloat();
        this.experience = new PossessionExperience();
        int experienceCount = buffer.readShort();
        for (int i = 0; i < experienceCount; i++) {
            Class<? extends Entity> entity = EntityList.getClassFromID(buffer.readInt());
            if (entity != null && EntityLivingBase.class.isAssignableFrom(entity)) {
                this.experience.setExperience((Class<? extends EntityLivingBase>) entity, buffer.readShort());
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.playerId);
        buffer.writeFloat(this.possessTime);
        buffer.writeShort(this.experience.getAllExperience().size());
        for (Map.Entry<String, Integer> entry : this.experience.getAllExperience().entrySet()) {
            buffer.writeInt(EntityList.getIDFromString(entry.getKey()));
            buffer.writeShort(entry.getValue());
        }
    }

    public static class Handler implements IMessageHandler<SyncDataMessage, PossessHurtMessage> {
        @Override
        public PossessHurtMessage onMessage(SyncDataMessage message, MessageContext ctx) {
            Possessed.getProxy().handleMessage(ctx, (player) -> {
                if (ctx.side.isClient()) {
                    EntityPlayer sender = null;
                    Entity senderEntity = player.worldObj.getEntityByID(message.playerId);
                    if (senderEntity instanceof EntityPlayer) {
                        sender = (EntityPlayer) senderEntity;
                    }
                    if (sender != null) {
                        PossessCapability capability = PossessCapability.Implementation.get(player);
                        capability.setPossessTime(message.possessTime);
                        capability.setExperience(message.experience);
                    }
                }
                return null;
            });
            return null;
        }
    }
}
