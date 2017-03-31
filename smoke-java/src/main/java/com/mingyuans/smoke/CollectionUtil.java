package com.mingyuans.smoke;

import java.util.Collection;

/**
 * Created by yanxq on 2017/3/2.
 */

public class CollectionUtil {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <E,T extends Collection<E>> void addAll(T dst, T src) {
        if (!isEmpty(src) && dst != null) {
            dst.addAll(src);
        }
    }
}
