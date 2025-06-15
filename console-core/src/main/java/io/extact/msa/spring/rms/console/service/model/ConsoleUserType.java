package io.extact.msa.spring.rms.console.service.model;

import io.extact.msa.spring.platform.fw.domain.model.ValueModel;

public enum ConsoleUserType implements ValueModel {

    ADMIN(true), MEMBER(false);

    boolean admin;

    private ConsoleUserType(boolean admin) {
        this.admin = admin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public static boolean isValidUserType(String userTypeName) {
        try {
            ConsoleUserType.valueOf(userTypeName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
