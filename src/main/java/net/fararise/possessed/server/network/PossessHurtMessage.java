package net.fararise.possessed.server.network;

import io.netty.buffer.ByteBuf;
import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.possessive.PossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PossessHurtMessage implements IMessage {
    private int playerId;

    public PossessHurtMessage() {
    }

    public PossessHurtMessage(EntityPlayer player) {
        this.playerId = player.getEntityId();
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.playerId = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.playerId);
    }

    public static class Handler implements IMessageHandler<PossessHurtMessage, PossessHurtMessage> {
        @Override
        public PossessHurtMessage onMessage(PossessHurtMessage message, MessageContext ctx) {
            Possessed.getProxy().handleMessage(ctx, (player) -> {
                if (ctx.side.isClient()) {
                    EntityPlayer sender = null;
                    Entity senderEntity = player.worldObj.getEntityByID(message.playerId);
                    if (senderEntity instanceof EntityPlayer) {
                        sender = (EntityPlayer) senderEntity;
                    }
                    if (sender != null) {
                        PossessivePlayer possessivePlayer = PossessHandler.get(sender);
                        if (possessivePlayer != null) {
                            EntityLivingBase possessing = possessivePlayer.getPossessing();
                            possessing.maxHurtTime = 10;
                            possessing.hurtTime = possessing.maxHurtTime;
                            possessing.attackedAtYaw = 0.0F;
                        }
                    }
                }
                return null;
            });
            return null;
        }
    }
}
