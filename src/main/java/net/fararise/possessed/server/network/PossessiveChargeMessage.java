package net.fararise.possessed.server.network;

import io.netty.buffer.ByteBuf;
import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.capability.PossessCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PossessiveChargeMessage implements IMessage {
    private int playerId;
    private int charge;

    public PossessiveChargeMessage() {
    }

    public PossessiveChargeMessage(EntityPlayer player, int charge) {
        this.playerId = player.getEntityId();
        this.charge = charge;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.playerId = buffer.readInt();
        this.charge = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.playerId);
        buffer.writeInt(this.charge);
    }

    public static class Handler implements IMessageHandler<PossessiveChargeMessage, PossessHurtMessage> {
        @Override
        public PossessHurtMessage onMessage(PossessiveChargeMessage message, MessageContext ctx) {
            Possessed.getProxy().handleMessage(ctx, (player) -> {
                if (ctx.side.isClient()) {
                    EntityPlayer sender = null;
                    Entity senderEntity = player.worldObj.getEntityByID(message.playerId);
                    if (senderEntity instanceof EntityPlayer) {
                        sender = (EntityPlayer) senderEntity;
                    }
                    if (sender != null) {
                        PossessCapability.Implementation.get(player).setPossessiveCharge(message.charge);
                    }
                }
                return null;
            });
            return null;
        }
    }
}
