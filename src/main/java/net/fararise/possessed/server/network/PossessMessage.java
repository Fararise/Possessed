package net.fararise.possessed.server.network;

import io.netty.buffer.ByteBuf;
import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.possessive.PossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PossessMessage implements IMessage {
    private int playerId;
    private NBTTagCompound tag;

    public PossessMessage() {
    }

    public PossessMessage(EntityPlayer player, PossessivePlayer possessivePlayer) {
        this.playerId = player.getEntityId();
        this.tag = new NBTTagCompound();
        if (possessivePlayer != null && possessivePlayer.getPossessing() != null) {
            this.tag.setString("id", EntityList.getEntityString(possessivePlayer.getPossessing()));
            possessivePlayer.getPossessing().writeToNBT(this.tag);
        }
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.tag = ByteBufUtils.readTag(buffer);
        this.playerId = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        ByteBufUtils.writeTag(buffer, this.tag);
        buffer.writeInt(this.playerId);
    }

    public static class Handler implements IMessageHandler<PossessMessage, PossessMessage> {
        @Override
        public PossessMessage onMessage(PossessMessage message, MessageContext ctx) {
            Possessed.getProxy().handleMessage(ctx, (player) -> {
                if (ctx.side.isClient()) {
                    EntityPlayer sender = null;
                    Entity senderEntity = player.worldObj.getEntityByID(message.playerId);
                    if (senderEntity instanceof EntityPlayer) {
                        sender = (EntityPlayer) senderEntity;
                    }
                    if (sender != null) {
                        if (message.tag.getKeySet().size() > 0) {
                            Entity entity = EntityList.createEntityFromNBT(message.tag, player.worldObj);
                            if (entity instanceof EntityLivingBase) {
                                if (((EntityLivingBase) entity).getHealth() > 0.0F) {
                                    PossessHandler.possess(sender, (EntityLivingBase) entity);
                                } else {
                                    PossessHandler.possess(sender, null);
                                }
                            }
                        } else {
                            PossessHandler.possess(sender, null);
                        }
                    }
                }
                return null;
            });
            return null;
        }
    }
}
