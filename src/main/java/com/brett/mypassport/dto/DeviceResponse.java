package com.brett.mypassport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Response object for device information")
public class DeviceResponse {

    @Schema(description = "IP Address of the device", example = "192.168.1.1")
    private String ipAddress;

    @Schema(description = "Device information (User-Agent)", example = "Mozilla/5.0 ...")
    private String deviceInfo;

    @Schema(description = "Last active time", example = "2023-10-27T10:00:00")
    private LocalDateTime lastActive;

    @Schema(description = "Whether this is the current session", example = "true")
    private boolean isCurrent;

    public DeviceResponse(String ipAddress, String deviceInfo, LocalDateTime lastActive, boolean isCurrent) {
        this.ipAddress = ipAddress;
        this.deviceInfo = deviceInfo;
        this.lastActive = lastActive;
        this.isCurrent = isCurrent;
    }

    // Getters and Setters
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public LocalDateTime getLastActive() {
        return lastActive;
    }

    public void setLastActive(LocalDateTime lastActive) {
        this.lastActive = lastActive;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }
}
