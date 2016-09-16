package net.fararise.possessed.server.tab;

import net.fararise.possessed.server.item.ItemRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class TabRegistry {
    public static final CreativeTabs POSSESSED_TAB = new CreativeTabs("possessed") {
        @Override
        public Item getTabIconItem() {
            return ItemRegistry.POSSESSIVE_HELMET;
        }
    };
}
