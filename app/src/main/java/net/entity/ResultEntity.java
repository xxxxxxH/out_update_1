package net.entity;

import java.io.Serializable;

public class ResultEntity implements Serializable {
    int status;
    String ukey;
    String pkey;
    String ikey;
    String path;
    String oPack;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUkey() {
        return ukey;
    }

    public void setUkey(String ukey) {
        this.ukey = ukey;
    }

    public String getPkey() {
        return pkey;
    }

    public void setPkey(String pkey) {
        this.pkey = pkey;
    }

    public String getIkey() {
        return ikey;
    }

    public void setIkey(String ikey) {
        this.ikey = ikey;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getoPack() {
        return oPack;
    }

    public void setoPack(String oPack) {
        this.oPack = oPack;
    }
}
