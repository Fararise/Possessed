package net.fararise.possessed.server.capability;

import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.possessive.PossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;

public interface PossessCapability {
    void init(EntityPlayer player);

    void serialize(NBTTagCompound compound);

    void deserialize(NBTTagCompound compound);

    EntityPlayer getPlayer();

    int getPossessiveCharge();

    void setPossessiveCharge(int charge);

    class Implementation implements PossessCapability {
        public static final int MAXIMUM_CHARGE = 12000;

        private EntityPlayer player;
        private int possessiveCharge = Implementation.MAXIMUM_CHARGE;

        public static PossessCapability get(EntityPlayer player) {
            return player.getCapability(Possessed.getPlayerDataCapability(), null);
        }

        @Override
        public void init(EntityPlayer player) {
            this.player = player;
        }

        @Override
        public void serialize(NBTTagCompound compound) {
            PossessivePlayer possessivePlayer = PossessHandler.get(this.player);
            if (possessivePlayer != null && possessivePlayer.getPossessing() != null) {
                NBTTagCompound entityTag = new NBTTagCompound();
                entityTag.setString("id", EntityList.getEntityString(possessivePlayer.getPossessing()));
                possessivePlayer.getPossessing().writeToNBT(entityTag);
                compound.setTag("Entity", entityTag);
                possessivePlayer.serialize(compound, this.player);
            }
            compound.setInteger("PossessiveCharge", this.possessiveCharge);
        }

        @Override
        public void deserialize(NBTTagCompound compound) {
            Entity entity = EntityList.createEntityFromNBT(compound.getCompoundTag("Entity"), this.player.worldObj);
            if (entity instanceof EntityLivingBase) {
                PossessHandler.possess(this.player, (EntityLivingBase) entity);
                PossessivePlayer player = PossessHandler.get(this.player);
                player.deserialize(compound, this.player);
            }
            this.possessiveCharge = compound.getInteger("PossessiveCharge");
        }

        @Override
        public EntityPlayer getPlayer() {
            return this.player;
        }

        @Override
        public int getPossessiveCharge() {
            return this.possessiveCharge;
        }

        @Override
        public void setPossessiveCharge(int charge) {
            this.possessiveCharge = charge;
        }
    }

    class Storage implements Capability.IStorage<PossessCapability> {
        @Override
        public NBTBase writeNBT(Capability<PossessCapability> capability, PossessCapability implementation, EnumFacing side) {
            NBTTagCompound compound = new NBTTagCompound();
            implementation.serialize(compound);
            return compound;
        }

        @Override
        public void readNBT(Capability<PossessCapability> capability, PossessCapability implementation, EnumFacing side, NBTBase compound) {
            implementation.deserialize((NBTTagCompound) compound);
        }
    }

    class Factory implements Callable<PossessCapability> {
        @Override
        public PossessCapability call() throws Exception {
            return new PossessCapability.Implementation();
        }
    }

    class Serializable implements ICapabilitySerializable<NBTBase> {
        private EntityPlayer player;
        private PossessCapability instance;

        public Serializable(EntityPlayer player) {
            this.player = player;
            this.instance = Possessed.getPlayerDataCapability().getDefaultInstance();
        }

        @Override
        public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == Possessed.getPlayerDataCapability();
        }

        @Override
        public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
            if (this.hasCapability(capability, facing)) {
                Capability<PossessCapability> dataCapability = Possessed.getPlayerDataCapability();
                return dataCapability.cast(this.instance);
            }
            return null;
        }

        @Override
        public NBTBase serializeNBT() {
            Capability<PossessCapability> capability = Possessed.getPlayerDataCapability();
            this.instance.init(this.player);
            return capability.getStorage().writeNBT(capability, this.instance, null);
        }

        @Override
        public void deserializeNBT(NBTBase tag) {
            Capability<PossessCapability> capability = Possessed.getPlayerDataCapability();
            this.instance.init(this.player);
            capability.getStorage().readNBT(capability, this.instance, null, tag);
        }
    }
}
