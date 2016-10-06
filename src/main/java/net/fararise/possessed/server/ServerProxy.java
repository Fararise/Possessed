package net.fararise.possessed.server;

import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.block.BlockRegistry;
import net.fararise.possessed.server.capability.PossessCapability;
import net.fararise.possessed.server.item.ItemRegistry;
import net.fararise.possessed.server.possessive.PossessHandler;
import net.fararise.possessed.server.world.PossessedWorldGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.function.Function;

public class ServerProxy implements IGuiHandler {
    public void onPreInit() {
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
        PossessHandler.onPreInit();
        ItemRegistry.onPreInit();
        BlockRegistry.onPreInit();

        NetworkRegistry.INSTANCE.registerGuiHandler(Possessed.getInstance(), this);

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

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }
}
