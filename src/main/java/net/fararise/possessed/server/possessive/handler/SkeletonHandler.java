package net.fararise.possessed.server.possessive.handler;

import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.possessive.PossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.SkeletonType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SkeletonHandler implements EntityPossessHandler {
    @Override
    public void onUpdate(PossessivePlayer possessivePlayer, EntityPlayer player) {
        EntitySkeleton possessing = (EntitySkeleton) possessivePlayer.getPossessing();
        ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
        if (heldItem != null && heldItem.getItem() == Items.BOW) {
            possessing.setSwingingArms(player.getItemInUseCount() > 0);
        } else {
            possessing.setSwingingArms(heldItem != null && heldItem.getItem().isFull3D());
        }
    }

    @Override
    public ResourceLocation getIdentifier() {
        return new ResourceLocation(Possessed.MODID, "skeleton");
    }

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return EntitySkeleton.class;
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
                EntitySkeleton possessing = (EntitySkeleton) possessivePlayer.getPossessing();
                if (possessing.getSkeletonType() == SkeletonType.WITHER) {
                    target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 100, 1));
                }
            }
        }
    }

    @SubscribeEvent
    public void onArrowNock(ArrowNockEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (this.isActive(player)) {
            if (!event.hasAmmo()) {
                player.setActiveHand(event.getHand());
                event.setAction(ActionResult.newResult(EnumActionResult.SUCCESS, event.getBow()));
            }
        }
    }

    @SubscribeEvent
    public void onArrowLoose(ArrowLooseEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (this.isActive(player)) {
            if (!event.hasAmmo()) {
                ItemStack stack = event.getBow();
                World world = player.getEntityWorld();
                EntitySkeleton skeleton = (EntitySkeleton) PossessHandler.get(player).getPossessing();
                float velocity = ItemBow.getArrowVelocity(event.getCharge());
                if (velocity >= 0.1) {
                    if (!world.isRemote) {
                        EntityTippedArrow arrow = new EntityTippedArrow(world, player);
                        if (skeleton.getSkeletonType() != SkeletonType.STRAY) {
                            arrow.setPotionEffect(new ItemStack(Items.ARROW));
                        } else {
                            arrow.setPotionEffect(new ItemStack(Items.TIPPED_ARROW));
                            arrow.addEffect(new PotionEffect(MobEffects.SLOWNESS, 600));
                        }
                        arrow.setAim(player, player.rotationPitch, player.rotationYaw, 0.0F, velocity * 3.0F, 1.0F);
                        arrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                        if (velocity == 1.0F) {
                            arrow.setIsCritical(true);
                        }
                        int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
                        if (power > 0) {
                            arrow.setDamage(arrow.getDamage() + (double) power * 0.5 + 0.5);
                        }
                        int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
                        if (punch > 0) {
                            arrow.setKnockbackStrength(punch);
                        }
                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
                            arrow.setFire(100);
                        }
                        stack.damageItem(1, player);
                        world.spawnEntityInWorld(arrow);
                    }
                    world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 1.2F) + velocity * 0.5F);
                    player.addStat(StatList.getObjectUseStats(stack.getItem()));
                }
            }
        }
    }
}
