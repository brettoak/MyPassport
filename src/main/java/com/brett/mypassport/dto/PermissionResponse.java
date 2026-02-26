package com.brett.mypassport.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object for permission details")
public class PermissionResponse {

    @Schema(description = "The unique identifier of the permission", example = "1")
    private Long id;

    @Schema(description = "The unique name of the permission", example = "USER_CREATE")
    private String name;

    @Schema(description = "A description of what this permission allows", example = "Allows creating new users")
    private String description;

    @Schema(description = "The module this permission belongs to", example = "USER_MANAGEMENT")
    private String module;

    @Schema(description = "The system this permission belongs to", example = "sys-b")
    private String sysCode;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getSysCode() {
        return sysCode;
    }

    public void setSysCode(String sysCode) {
        this.sysCode = sysCode;
    }
}
