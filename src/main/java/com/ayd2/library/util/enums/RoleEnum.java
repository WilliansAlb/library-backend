package com.ayd2.library.util.enums;

public enum RoleEnum {

    LIBRARIAN("0"),
    STUDENT("1");

    public final String roleId;

    RoleEnum(String roleId) {
        this.roleId = roleId;
    }
}
