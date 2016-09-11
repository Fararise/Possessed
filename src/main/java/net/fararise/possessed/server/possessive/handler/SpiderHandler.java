package net.fararise.possessed.server.possessive.handler;

import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class SpiderHandler implements EntityPossessHandler {
    @Override
    public void onUpdate(PossessivePlayer possessivePlayer, EntityPlayer player) {
        if (player.isCollidedHorizontally && !player.isOnLadder()) {
            player.motionX = MathHelper.clamp_double(player.motionX, -0.15, 0.15);
            player.motionZ = MathHelper.clamp_double(player.motionZ, -0.15, 0.15);
            player.fallDistance = 0.0F;
            boolean sneaking = player.isSneaking();
            if (sneaking && player.motionY < 0.0) {
                player.motionY = 0.0;
            } else {
                if (player.motionY < -0.15) {
                    player.motionY = -0.15;
                } else if (player.motionY >= 0.0 || player.onGround) {
                    player.motionY = 0.2;
                }
            }
        }
    }

    @Override
    public ResourceLocation getIdentifier() {
        return new ResourceLocation(Possessed.MODID, "spider");
    }

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return EntitySpider.class;
    }
}
