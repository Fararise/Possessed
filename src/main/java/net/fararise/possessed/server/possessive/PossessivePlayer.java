package net.fararise.possessed.server.possessive;

import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.entity.MockMoveHelper;
import net.fararise.possessed.server.entity.MockTask;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PossessivePlayer {
    public static final int POSSESS_ANIMATION_LENGTH = 30;

    private final EntityLivingBase possessing;
    private float originalHealth;
    private int originalHunger;
    private float originalSaturation;
    private InventoryPlayer originalInventory;
    private Container originalInventoryContainer;
    private EntityMoveHelper originalMoveHelper;

    private InventoryPlayer newInventory;
    private Container newInventoryContainer;
    private List<EntityPossessHandler> handlers;
    private List<Entity> prevPassengers;

    private Set<EntityAITasks.EntityAITaskEntry> taskEntries;
    private Set<EntityAITasks.EntityAITaskEntry> targetTaskEntries;

    private int possessAnimation;
    private boolean isPossessing = true;

    private double originalX;
    private double originalY;
    private double originalZ;

    public PossessivePlayer(EntityPlayer player, EntityLivingBase possessing, double originalX, double originalY, double originalZ) {
        this.possessing = possessing;
        this.originalHealth = player.getHealth();
        this.originalHunger = player.getFoodStats().getFoodLevel();
        this.originalSaturation = player.getFoodStats().getSaturationLevel();
        this.originalInventory = player.inventory;
        this.originalInventoryContainer = player.inventoryContainer;
        this.originalX = originalX;
        this.originalY = originalY;
        this.originalZ = originalZ;
        if (possessing instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) possessing;
            this.originalMoveHelper = living.getMoveHelper();
            this.setMoveHelper(new MockMoveHelper(living));
            this.taskEntries = new HashSet<>(living.tasks.taskEntries);
            this.targetTaskEntries = new HashSet<>(living.targetTasks.taskEntries);
            for (EntityAITasks.EntityAITaskEntry entry : this.taskEntries) {
                living.tasks.removeTask(entry.action);
            }
            for (EntityAITasks.EntityAITaskEntry entry : this.targetTaskEntries) {
                living.targetTasks.removeTask(entry.action);
            }
            living.tasks.addTask(0, new MockTask());
            living.targetTasks.addTask(0, new MockTask());
        } else {
            this.originalMoveHelper = null;
        }
        this.newInventory = new InventoryPlayer(player);
        this.newInventory.pickItem(player.inventory.currentItem);
        this.newInventoryContainer = new ContainerPlayer(this.newInventory, !player.worldObj.isRemote, player);
        this.handlers = PossessHandler.getPossessHandlers(possessing);
    }

    public void update(EntityPlayer player, boolean render) {
        this.updateAnimation(player);

        this.possessing.moveForward = player.moveForward;
        this.possessing.moveStrafing = player.moveStrafing;
        this.possessing.isCollided = player.isCollided;
        this.possessing.isCollidedHorizontally = player.isCollidedHorizontally;
        this.possessing.isCollidedVertically = player.isCollidedVertically;
        this.possessing.worldObj = player.worldObj;
        if (this.possessing instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) this.possessing;
            living.setAttackTarget(null);
        }
        if (!render) {
            PossessHandler.setSize(player, this.possessing.width, this.possessing.height);
            this.possessing.onUpdate();
            if (this.isPossessing) {
                for (EntityPossessHandler handler : this.handlers) {
                    handler.onUpdate(this, player);
                }
            }
        }
        this.possessing.rotationYaw = player.rotationYaw;
        this.possessing.prevRotationPitch = this.possessing.rotationPitch;
        this.possessing.rotationPitch = player.rotationPitch;
        this.possessing.rotationYawHead = player.rotationYawHead;
        this.possessing.renderYawOffset = player.renderYawOffset;
        this.possessing.prevRenderYawOffset = player.prevRenderYawOffset;
        this.possessing.prevRotationYaw = player.prevRotationYaw;
        this.possessing.prevRotationYawHead = player.prevRotationYawHead;
        this.possessing.setPosition(player.posX, player.posY, player.posZ);
        this.possessing.setSneaking(player.isSneaking());
        this.possessing.setSprinting(player.isSprinting());
        this.possessing.limbSwing = player.limbSwing;
        this.possessing.limbSwingAmount = player.limbSwingAmount;
        this.possessing.prevLimbSwingAmount = player.prevLimbSwingAmount;
        this.possessing.ticksExisted = player.ticksExisted;
        this.possessing.onGround = player.onGround;
        this.possessing.isAirBorne = player.isAirBorne;
        this.possessing.motionX = player.motionX;
        this.possessing.motionY = player.motionY;
        this.possessing.motionZ = player.motionZ;
        this.possessing.posX = player.posX;
        this.possessing.posY = player.posY;
        this.possessing.posZ = player.posZ;
        this.possessing.prevPosX = player.prevPosX;
        this.possessing.prevPosY = player.prevPosY;
        this.possessing.prevPosZ = player.prevPosZ;
        this.possessing.lastTickPosX = player.lastTickPosX;
        this.possessing.lastTickPosY = player.lastTickPosY;
        this.possessing.lastTickPosZ = player.lastTickPosZ;
        this.possessing.swingProgress = player.swingProgress;
        this.possessing.swingingHand = player.swingingHand;
        this.possessing.prevSwingProgress = player.prevSwingProgress;
        this.possessing.isSwingInProgress = player.isSwingInProgress;
        this.possessing.swingProgressInt = player.swingProgressInt;
        this.possessing.setEntityBoundingBox(player.getEntityBoundingBox());

        this.possessing.isDead = this.possessing.getHealth() <= 0.0F;

        if (this.isPossessing) {
            this.possessing.noClip = true;
            this.possessing.fallDistance = player.fallDistance;

            if (!player.worldObj.isRemote && !this.possessing.getPassengers().isEmpty()) {
                List<Entity> passengers = this.possessing.getPassengers();
                this.possessing.removePassengers();
                for (Entity passenger : passengers) {
                    passenger.startRiding(player);
                }
                if (player.worldObj instanceof WorldServer && player instanceof EntityPlayerMP) {
                    NetHandlerPlayServer connection = ((EntityPlayerMP) player).connection;
                    if (connection != null) {
                        connection.sendPacket(new SPacketSetPassengers(player));
                    }
                }
            } else if (!player.worldObj.isRemote && !player.getPassengers().equals(this.prevPassengers)) {
                if (player.worldObj instanceof WorldServer && player instanceof EntityPlayerMP) {
                    NetHandlerPlayServer connection = ((EntityPlayerMP) player).connection;
                    if (connection != null) {
                        connection.sendPacket(new SPacketSetPassengers(player));
                    }
                }
                this.prevPassengers = player.getPassengers();
            }

            player.getFoodStats().setFoodLevel(20);

            if (!player.worldObj.isRemote) {
                if (player.inventory != this.newInventory) {
                    player.inventory = this.newInventory;
                    player.setHeldItem(EnumHand.OFF_HAND, this.possessing.getHeldItem(EnumHand.OFF_HAND));
                    player.setHeldItem(EnumHand.MAIN_HAND, this.possessing.getHeldItem(EnumHand.MAIN_HAND));
                    player.setItemStackToSlot(EntityEquipmentSlot.HEAD, this.possessing.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
                    player.setItemStackToSlot(EntityEquipmentSlot.CHEST, this.possessing.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
                    player.setItemStackToSlot(EntityEquipmentSlot.LEGS, this.possessing.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
                    player.setItemStackToSlot(EntityEquipmentSlot.FEET, this.possessing.getItemStackFromSlot(EntityEquipmentSlot.FEET));
                    player.inventoryContainer = null;
                }
                if (player.inventoryContainer != this.newInventoryContainer) {
                    player.inventoryContainer = this.newInventoryContainer;
                    if (player instanceof EntityPlayerMP) {
                        try {
                            ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
                        } catch (Exception e) {
                        }
                    }
                }
            }

            this.possessing.setHeldItem(EnumHand.MAIN_HAND, player.getHeldItem(EnumHand.MAIN_HAND));
            this.possessing.setHeldItem(EnumHand.OFF_HAND, player.getHeldItem(EnumHand.OFF_HAND));
            this.possessing.setItemStackToSlot(EntityEquipmentSlot.HEAD, player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
            this.possessing.setItemStackToSlot(EntityEquipmentSlot.CHEST, player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
            this.possessing.setItemStackToSlot(EntityEquipmentSlot.LEGS, player.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
            this.possessing.setItemStackToSlot(EntityEquipmentSlot.FEET, player.getItemStackFromSlot(EntityEquipmentSlot.FEET));

            if (!player.worldObj.isRemote && player.openContainer instanceof ContainerPlayer && player.openContainer != player.inventoryContainer) {
                player.openContainer = player.inventoryContainer;
                if (player instanceof EntityPlayerMP) {
                    EntityPlayerMP playerMP = (EntityPlayerMP) player;
                    if (playerMP.connection != null) {
                        try {
                            playerMP.addSelfToInternalCraftingInventory();
                        } catch (Exception e) {
                        }
                    }
                }
            }

            IAttributeInstance entityMovementSpeed = this.possessing.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
            if (entityMovementSpeed != null) {
                double speed = Math.max(0.08, entityMovementSpeed.getBaseValue() * 0.2);
                for (EntityPossessHandler handler : this.handlers) {
                    double handlerSpeed = handler.getSpeed(this, player);
                    if (!(handlerSpeed < 0)) {
                        speed = handlerSpeed;
                        break;
                    }
                }
                player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(speed);
            }
            IAttributeInstance entityMaxHealth = this.possessing.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            if (entityMaxHealth != null) {
                player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(entityMaxHealth.getBaseValue());
            }
            IAttributeInstance entityDamage = this.possessing.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
            if (entityDamage != null) {
                player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(entityDamage.getBaseValue());
            }

            PossessHandler.setSize(player, player.width, player.height);

            player.setAir(this.possessing.getAir());
            player.stepHeight = this.possessing.stepHeight;
            player.eyeHeight = this.possessing.getEyeHeight();
            player.setHealth(Math.max(0.5F, this.possessing.getHealth()));
            player.hurtTime = this.possessing.hurtTime;
            player.hurtResistantTime = this.possessing.hurtResistantTime;
            player.maxHurtTime = this.possessing.maxHurtTime;
            player.maxHurtResistantTime = this.possessing.maxHurtResistantTime;
            player.attackedAtYaw = this.possessing.attackedAtYaw;

            if (this.possessing.isBurning() && !player.capabilities.isCreativeMode) {
                player.setFire(1);
            }
        }

        if (this.possessing.isDead && this.possessing.deathTime > 0) {
            this.originalX = player.posX;
            this.originalY = player.posY;
            this.originalZ = player.posZ;
            if (!this.possessing.worldObj.isRemote) {
                PossessHandler.possess(player, null);
                for (EntityPossessHandler possessHandler : this.handlers) {
                    possessHandler.onDeath(this, player);
                }
            }
        }
    }

    public void updateAnimation(EntityPlayer player) {
        if (this.isPossessing) {
            if (this.possessAnimation < PossessivePlayer.POSSESS_ANIMATION_LENGTH) {
                this.possessAnimation++;
            }
        } else {
            if (this.possessAnimation > 0) {
                this.possessAnimation--;
            } else {
                PossessHandler.removePossession(player);
            }
        }
    }

    public void stop(EntityPlayer player) {
        this.isPossessing = false;
        World world = player.worldObj;
        player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.10000000149011612D);
        player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
        player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
        player.setHealth(this.getOriginalHealth());
        if (!world.isRemote) {
            List<ItemStack> items = new ArrayList<>();
            Collections.addAll(items, player.inventory.mainInventory);
            Collections.addAll(items, player.inventory.armorInventory);
            Collections.addAll(items, player.inventory.offHandInventory);
            for (ItemStack item : items) {
                if (item != null && item.stackSize > 0) {
                    player.entityDropItem(item, 0.0F);
                }
            }
        }
        if (this.possessing instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) this.possessing;
            living.tasks.taskEntries.clear();
            living.tasks.taskEntries.addAll(this.taskEntries);
            living.targetTasks.taskEntries.clear();
            living.targetTasks.taskEntries.addAll(this.targetTaskEntries);
            this.setMoveHelper(this.originalMoveHelper != null ? this.originalMoveHelper : new EntityMoveHelper(living));
        }
        player.inventory = this.getOriginalInventory();
        player.inventoryContainer = this.getOriginalInventoryContainer();
        player.capabilities.allowFlying = player.capabilities.isCreativeMode;
        player.capabilities.isFlying = false;
        if (player.openContainer instanceof ContainerPlayer) {
            player.openContainer = player.inventoryContainer;
        }
        if (!world.isRemote && player instanceof EntityPlayerMP) {
            try {
                ((EntityPlayerMP) player).addSelfToInternalCraftingInventory();
            } catch (Exception e) {
            }
            ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
        }
        player.stepHeight = 0.6F;
        player.eyeHeight = player.getDefaultEyeHeight();
        player.setFire(0);
        player.getFoodStats().setFoodLevel(this.originalHunger);
        player.getFoodStats().setFoodSaturationLevel(this.originalSaturation);
        this.possessing.noClip = false;
        this.possessing.setHeldItem(EnumHand.MAIN_HAND, null);
        this.possessing.setHeldItem(EnumHand.OFF_HAND, null);
        this.possessing.setItemStackToSlot(EntityEquipmentSlot.HEAD, null);
        this.possessing.setItemStackToSlot(EntityEquipmentSlot.CHEST, null);
        this.possessing.setItemStackToSlot(EntityEquipmentSlot.LEGS, null);
        this.possessing.setItemStackToSlot(EntityEquipmentSlot.FEET, null);
        this.possessing.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
        if (!world.isRemote && !(this.possessing.isDead && this.possessing.deathTime >= 20)) {
            this.possessing.isDead = false;
            this.possessing.worldObj = world;
            world.spawnEntityInWorld(this.possessing);
        }
    }

    public EntityLivingBase getPossessing() {
        return this.possessing;
    }

    public float getOriginalHealth() {
        return this.originalHealth;
    }

    public int getOriginalHunger() {
        return this.originalHunger;
    }

    public float getOriginalSaturation() {
        return this.originalSaturation;
    }

    public InventoryPlayer getOriginalInventory() {
        return this.originalInventory;
    }

    public Container getOriginalInventoryContainer() {
        return this.originalInventoryContainer;
    }

    public int getPossessAnimation() {
        return this.possessAnimation;
    }

    public boolean isPossessing() {
        return this.isPossessing;
    }

    public void setMoveHelper(EntityMoveHelper helper) {
        if (this.possessing instanceof EntityLiving) {
            ReflectionHelper.setPrivateValue(EntityLiving.class, (EntityLiving) this.possessing, helper, 4);
        }
    }

    public void serialize(NBTTagCompound compound, EntityPlayer player) {
        compound.setFloat("OriginalHealth", this.originalHealth);
        compound.setInteger("OriginalHunger", this.originalHunger);
        compound.setFloat("OriginalSaturation", this.originalSaturation);
        compound.setBoolean("IsPossessing", this.isPossessing);
        compound.setInteger("PossessAnimation", this.possessAnimation);
        compound.setDouble("OriginalX", this.originalX);
        compound.setDouble("OriginalY", this.originalY);
        compound.setDouble("OriginalZ", this.originalZ);
        NBTTagList originalInventoryTag = new NBTTagList();
        this.originalInventory.writeToNBT(originalInventoryTag);
        compound.setTag("OriginalInventory", originalInventoryTag);
        NBTTagList handlerList = new NBTTagList();
        for (EntityPossessHandler possessHandler : this.handlers) {
            NBTTagCompound handlerTag = new NBTTagCompound();
            handlerTag.setString("Identifier", possessHandler.getIdentifier().toString());
            possessHandler.serialize(player, handlerTag);
            handlerList.appendTag(handlerTag);
        }
        compound.setTag("Handlers", handlerList);
    }

    public void deserialize(NBTTagCompound compound, EntityPlayer player) {
        this.originalHealth = compound.getFloat("OriginalHealth");
        this.originalHunger = compound.getInteger("OriginalHunger");
        this.originalSaturation = compound.getFloat("OriginalSaturation");
        this.isPossessing = compound.getBoolean("IsPossessing");
        this.possessAnimation = compound.getInteger("PossessAnimation");
        this.originalX = compound.getDouble("OriginalX");
        this.originalY = compound.getDouble("OriginalY");
        this.originalZ = compound.getDouble("OriginalZ");
        NBTTagList originalInventoryTag = compound.getTagList("OriginalInventory", 10);
        this.originalInventory = new InventoryPlayer(player);
        this.originalInventory.readFromNBT(originalInventoryTag);
        this.originalInventoryContainer = new ContainerPlayer(this.originalInventory, !player.worldObj.isRemote, player);
        NBTTagList handlerList = compound.getTagList("Handlers", 10);
        for (int i = 0; i < handlerList.tagCount(); i++) {
            NBTTagCompound handlerTag = handlerList.getCompoundTagAt(i);
            try {
                EntityPossessHandler possessHandler = PossessHandler.getPossessHandler(new ResourceLocation(handlerTag.getString("Identifier")));
                possessHandler.deserialize(player, handlerTag);
            } catch (Exception e) {
                System.err.println("Failed to load possess handler: " + handlerTag + "! Skipping.");
                e.printStackTrace();
            }
        }
    }

    public boolean isAnimating() {
        return this.isPossessing ? this.possessAnimation < PossessivePlayer.POSSESS_ANIMATION_LENGTH : this.possessAnimation > 0;
    }

    public double getOriginalX() {
        return this.originalX;
    }

    public double getOriginalY() {
        return this.originalY;
    }

    public double getOriginalZ() {
        return this.originalZ;
    }

    public void stopPossessing() {
        this.isPossessing = false;
    }

    public void setOriginalPosition(double originalX, double originalY, double originalZ) {
        this.originalX = originalX;
        this.originalY = originalY;
        this.originalZ = originalZ;
    }

    public boolean hasHandler(EntityPossessHandler handler) {
        return this.handlers.contains(handler);
    }
}
