package com.ayd2.library.dto;

import java.util.Collection;

public class CollectionPage<T extends Collection<?>, Long> {

    public final T list;
    public final Long count;

    private CollectionPage(T list, Long count) {
        this.list = list;
        this.count = count;
    }

    public static <T extends Collection<?>, Long> CollectionPage<T, Long> of(T list, Long count) {
        return new CollectionPage<>(list, count);
    }
}
