package com.gtnh.processingplus.materials;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.gtnh.processingplus.GTNHProcessingPlus;

import bartworks.system.material.Werkstoff;

final class PrPMaterialCompat {

    static final String GTNL_MATERIALS = "com.science.gtnl.common.material.GTNLMaterials";

    private PrPMaterialCompat() {}

    static Werkstoff registerOrReuse(List<Werkstoff> ownedMaterials, String fieldName, String displayName,
        Supplier<Werkstoff> localFactory, boolean deferForExternalHolder, String... holderClasses) {

        boolean externalHolderFieldExists = false;
        for (String holderClass : holderClasses) {
            try {
                Class<?> cls = Class.forName(holderClass);
                Field field = cls.getField(fieldName);
                externalHolderFieldExists = true;

                Object value = field.get(null);
                if (value instanceof Werkstoff) {
                    GTNHProcessingPlus.LOG.info("Reusing external Werkstoff '{}' from another mod", displayName);
                    return (Werkstoff) value;
                }
            } catch (ReflectiveOperationException | LinkageError ignored) {
                // Try the remaining holders and Werkstoff maps below.
            }
        }

        Werkstoff byVarName = getWerkstoffMapValue("werkstoffVarNameHashMap", fieldName);
        if (byVarName != null) {
            GTNHProcessingPlus.LOG.info("Reusing external Werkstoff '{}' from another mod", displayName);
            return byVarName;
        }

        Werkstoff byName = getWerkstoffMapValue("werkstoffNameHashMap", displayName);
        if (byName != null) {
            GTNHProcessingPlus.LOG.info("Reusing external Werkstoff '{}' from another mod", displayName);
            return byName;
        }

        if (deferForExternalHolder && externalHolderFieldExists) {
            GTNHProcessingPlus.LOG
                .info("Deferring external Werkstoff '{}' until its owner initializes it", displayName);
            return null;
        }

        Werkstoff local = localFactory.get();
        ownedMaterials.add(local);
        return local;
    }

    static boolean isExternal(Werkstoff material, String holderClass, String fieldName) {
        if (material == null) return false;
        try {
            Class<?> cls = Class.forName(holderClass);
            Field field = cls.getField(fieldName);
            Object value = field.get(null);
            return material == value;
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return false;
        }
    }

    private static Werkstoff getWerkstoffMapValue(String mapFieldName, String key) {
        try {
            Field field = Werkstoff.class.getField(mapFieldName);
            Object mapValue = field.get(null);
            if (!(mapValue instanceof Map)) return null;

            Object value = ((Map<?, ?>) mapValue).get(key);
            return value instanceof Werkstoff ? (Werkstoff) value : null;
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return null;
        }
    }
}
