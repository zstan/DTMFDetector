package ru.amberdata.dtmf.configuration.management;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhenya on 2016-12-24.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public final class Channel {
    @XmlAttribute(required = true)
    private Integer id;

    @XmlAttribute()
    private String name;

    private String serverAddress, userName, authKey;
    private int externalID;
    private List<AdBreak> adBreak = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public int getExternalID() {
        return externalID;
    }

    public void setExternalID(int externalID) {
        this.externalID = externalID;
    }

    public List<AdBreak> getAdBreak() {
        return adBreak;
    }

    public void setAdBreak(List<AdBreak> adBreak) {
        this.adBreak = adBreak;
    }
}
