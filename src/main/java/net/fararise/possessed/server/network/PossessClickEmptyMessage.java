package net.fararise.possessed.server.network;

import io.netty.buffer.ByteBuf;
import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.possessive.PossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PossessClickEmptyMessage implements IMessage {
    @Override
    public void fromBytes(ByteBuf buffer) {
    }

    @Override
    public void toBytes(ByteBuf buffer) {
    }

    public static class Handler implements IMessageHandler<PossessClickEmptyMessage, PossessClickEmptyMessage> {
        @Override
        public PossessClickEmptyMessage onMessage(PossessClickEmptyMessage message, MessageContext ctx) {
            Possessed.getProxy().handleMessage(ctx, (player) -> {
                if (ctx.side.isServer()) {
                    PossessivePlayer possessivePlayer = PossessHandler.get(player);
                    if (possessivePlayer != null) {
                        for (EntityPossessHandler handler : PossessHandler.getPossessHandlers(possessivePlayer.getPossessing())) {
                            handler.onClickAir(possessivePlayer, player);
                        }
                    }
                }
                return null;
            });
            return null;
        }
    }
}
