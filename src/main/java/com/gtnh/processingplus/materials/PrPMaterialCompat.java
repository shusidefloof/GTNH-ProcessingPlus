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
        Supplier<Werkstoff> localFactory, String... holderClasses) {

        Werkstoff external = findExternal(fieldName, displayName, holderClasses);
        if (external != null) {
            GTNHProcessingPlus.LOG.info("Reusing external Werkstoff '{}' from another mod", displayName);
            return external;
        }

        Werkstoff local = localFactory.get();
        ownedMaterials.add(local);
        return local;
    }

    static boolean isExternal(Werkstoff material, String holderClass, String fieldName) {
        if (material == null) return false;
        return material == getHolderField(holderClass, fieldName);
    }

    private static Werkstoff findExternal(String fieldName, String displayName, String... holderClasses) {
        for (String holderClass : holderClasses) {
            Werkstoff fromHolder = getHolderField(holderClass, fieldName);
            if (fromHolder != null) return fromHolder;
        }

        Werkstoff byVarName = getWerkstoffMapValue("werkstoffVarNameHashMap", fieldName);
        if (byVarName != null) return byVarName;

        return getWerkstoffMapValue("werkstoffNameHashMap", displayName);
    }

    private static Werkstoff getHolderField(String holderClass, String fieldName) {
        try {
            Class<?> cls = Class.forName(holderClass);
            Field field = cls.getField(fieldName);
            Object value = field.get(null);
            return value instanceof Werkstoff ? (Werkstoff) value : null;
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return null;
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
