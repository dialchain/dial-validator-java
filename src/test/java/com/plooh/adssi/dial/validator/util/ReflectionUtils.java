package com.plooh.adssi.dial.validator.util;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Objects;
import lombok.SneakyThrows;

public class ReflectionUtils {

    private static final VarHandle MODIFIERS;

    static {
        try {
            var lookup = MethodHandles.privateLookupIn(Field.class, MethodHandles.lookup());
            MODIFIERS = lookup.findVarHandle(Field.class, "modifiers", int.class);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }
    }

    private ReflectionUtils() {
    }

    @SneakyThrows
    public static void setFinalFieldValue(Object bean, String fieldName, Object newValue) {
        Field field = org.springframework.util.ReflectionUtils.findField(bean.getClass(), fieldName);
        Objects.requireNonNull(field, MessageFormat.format(
            "Field {0} does not exist in the class {1}!", fieldName, bean.getClass().getName()));
        field.setAccessible(true);

        makeNonFinal(field);

        field.set(bean, newValue);
    }

    public static void makeNonFinal(Field field) {
        int mods = field.getModifiers();
        if (Modifier.isFinal(mods)) {
            MODIFIERS.set(field, mods & ~Modifier.FINAL);
        }
    }

}
