package com.tencent.newhb.grabings.entity;

/**
 * Created by MSI05 on 2017/3/8.
 */
public class UpdateApk {

    private String needupdate;
    private String version;
    private String lastupdate;
    private String updatenote;
    private String filesize;
    private String downurl;
    private String forceupdate;

    public String getNeedupdate() {
        return needupdate;
    }

    public void setNeedupdate(String needupdate) {
        this.needupdate = needupdate;
    }

    public String getForceupdate() {
        return forceupdate;
    }

    public void setForceupdate(String forceupdate) {
        this.forceupdate = forceupdate;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public String getUpdatenote() {
        return updatenote;
    }

    public void setUpdatenote(String updatenote) {
        this.updatenote = updatenote;
    }

    public String getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(String lastupdate) {
        this.lastupdate = lastupdate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDownurl() {
        return downurl;
    }

    public void setDownurl(String downurl) {
        this.downurl = downurl;
    }
}
