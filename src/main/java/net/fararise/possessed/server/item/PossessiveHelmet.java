package net.fararise.possessed.server.item;

import net.fararise.possessed.Possessed;
import net.fararise.possessed.client.model.PossessiveHatModel;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PossessiveHelmet extends ItemArmor {
    public static final ArmorMaterial MATERIAL = EnumHelper.addArmorMaterial("possessive", Possessed.MODID + ":textures/armor/possessive.png", 7, new int[] { 1, 3, 5, 2 }, 25, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0.0F);
    private static final PossessiveHatModel MODEL = new PossessiveHatModel();
    private static final String TEXTURE = Possessed.MODID + ":textures/armor/possessive_hat.png";

    public PossessiveHelmet() {
        super(PossessiveHelmet.MATERIAL, 5, EntityEquipmentSlot.HEAD);
        this.setCreativeTab(CreativeTabs.COMBAT);
        this.setUnlocalizedName("possessive_helmet");
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return PossessiveHelmet.TEXTURE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entity, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped defaultModel) {
        return PossessiveHelmet.MODEL;
    }
}
