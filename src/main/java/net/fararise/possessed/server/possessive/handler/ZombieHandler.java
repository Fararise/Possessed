package net.fararise.possessed.server.possessive.handler;

import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.possessive.PossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.ZombieType;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ZombieHandler implements EntityPossessHandler {
    @Override
    public void onUpdate(PossessivePlayer possessivePlayer, EntityPlayer player) {
    }

    @Override
    public ResourceLocation getIdentifier() {
        return new ResourceLocation(Possessed.MODID, "zombie");
    }

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return EntityZombie.class;
    }

    @Override
    public boolean isEventHandler() {
        return true;
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (event.getTarget() instanceof EntityLivingBase) {
            EntityLivingBase target = (EntityLivingBase) event.getTarget();
            EntityPlayer player = event.getEntityPlayer();
            if (this.isActive(player)) {
                PossessivePlayer possessivePlayer = PossessHandler.get(player);
                EntityZombie possessing = (EntityZombie) possessivePlayer.getPossessing();
                if (possessing.getZombieType() == ZombieType.HUSK) {
                    target.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 140));
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getSourceOfDamage() != null && event.getSource().getSourceOfDamage() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getSourceOfDamage();
            if (this.isActive(player) && event.getEntityLiving() instanceof EntityVillager) {
                EntityVillager villager = (EntityVillager) event.getEntityLiving();
                EntityZombie zombieVillager = new EntityZombie(player.worldObj);
                zombieVillager.copyLocationAndAnglesFrom(villager);
                player.worldObj.removeEntity(villager);
                zombieVillager.onInitialSpawn(player.worldObj.getDifficultyForLocation(new BlockPos(zombieVillager)), null);
                zombieVillager.setVillagerType(villager.getProfessionForge());
                zombieVillager.setChild(villager.isChild());
                zombieVillager.setNoAI(villager.isAIDisabled());
                if (villager.hasCustomName()) {
                    zombieVillager.setCustomNameTag(villager.getCustomNameTag());
                    zombieVillager.setAlwaysRenderNameTag(villager.getAlwaysRenderNameTag());
                }
                player.worldObj.spawnEntityInWorld(zombieVillager);
                player.worldObj.playEvent(null, 1026, zombieVillager.getPosition(), 0);
            }
        }
    }
}
