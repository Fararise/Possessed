package net.fararise.possessed.server.item;

import net.fararise.possessed.server.tab.TabRegistry;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPossessitite extends Item {
    public ItemPossessitite() {
        super();
        this.setCreativeTab(TabRegistry.POSSESSED_TAB);
        this.setUnlocalizedName("possessitite");
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }
}
