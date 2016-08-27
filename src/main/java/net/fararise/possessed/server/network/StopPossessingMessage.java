package net.fararise.possessed.server.network;

import io.netty.buffer.ByteBuf;
import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.possessive.PossessHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class StopPossessingMessage implements IMessage {
    @Override
    public void fromBytes(ByteBuf buffer) {
    }

    @Override
    public void toBytes(ByteBuf buffer) {
    }

    public static class Handler implements IMessageHandler<StopPossessingMessage, StopPossessingMessage> {
        @Override
        public StopPossessingMessage onMessage(StopPossessingMessage message, MessageContext ctx) {
            Possessed.getProxy().handleMessage(ctx, (player) -> {
                if (ctx.side.isServer()) {
                    PossessHandler.possess(player, null);
                }
                return null;
            });
            return null;
        }
    }
}
