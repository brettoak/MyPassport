package com.brett.mypassport.common;

/**
 * Constants for Application Permissions (Authorities).
 * Usage in annotations: @PreAuthorize("hasAuthority(T(com.brett.mypassport.common.PermissionConstants).ROLE_VIEW)")
 */
public final class PermissionConstants {

    private PermissionConstants() {
        // Prevent instantiation
    }

    // User Management
    public static final String USER_VIEW = "USER_VIEW";
    public static final String USER_CREATE = "USER_CREATE";
    public static final String USER_UPDATE = "USER_UPDATE";
    public static final String USER_DELETE = "USER_DELETE";

    // Role Management
    public static final String ROLE_VIEW = "ROLE_VIEW";
    public static final String ROLE_CREATE = "ROLE_CREATE";
    public static final String ROLE_UPDATE = "ROLE_UPDATE";
    public static final String ROLE_DELETE = "ROLE_DELETE";
    public static final String ROLE_ASSIGN = "ROLE_ASSIGN";
    public static final String PERMISSION_VIEW = "PERMISSION_VIEW";

    // Device/Session Management
    public static final String DEVICE_VIEW = "DEVICE_VIEW";
    public static final String DEVICE_KICK = "DEVICE_KICK";

    // System Configuration
    public static final String SYS_CONFIG_VIEW = "SYS_CONFIG_VIEW";
    public static final String SYS_CONFIG_EDIT = "SYS_CONFIG_EDIT";
}
