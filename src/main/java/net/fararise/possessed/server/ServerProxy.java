package net.fararise.possessed.server;

import net.fararise.possessed.server.capability.PossessCapability;
import net.fararise.possessed.server.item.ItemRegistry;
import net.fararise.possessed.server.possessive.PossessHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.function.Function;

public class ServerProxy {
    public void onPreInit() {
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
        PossessHandler.onPreInit();
        ItemRegistry.onPreInit();

        CapabilityManager.INSTANCE.register(PossessCapability.class, new PossessCapability.Storage(), new PossessCapability.Factory());
    }

    public void onInit() {
    }

    public void onPostInit() {
    }

    public EntityPlayer getPlayer(MessageContext ctx) {
        return ctx.getServerHandler().playerEntity;
    }

    public void handleMessage(MessageContext ctx, Function<EntityPlayer, Void> call) {
        ((WorldServer) ctx.getServerHandler().playerEntity.worldObj).addScheduledTask(() -> call.apply(this.getPlayer(ctx)));
    }
}