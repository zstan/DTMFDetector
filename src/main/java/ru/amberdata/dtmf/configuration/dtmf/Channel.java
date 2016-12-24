package ru.amberdata.dtmf.configuration.dtmf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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

    public Integer getSymbolLength() {
        return symbolLength;
    }

    public void setSymbolLength(Integer symbolLength) {
        this.symbolLength = symbolLength;
    }

    public Integer getPauseLength() {
        return pauseLength;
    }

    public void setPauseLength(Integer pauseLength) {
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
        return streamAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStreamAddress(String streamAddress) {
        this.streamAddress = streamAddress;
    }

    public void setAudioPID(Integer audioPID) {
        this.audioPID = audioPID;
    }

    public void setAudioFormat(String audioFormat) {
        this.audioFormat = audioFormat;
    }

    public void setDtmfChannel(DTMFChannel DTMFChannel) {
        this.dtmfChannel = DTMFChannel;
    }

    public void setAdBreak(List<AdBreak> action) {
        this.adBreak = action;
    }

    public Double getCutoffNoiseRatio() {
        return cutoffNoiseRatio;
    }

    public void setCutoffNoiseRatio(Double cutoffNoiseRatio) {
        this.cutoffNoiseRatio = cutoffNoiseRatio;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

enum DTMFChannel {
    auto, left, right, mono;
}