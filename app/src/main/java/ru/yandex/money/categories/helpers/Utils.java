package ru.yandex.money.categories.helpers;

import java.util.Collection;

public class Utils {
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }
}
