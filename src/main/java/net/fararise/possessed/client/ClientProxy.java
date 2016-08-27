package net.fararise.possessed.client;

import net.fararise.possessed.Possessed;
import net.fararise.possessed.client.gui.GameOverlayGUI;
import net.fararise.possessed.server.ServerProxy;
import net.fararise.possessed.server.item.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.lwjgl.input.Keyboard;

import java.util.function.Function;

public class ClientProxy extends ServerProxy {
    public static final KeyBinding STOP_POSSESSING_KEY = new KeyBinding("Stop Possessing", Keyboard.KEY_C, Possessed.NAME);

    private static final Minecraft MINECRAFT = Minecraft.getMinecraft();

    @Override
    public void onPreInit() {
        super.onPreInit();

        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new GameOverlayGUI());

        ClientRegistry.registerKeyBinding(ClientProxy.STOP_POSSESSING_KEY);
    }

    @Override
    public void onInit() {
        super.onInit();
    }

    @Override
    public void onPostInit() {
        super.onPostInit();
        ItemModelMesher modelMesher = ClientProxy.MINECRAFT.getRenderItem().getItemModelMesher();
        modelMesher.register(ItemRegistry.POSSESSIVE_HELMET, stack -> new ModelResourceLocation(Possessed.MODID + ":possessive_helmet", "inventory"));
    }

    @Override
    public EntityPlayer getPlayer(MessageContext ctx) {
        return ctx.side.isServer() ? super.getPlayer(ctx) : ClientProxy.MINECRAFT.thePlayer;
    }

    @Override
    public void handleMessage(MessageContext ctx, Function<EntityPlayer, Void> call) {
        if (ctx.side.isServer()) {
            super.handleMessage(ctx, call);
        } else {
            ClientProxy.MINECRAFT.addScheduledTask(() -> call.apply(this.getPlayer(ctx)));
        }
    }
}
