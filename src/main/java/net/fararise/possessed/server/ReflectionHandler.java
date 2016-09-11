package net.fararise.possessed.server;

import net.minecraft.entity.ai.EntityAINearestAttackableTarget;

import java.lang.reflect.Field;

public class ReflectionHandler {
    public static final Field TARGET_CLASS = ReflectionHandler.getField(EntityAINearestAttackableTarget.class, 0);

    public static Field getField(Class<?> clazz, int index) {
        Field field = clazz.getDeclaredFields()[index];
        field.setAccessible(true);
        return field;
    }
}
