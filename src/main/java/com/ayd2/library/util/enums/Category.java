package com.ayd2.library.util.enums;

public enum Category {

    TYPE_OF_FOOD(500L),
        TF_FOOD(501L),
        TF_DRINK(502L),
    PAYMENT_STATUS(510L),
        PS_PENDING(511L),
        PS_CANCELLED(512L);

    public final Long internalId;

    Category(Long internalId) {
        this.internalId = internalId;
    }
}
