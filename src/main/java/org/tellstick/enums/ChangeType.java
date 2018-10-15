package org.tellstick.enums;

public enum ChangeType {
    ADDED(1),
    REMOVED(3),
    CHANGED(2),
    UPDATED(999);

    private int nativeInt;

    ChangeType(int nativeMethod) {
        this.nativeInt = nativeMethod;
    }

    public static ChangeType getChangeTypeById(int nativeId) {
        for (ChangeType m : ChangeType.values()) {
            if (m.nativeInt == nativeId) {
                return m;
            }
        }
        throw new RuntimeException("ChangeType not found for " + nativeId);
    }

}
