package net.fararise.possessed;

import net.fararise.possessed.server.ServerProxy;
import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.capability.PossessCapability;
import net.fararise.possessed.server.network.PossessClickEmptyMessage;
import net.fararise.possessed.server.network.PossessHurtMessage;
import net.fararise.possessed.server.network.PossessMessage;
import net.fararise.possessed.server.network.PossessiveChargeMessage;
import net.fararise.possessed.server.network.StopPossessingMessage;
import net.fararise.possessed.server.network.UpdatePossessedDataMessage;
import net.fararise.possessed.server.possessive.PossessHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Possessed.MODID, name = Possessed.NAME, version = Possessed.VERSION)
public class Possessed {
    public static final String MODID = "possessed";
    public static final String NAME = "Possessed";
    public static final String VERSION = "0.3.1";

    @SidedProxy(serverSide = "net.fararise.possessed.server.ServerProxy", clientSide = "net.fararise.possessed.client.ClientProxy")
    private static ServerProxy proxy;

    private static SimpleNetworkWrapper networkWrapper;

    @CapabilityInject(PossessCapability.class)
    private static Capability<PossessCapability> playerDataCapability;

    public static ServerProxy getProxy() {
        return Possessed.proxy;
    }

    public static SimpleNetworkWrapper getNetworkWrapper() {
        return Possessed.networkWrapper;
    }

    public static Capability<PossessCapability> getPlayerDataCapability() {
        return Possessed.playerDataCapability;
    }

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        Possessed.proxy.onPreInit();

        Possessed.networkWrapper = new SimpleNetworkWrapper(Possessed.MODID);

        Possessed.networkWrapper.registerMessage(PossessMessage.Handler.class, PossessMessage.class, 0, Side.CLIENT);
        Possessed.networkWrapper.registerMessage(UpdatePossessedDataMessage.Handler.class, UpdatePossessedDataMessage.class, 1, Side.CLIENT);
        Possessed.networkWrapper.registerMessage(PossessClickEmptyMessage.Handler.class, PossessClickEmptyMessage.class, 2, Side.SERVER);
        Possessed.networkWrapper.registerMessage(PossessHurtMessage.Handler.class, PossessHurtMessage.class, 3, Side.CLIENT);
        Possessed.networkWrapper.registerMessage(PossessiveChargeMessage.Handler.class, PossessiveChargeMessage.class, 4, Side.CLIENT);
        Possessed.networkWrapper.registerMessage(StopPossessingMessage.Handler.class, StopPossessingMessage.class, 5, Side.SERVER);
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        Possessed.proxy.onInit();
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        Possessed.proxy.onPostInit();
    }

    @Mod.EventHandler
    public void onReceiveIMC(FMLInterModComms.IMCEvent event) {
        for (FMLInterModComms.IMCMessage message : event.getMessages()) {
            if (message.key.equalsIgnoreCase("possess_handler")) {
                if (message.isStringMessage()) {
                    try {
                        Class<? extends EntityPossessHandler> clazz = (Class<? extends EntityPossessHandler>) Class.forName(message.getStringValue());
                        EntityPossessHandler handler = clazz.getDeclaredConstructor().newInstance();
                        PossessHandler.registerHandler(handler);
                    } catch (Exception e) {
                        System.err.println("Received invalid IMC EntityPossessHandler class from mod " + message.getSender() + " (" + message.getStringValue() + ")");
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
