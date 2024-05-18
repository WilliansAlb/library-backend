package com.ayd2.library.util.enums;

public enum RoleEnum {

    MANAGER("5001"),
    RECEPTION("5002"),
    RESTAURANT("5003");

    public final String roleId;

    RoleEnum(String roleId) {
        this.roleId = roleId;
    }
}
