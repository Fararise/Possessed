package net.fararise.possessed.server.possessive;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class PossessionExperience {
    public static final int MAXIMUM_EXPERIENCE = 8000;

    private Map<String, Integer> experience = new HashMap<>();

    public void setExperience(Class<? extends EntityLivingBase> entity, int experience) {
        String name = EntityList.getEntityStringFromClass(entity);
        if (name != null) {
            this.experience.put(name, Math.max(0, Math.min(MAXIMUM_EXPERIENCE, experience)));
        }
    }

    public void incrementExperience(Class<? extends EntityLivingBase> entity, int amount) {
        this.setExperience(entity, this.getExperience(entity) + amount);
    }

    public int getExperience(Class<? extends EntityLivingBase> entity) {
        String name = EntityList.getEntityStringFromClass(entity);
        if (name != null) {
            return this.getExperience(name);
        }
        return 0;
    }

    public int getExperience(String entity) {
        if (this.experience.containsKey(entity)) {
            return this.experience.get(entity);
        }
        return 0;
    }

    public void deserialize(NBTTagCompound compound) {
        this.experience.clear();
        NBTTagList possessionExperience = compound.getTagList("PossessionExperience", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < possessionExperience.tagCount(); i++) {
            NBTTagCompound tag = possessionExperience.getCompoundTagAt(i);
            this.experience.put(tag.getString("Type"), (int) tag.getShort("Experience"));
        }
    }

    public void serialize(NBTTagCompound compound) {
        NBTTagList possessionExperience = new NBTTagList();
        for (Map.Entry<String, Integer> entry : this.experience.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("Type", entry.getKey());
            tag.setShort("Experience", (short) entry.getValue().intValue());
            possessionExperience.appendTag(tag);
        }
        compound.setTag("PossessionExperience", possessionExperience);
    }

    public Map<String, Integer> getAllExperience() {
        return this.experience;
    }

    public float getRate(Class<? extends EntityLivingBase> entity) {
        return 1.0F / (1.0F + (((float) this.getExperience(entity) / MAXIMUM_EXPERIENCE) * 4.0F));
    }
}
