package net.fararise.possessed.server.capability;

import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.possessive.PossessHandler;
import net.fararise.possessed.server.possessive.PossessionExperience;
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

    float getPossessTime();

    void setPossessTime(float time);

    PossessionExperience getExperience();

    void setExperience(PossessionExperience experience);

    class Implementation implements PossessCapability {
        public static final float BASE_TIME = 12000;
        private static final int VERSION = 1;

        private EntityPlayer player;
        private float possessTime = Implementation.BASE_TIME;
        private PossessionExperience experience = new PossessionExperience();

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
            compound.setFloat("PossessTime", this.possessTime);
            compound.setShort("DataVersion", (short) VERSION);
            this.experience.serialize(compound);
        }

        @Override
        public void deserialize(NBTTagCompound compound) {
            int version = 0;
            if (compound.hasKey("DataVersion")) {
                version = compound.getShort("DataVersion");
            }
            Entity entity = EntityList.createEntityFromNBT(compound.getCompoundTag("Entity"), this.player.worldObj);
            if (entity instanceof EntityLivingBase) {
                PossessHandler.possess(this.player, (EntityLivingBase) entity);
                PossessivePlayer player = PossessHandler.get(this.player);
                if (player != null) {
                    player.deserialize(compound, this.player);
                }
            }
            this.possessTime = version == 0 ? compound.getInteger("PossessiveCharge") : compound.getFloat("PossessTime");
            if (version >= 1) {
                this.experience.deserialize(compound);
            }
        }

        @Override
        public EntityPlayer getPlayer() {
            return this.player;
        }

        @Override
        public float getPossessTime() {
            return this.possessTime;
        }

        @Override
        public void setPossessTime(float time) {
            this.possessTime = time;
        }

        @Override
        public PossessionExperience getExperience() {
            return this.experience;
        }

        @Override
        public void setExperience(PossessionExperience experience) {
            this.experience = experience;
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
