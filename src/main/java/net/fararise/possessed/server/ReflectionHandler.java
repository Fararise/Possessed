package net.fararise.possessed.server;

import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.util.FoodStats;

import java.lang.reflect.Field;

public class ReflectionHandler {
    public static final Field TARGET_CLASS = ReflectionHandler.getField(EntityAINearestAttackableTarget.class, 0);
    public static final Field FOOD_SATURATION_LEVEL = ReflectionHandler.getField(FoodStats.class, 1);

    public static Field getField(Class<?> clazz, int index) {
        Field field = clazz.getDeclaredFields()[index];
        field.setAccessible(true);
        return field;
    }
}
