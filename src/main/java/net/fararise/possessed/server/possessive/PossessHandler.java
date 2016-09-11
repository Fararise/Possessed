package net.fararise.possessed.server.possessive;

import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.network.PossessMessage;
import net.fararise.possessed.server.possessive.handler.ChickenHandler;
import net.fararise.possessed.server.possessive.handler.CreeperHandler;
import net.fararise.possessed.server.possessive.handler.DragonHandler;
import net.fararise.possessed.server.possessive.handler.EndermanHandler;
import net.fararise.possessed.server.possessive.handler.FlyingHandler;
import net.fararise.possessed.server.possessive.handler.GuardianHandler;
import net.fararise.possessed.server.possessive.handler.IronGolemHandler;
import net.fararise.possessed.server.possessive.handler.PolarBearHandler;
import net.fararise.possessed.server.possessive.handler.RabbitHandler;
import net.fararise.possessed.server.possessive.handler.SheepHandler;
import net.fararise.possessed.server.possessive.handler.ShulkerHandler;
import net.fararise.possessed.server.possessive.handler.SkeletonHandler;
import net.fararise.possessed.server.possessive.handler.SlimeHandler;
import net.fararise.possessed.server.possessive.handler.SnowmanHandler;
import net.fararise.possessed.server.possessive.handler.SpiderHandler;
import net.fararise.possessed.server.possessive.handler.SquidHandler;
import net.fararise.possessed.server.possessive.handler.WaterMobHandler;
import net.fararise.possessed.server.possessive.handler.WitherHandler;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PossessHandler {
    private static final Map<EntityPlayer, PossessivePlayer> CLIENT_POSSESSIVE_PLAYERS = new HashMap<>();
    private static final Map<EntityPlayer, PossessivePlayer> SERVER_POSSESSIVE_PLAYERS = new HashMap<>();

    private static final Map<Class<? extends EntityLivingBase>, List<EntityPossessHandler>> POSSESS_HANDLERS = new HashMap<>();
    private static final Map<ResourceLocation, EntityPossessHandler> POSSESS_HANDLER_IDENTIFIERS = new HashMap<>();

    public static void onPreInit() {
        PossessHandler.registerHandler(new ChickenHandler());
        PossessHandler.registerHandler(new CreeperHandler());
        PossessHandler.registerHandler(new DragonHandler());
        PossessHandler.registerHandler(new EndermanHandler());
        PossessHandler.registerHandler(new FlyingHandler(EntityFlying.class, new ResourceLocation(Possessed.MODID, "flying")));
        PossessHandler.registerHandler(new FlyingHandler(EntityBat.class, new ResourceLocation(Possessed.MODID, "bat")));
        PossessHandler.registerHandler(new FlyingHandler(EntityBlaze.class, new ResourceLocation(Possessed.MODID, "blaze")));
        PossessHandler.registerHandler(new IronGolemHandler());
        PossessHandler.registerHandler(new PolarBearHandler());
        PossessHandler.registerHandler(new RabbitHandler());
        PossessHandler.registerHandler(new SheepHandler());
        PossessHandler.registerHandler(new ShulkerHandler());
        PossessHandler.registerHandler(new SkeletonHandler());
        PossessHandler.registerHandler(new SlimeHandler());
        PossessHandler.registerHandler(new SnowmanHandler());
        PossessHandler.registerHandler(new SpiderHandler());
        PossessHandler.registerHandler(new SquidHandler());
        PossessHandler.registerHandler(new GuardianHandler());
        PossessHandler.registerHandler(new WaterMobHandler());
        PossessHandler.registerHandler(new WitherHandler());
    }

    public static void possess(EntityPlayer player, EntityLivingBase entity) {
        if (entity != null) {
            for (EntityPossessHandler possessHandler : PossessHandler.getPossessHandlers(entity)) {
                if (!possessHandler.canPossess(player, entity)) {
                    return;
                }
            }
        }
        double originalX = player.lastTickPosX;
        double originalY = player.lastTickPosY;
        double originalZ = player.lastTickPosZ;
        PossessivePlayer prevPossessing = PossessHandler.get(player);
        if (PossessHandler.isPossessing(player)) {
            if (entity != null) {
                PossessHandler.getPossessivePlayers(player.worldObj).remove(player).stop(player);
            } else {
                if (!prevPossessing.isAnimating()) {
                    prevPossessing.stopPossessing();
                }
            }
        }
        if (entity != null) {
            EntityPossessHandler.PLAYER_DATA.put(player, new NBTTagCompound());
            player.setPositionAndRotation(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
            PossessivePlayer possessivePlayer = new PossessivePlayer(player, entity, originalX, originalY, originalZ);
            possessivePlayer.update(player, false);
            PossessHandler.getPossessivePlayers(player.worldObj).put(player, possessivePlayer);
            entity.isDead = true;
            player.setHealth(entity.getHealth());
        } else if (prevPossessing != null) {
            prevPossessing.setOriginalPosition(originalX, originalY, originalZ);
        }
        if (!player.worldObj.isRemote && player.worldObj instanceof WorldServer && player instanceof EntityPlayerMP) {
            PossessivePlayer possessivePlayer = PossessHandler.get(player);
            if (!((prevPossessing == possessivePlayer) || (prevPossessing != null && possessivePlayer != null && possessivePlayer.getPossessing() == prevPossessing.getPossessing()))) {
                PossessHandler.syncTracking(player, possessivePlayer);
            }
        }
    }

    private static void syncTracking(EntityPlayer player, PossessivePlayer possessivePlayer) {
        if (player.worldObj instanceof WorldServer) {
            IMessage message = new PossessMessage(player, possessivePlayer);
            WorldServer worldServer = (WorldServer) player.worldObj;
            for (EntityPlayer tracking : worldServer.getEntityTracker().getTrackingPlayers(player)) {
                if (((EntityPlayerMP) tracking).connection != null) {
                    Possessed.getNetworkWrapper().sendTo(message, (EntityPlayerMP) tracking);
                }
            }
            if (((EntityPlayerMP) player).connection != null) {
                Possessed.getNetworkWrapper().sendTo(message, (EntityPlayerMP) player);
            }
        }
    }

    public static PossessivePlayer get(EntityPlayer player) {
        return PossessHandler.getPossessivePlayers(player.worldObj).get(player);
    }

    public static EntityPlayer getPossesor(EntityLivingBase entity) {
        Map<EntityPlayer, PossessivePlayer> possessivePlayers = PossessHandler.getPossessivePlayers(entity.worldObj);
        for (Map.Entry<EntityPlayer, PossessivePlayer> entry : possessivePlayers.entrySet()) {
            if (entry.getValue().getPossessing().equals(entity)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static boolean isPossessing(EntityPlayer player) {
        return PossessHandler.get(player) != null;
    }

    public static void registerHandler(EntityPossessHandler handler) {
        Class<? extends EntityLivingBase> entity = handler.getEntityClass();
        List<EntityPossessHandler> handlers = PossessHandler.POSSESS_HANDLERS.get(entity);
        if (handlers == null) {
            handlers = new ArrayList<>();
        }
        handlers.add(handler);
        PossessHandler.POSSESS_HANDLERS.put(entity, handlers);
        PossessHandler.POSSESS_HANDLER_IDENTIFIERS.put(handler.getIdentifier(), handler);
        if (handler.isEventHandler()) {
            MinecraftForge.EVENT_BUS.register(handler);
        }
    }

    public static List<EntityPossessHandler> getPossessHandlers(EntityLivingBase entity) {
        List<EntityPossessHandler> handlers = new LinkedList<>();
        for (Map.Entry<Class<? extends EntityLivingBase>, List<EntityPossessHandler>> entry : PossessHandler.POSSESS_HANDLERS.entrySet()) {
            if (entry.getKey().isAssignableFrom(entity.getClass())) {
                handlers.addAll(entry.getValue());
            }
        }
        return handlers;
    }

    public static EntityPossessHandler getPossessHandler(ResourceLocation identifier) {
        return PossessHandler.POSSESS_HANDLER_IDENTIFIERS.get(identifier);
    }

    private static Map<EntityPlayer, PossessivePlayer> getPossessivePlayers(World world) {
        return world.isRemote ? PossessHandler.CLIENT_POSSESSIVE_PLAYERS : PossessHandler.SERVER_POSSESSIVE_PLAYERS;
    }

    public static void setSize(EntityLivingBase entity, float width, float height) {
        if (width != entity.width || height != entity.height) {
            float prevWidth = entity.width;
            entity.width = width;
            entity.height = height;
            AxisAlignedBB bounds = entity.getEntityBoundingBox();
            entity.setEntityBoundingBox(new AxisAlignedBB(bounds.minX, bounds.minY, bounds.minZ, bounds.minX + entity.width, bounds.minY + entity.height, bounds.minZ + entity.width));
            if (entity.width > prevWidth && entity.ticksExisted > 1 && !entity.worldObj.isRemote) {
                entity.moveEntity(prevWidth - entity.width, 0.0F, prevWidth - entity.width);
            }
        }
    }

    public static void removePossession(EntityPlayer player) {
        PossessivePlayer possessivePlayer = PossessHandler.getPossessivePlayers(player.worldObj).remove(player);
        if (possessivePlayer != null) {
            possessivePlayer.stop(player);
        }
        PossessHandler.syncTracking(player, null);
    }
}
