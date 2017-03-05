package com.mingyuans.smoke;

import java.util.Collection;

/**
 * Created by yanxq on 2017/3/2.
 */

class CollectionUtil {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <E,T extends Collection<E>> T clone(T src) {
        T newCollection = null;
        try {
            if (src != null) {
                newCollection = (T)src.getClass().newInstance();
                if (!src.isEmpty()) {
                    for(E item : src) {
                        newCollection.add(item);
                    }
                }
            }
        } catch (Exception e) {

        }

        return newCollection;
    }

    public static <E,T extends Collection<E>> void addAll(Collection<E> src, T dst) {
        if (!isEmpty(src) && dst != null) {
            dst.addAll(src);
        }
    }

}
