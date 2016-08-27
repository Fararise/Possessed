package net.fararise.possessed.server.network;

import io.netty.buffer.ByteBuf;
import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.possessive.PossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;
import java.util.List;

public class UpdatePossessedDataMessage implements IMessage {
    private int entityID;
    private List<EntityDataManager.DataEntry<?>> entries;

    public UpdatePossessedDataMessage() {
    }

    public UpdatePossessedDataMessage(EntityPlayer entity, EntityLivingBase possessed) {
        this.entityID = entity.getEntityId();
        this.entries = possessed.getDataManager().getDirty();
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.entityID = buffer.readInt();
        try {
            this.entries = EntityDataManager.readEntries(new PacketBuffer(buffer));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.entityID);
        try {
            EntityDataManager.writeEntries(this.entries, new PacketBuffer(buffer));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Handler implements IMessageHandler<UpdatePossessedDataMessage, UpdatePossessedDataMessage> {
        @Override
        public UpdatePossessedDataMessage onMessage(UpdatePossessedDataMessage message, MessageContext ctx) {
            Possessed.getProxy().handleMessage(ctx, player -> {
                if (ctx.side.isClient()) {
                    EntityPlayer sender = null;
                    Entity senderEntity = player.worldObj.getEntityByID(message.entityID);
                    if (senderEntity instanceof EntityPlayer) {
                        sender = (EntityPlayer) senderEntity;
                    }
                    if (sender != null) {
                        PossessivePlayer possessivePlayer = PossessHandler.get(sender);
                        if (possessivePlayer != null) {
                            if (message.entries != null) {
                                possessivePlayer.getPossessing().getDataManager().setEntryValues(message.entries);
                            }
                        }
                    }
                }
                return null;
            });
            return null;
        }
    }
}
