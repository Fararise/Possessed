package net.fararise.possessed.server;

import net.fararise.possessed.server.block.BlockRegistry;
import net.fararise.possessed.server.capability.PossessCapability;
import net.fararise.possessed.server.item.ItemRegistry;
import net.fararise.possessed.server.possessive.PossessHandler;
import net.fararise.possessed.server.world.PossessedWorldGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.function.Function;

public class ServerProxy {
    public void onPreInit() {
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
        PossessHandler.onPreInit();
        ItemRegistry.onPreInit();
        BlockRegistry.onPreInit();

        GameRegistry.registerWorldGenerator(new PossessedWorldGenerator(), 0);

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

    public void pickItem(EntityPlayer player, int index) {
        player.inventory.pickItem(index);
    }
}
