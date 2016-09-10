package net.fararise.possessed.server;

import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.capability.PossessCapability;
import net.fararise.possessed.server.item.PossessiveHelmet;
import net.fararise.possessed.server.network.PossessHurtMessage;
import net.fararise.possessed.server.network.PossessMessage;
import net.fararise.possessed.server.network.PossessiveChargeMessage;
import net.fararise.possessed.server.network.UpdatePossessedDataMessage;
import net.fararise.possessed.server.possessive.PossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ServerEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onAttachCapabilities(AttachCapabilitiesEvent.Entity event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            event.addCapability(new ResourceLocation(Possessed.MODID, "PossessedData"), new PossessCapability.Serializable(player));
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            EntityPlayer player = event.player;
            PossessivePlayer possessivePlayer = PossessHandler.get(player);
            if (possessivePlayer != null) {
                possessivePlayer.update(player, false);
                if (!player.worldObj.isRemote) {
                    EntityLivingBase possessing = possessivePlayer.getPossessing();
                    if (possessing.getDataManager().isDirty()) {
                        IMessage message = new UpdatePossessedDataMessage(player, possessing);
                        WorldServer worldServer = (WorldServer) player.worldObj;
                        for (EntityPlayer tracking : worldServer.getEntityTracker().getTrackingPlayers(player)) {
                            Possessed.getNetworkWrapper().sendTo(message, (EntityPlayerMP) tracking);
                        }
                        Possessed.getNetworkWrapper().sendTo(message, (EntityPlayerMP) player);
                    }
                }
            }
            PossessCapability possessCapability = PossessCapability.Implementation.get(player);
            int charge = possessCapability.getPossessiveCharge();
            if (possessivePlayer == null) {
                if (charge < PossessCapability.Implementation.MAXIMUM_CHARGE) {
                    ItemStack head = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
                    if (head != null && head.getItem() instanceof PossessiveHelmet) {
                        possessCapability.setPossessiveCharge(++charge);
                    }
                }
            } else {
                if (charge > 0) {
                    possessCapability.setPossessiveCharge(charge - 2);
                } else {
                    PossessHandler.possess(player, null);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (PossessHandler.isPossessing(player)) {
                PossessivePlayer possessivePlayer = PossessHandler.get(player);
                EntityLivingBase possessing = possessivePlayer.getPossessing();
                for (EntityPossessHandler possessHandler : PossessHandler.getPossessHandlers(possessing)) {
                    possessHandler.onDeath(possessivePlayer, player);
                }
                PossessHandler.possess(player, null);
                if (!player.worldObj.isRemote) {
                    possessing.onDeath(event.getSource());
                }
                event.setCanceled(true);
            }
            EntityPossessHandler.PLAYER_DATA.remove(player);
        }
        Entity sourceOfDamage = event.getSource().getSourceOfDamage();
        if (sourceOfDamage instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sourceOfDamage;
            ItemStack head = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            if ((head != null && head.getItem() instanceof PossessiveHelmet) || PossessHandler.isPossessing(player)) {
                PossessCapability possessCapability = PossessCapability.Implementation.get(player);
                possessCapability.setPossessiveCharge(Math.min(PossessCapability.Implementation.MAXIMUM_CHARGE, possessCapability.getPossessiveCharge() + 1000));
                if (player instanceof EntityPlayerMP) {
                    Possessed.getNetworkWrapper().sendTo(new PossessiveChargeMessage(player, possessCapability.getPossessiveCharge()), (EntityPlayerMP) player);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        EntityPlayer player = event.player;
        if (!player.worldObj.isRemote) {
            if (PossessHandler.isPossessing(player)) {
                PossessHandler.possess(player, null);
            }
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof EntityPlayerMP) {
            EntityPlayer player = event.getEntityPlayer();
            EntityPlayerMP target = (EntityPlayerMP) event.getTarget();
            if (!player.worldObj.isRemote && player instanceof EntityPlayerMP) {
                if (target.getHealth() > 0.0F) {
                    Possessed.getNetworkWrapper().sendTo(new PossessMessage(target, PossessHandler.get(target)), (EntityPlayerMP) player);
                }
                if (player.getHealth() > 0.0F) {
                    Possessed.getNetworkWrapper().sendTo(new PossessMessage(player, PossessHandler.get(player)), target);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            if (!player.worldObj.isRemote) {
                if (player instanceof EntityPlayerMP) {
                    Possessed.getNetworkWrapper().sendTo(new PossessMessage(player, PossessHandler.get(player)), (EntityPlayerMP) player);
                    Possessed.getNetworkWrapper().sendTo(new PossessiveChargeMessage(player, PossessCapability.Implementation.get(player).getPossessiveCharge()), (EntityPlayerMP) player);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        EntityPlayer player = event.getEntityPlayer();
        if (!player.worldObj.isRemote && event.getHand() == EnumHand.MAIN_HAND) {
            if (event.getTarget() instanceof EntityLivingBase && !(event.getTarget() instanceof EntityPlayer) && player.isSneaking()) {
                ItemStack head = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
                if ((head != null && head.getItem() instanceof PossessiveHelmet) || PossessHandler.isPossessing(player)) {
                    if (head != null) {
                        head.damageItem(2, player);
                    }
                    EntityLivingBase target = (EntityLivingBase) event.getTarget();
                    if (!(target instanceof EntityLiving && ((EntityLiving) target).isAIDisabled())) {
                        PossessHandler.possess(player, target);
                    }
                }
            } else if (event.getTarget() instanceof EntityPlayer) {
                PossessivePlayer possessivePlayer = PossessHandler.get((EntityPlayer) event.getTarget());
                if (possessivePlayer != null) {
                    ItemStack stack = event.getItemStack();
                    EntityLivingBase possessing = possessivePlayer.getPossessing();
                    EnumHand hand = event.getHand();
                    if ((stack != null && stack.getItem().itemInteractionForEntity(stack, player, possessing, hand)) || (possessing.processInitialInteract(player, stack, hand))) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onFall(LivingFallEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            PossessivePlayer possessivePlayer = PossessHandler.get((EntityPlayer) event.getEntityLiving());
            if (possessivePlayer != null) {
                possessivePlayer.getPossessing().fall(event.getDistance(), event.getDamageMultiplier());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            PossessivePlayer possessivePlayer = PossessHandler.get(player);
            if (possessivePlayer != null) {
                if (event.getSource().isFireDamage()) {
                    event.setCanceled(true);
                } else {
                    possessivePlayer.getPossessing().attackEntityFrom(event.getSource(), event.getAmount());
                    event.setCanceled(true);
                    if (!player.capabilities.isCreativeMode && !event.getSource().canHarmInCreative() && possessivePlayer.getPossessing().hurtTime <= 0) {
                        this.playPossessedHurtAnimation(player);
                    }
                }
            }
        } else {
            EntityPlayer possessor = PossessHandler.getPossesor(event.getEntityLiving());
            if (possessor != null) {
                if (possessor.capabilities.isCreativeMode && !event.getSource().canHarmInCreative()) {
                    event.setCanceled(true);
                } else if (possessor.hurtTime <= 0) {
                    this.playPossessedHurtAnimation(possessor);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            PossessivePlayer possessivePlayer = PossessHandler.get(player);
            if (possessivePlayer != null) {
                for (EntityPossessHandler handler : PossessHandler.getPossessHandlers(possessivePlayer.getPossessing())) {
                    handler.onJump(possessivePlayer, player);
                }
            }
        }
    }

    private void playPossessedHurtAnimation(EntityPlayer player) {
        if (player.worldObj instanceof WorldServer) {
            IMessage message = new PossessHurtMessage(player);
            for (EntityPlayer tracking : ((WorldServer) player.worldObj).getEntityTracker().getTrackingPlayers(player)) {
                Possessed.getNetworkWrapper().sendTo(message, (EntityPlayerMP) tracking);
            }
            Possessed.getNetworkWrapper().sendTo(message, (EntityPlayerMP) player);
            PossessivePlayer possessivePlayer = PossessHandler.get(player);
            EntityLivingBase possessing = possessivePlayer.getPossessing();
            if (possessing != null) {
                possessing.maxHurtTime = 10;
                possessing.hurtTime = possessing.maxHurtTime;
                possessing.attackedAtYaw = 0.0F;
            }
        }
    }

    @SubscribeEvent
    public void onLivingHeal(LivingHealEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            PossessivePlayer possessivePlayer = PossessHandler.get(player);
            if (possessivePlayer != null) {
                possessivePlayer.getPossessing().heal(event.getAmount());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onClickBlock(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        PossessivePlayer possessivePlayer = PossessHandler.get(player);
        if (possessivePlayer != null) {
            for (EntityPossessHandler handler : PossessHandler.getPossessHandlers(possessivePlayer.getPossessing())) {
                handler.onClickBlock(possessivePlayer, player);
            }
        }
    }

    @SubscribeEvent
    public void onClickItem(PlayerInteractEvent.RightClickItem event) {
        EntityPlayer player = event.getEntityPlayer();
        PossessivePlayer possessivePlayer = PossessHandler.get(player);
        if (possessivePlayer != null) {
            for (EntityPossessHandler handler : PossessHandler.getPossessHandlers(possessivePlayer.getPossessing())) {
                handler.onClickAir(possessivePlayer, player);
            }
        }
    }
}
