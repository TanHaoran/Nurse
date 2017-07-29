package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/7/29.
 */

public class AppVersion {

    private String VersionId;
    private String Version;
    private String ReleaseUrl;
    private String ReleaseTime;

    public String getVersionId() {
        return VersionId;
    }

    public void setVersionId(String versionId) {
        VersionId = versionId;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public String getReleaseUrl() {
        return ReleaseUrl;
    }

    public void setReleaseUrl(String releaseUrl) {
        ReleaseUrl = releaseUrl;
    }

    public String getReleaseTime() {
        return ReleaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        ReleaseTime = releaseTime;
    }
}
