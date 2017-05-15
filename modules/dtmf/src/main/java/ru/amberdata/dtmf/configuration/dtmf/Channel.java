package ru.amberdata.dtmf.configuration.dtmf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhenya on 2016-12-16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Channel {

    private String streamAddress;
    private Integer audioPID;
    private String audioFormat;
    private DTMFChannel dtmfChannel;
    private Double cutoffNoiseRatio = -1.;
    private Integer symbolLength;
    private Integer pauseLength;

    private List<AdBreak> adBreak = new ArrayList<>();

    @XmlAttribute(required = true)
    private Integer id;
    @XmlAttribute()
    private String name;
    @XmlTransient
    private ru.amberdata.dtmf.configuration.external.Elemental.Channel externalChannel;

    public Integer getSymbolLength() {
        return symbolLength;
    }

    void setSymbolLength(Integer symbolLength) {
        this.symbolLength = symbolLength;
    }

    public Integer getPauseLength() {
        return pauseLength;
    }

    void setPauseLength(Integer pauseLength) {
        this.pauseLength = pauseLength;
    }

    public List<AdBreak> getAdBreak() {
        return adBreak;
    }

    public DTMFChannel getDtmfChannel() {
        return dtmfChannel;
    }

    public String getAudioFormat() {
        return audioFormat;
    }

    public Integer getAudioPID() {
        return audioPID;
    }

    public String getStreamAddress() {
        try {
            URI u = new URI(streamAddress);
            return String.format("%s:%s", u.getHost(), u.getPort());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    void setStreamAddress(String streamAddress) {
        this.streamAddress = streamAddress;
    }

    void setAudioPID(Integer audioPID) {
        this.audioPID = audioPID;
    }

    void setAudioFormat(String audioFormat) {
        this.audioFormat = audioFormat;
    }

    void setDtmfChannel(DTMFChannel DTMFChannel) {
        this.dtmfChannel = DTMFChannel;
    }

    void setAdBreak(List<AdBreak> action) {
        this.adBreak = action;
    }

    public Double getCutoffNoiseRatio() {
        return cutoffNoiseRatio;
    }

    void setCutoffNoiseRatio(Double cutoffNoiseRatio) {
        this.cutoffNoiseRatio = cutoffNoiseRatio;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ru.amberdata.dtmf.configuration.external.Elemental.Channel getExternalChannel() {
        return externalChannel;
    }

    public void setExternalChannel(ru.amberdata.dtmf.configuration.external.Elemental.Channel externalChannel) {
        this.externalChannel = externalChannel;
    }

    public DTMFProtocol getProtocol() {
        try {
            URI u = new URI(streamAddress);
            return DTMFProtocol.valueOf(u.getScheme().toUpperCase());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public enum DTMFProtocol {
        UDP, RTP;
    }
}

enum DTMFChannel {
    auto, left, right, mono;
}