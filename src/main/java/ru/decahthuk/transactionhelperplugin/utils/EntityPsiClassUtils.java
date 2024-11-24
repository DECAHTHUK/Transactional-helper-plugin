package ru.decahthuk.transactionhelperplugin.utils;

public final class EntityPsiClassUtils {

    private EntityPsiClassUtils() {
    }


    public static String convertFieldNameToGetter(String fieldName) {
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
}
