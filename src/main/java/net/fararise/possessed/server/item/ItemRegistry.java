package net.fararise.possessed.server.item;

import net.fararise.possessed.Possessed;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemRegistry {
    public static final PossessiveHelmet POSSESSIVE_HELMET = new PossessiveHelmet();

    public static void onPreInit() {
        GameRegistry.register(ItemRegistry.POSSESSIVE_HELMET, new ResourceLocation(Possessed.MODID, "possessive_helmet"));

        GameRegistry.addRecipe(new ItemStack(ItemRegistry.POSSESSIVE_HELMET), "BDB", "GSG", "LZL", 'B', new ItemStack(Items.DYE, 1, 0), 'D', Items.DIAMOND, 'G', Items.GLOWSTONE_DUST, 'S', Items.NETHER_STAR, 'L', Items.LEATHER, 'Z', new ItemStack(Items.DYE, 1, 4));
    }
}
