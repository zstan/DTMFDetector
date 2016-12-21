package ru.amberdata.dtmf.configuration;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhenya on 2016-12-16.
 */
public class Channel {

    private String name;
    private String streamAddress;
    private Integer AudioPID;
    private String AudioFormat;
    private DTMFChannel dtmfChannel;
    private Double cutoffNoiseRatio = -1.;
    private Integer symbolLength;
    private Integer pauseLength;

    private List<AdBreak> adBreak = new ArrayList<>();

    @XmlElement(required=true)
    public Integer getSymbolLength() {
        return symbolLength;
    }

    public void setSymbolLength(Integer symbolLength) {
        this.symbolLength = symbolLength;
    }

    @XmlElement(required=true)
    public Integer getPauseLength() {
        return pauseLength;
    }

    public void setPauseLength(Integer pauseLength) {
        this.pauseLength = pauseLength;
    }

    @XmlElement(required=true)
    public List<AdBreak> getAdBreak() {
        return adBreak;
    }

    @XmlElement(required=true)
    public DTMFChannel getDtmfChannel() {
        return dtmfChannel;
    }

    @XmlElement(required=true)
    public String getAudioFormat() {
        return AudioFormat;
    }

    @XmlElement(required=true)
    public Integer getAudioPID() {
        return AudioPID;
    }

    @XmlElement(required=true)
    public String getStreamAddress() {
        return streamAddress;
    }

    @XmlElement(required=true)
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
        AudioPID = audioPID;
    }

    public void setAudioFormat(String audioFormat) {
        AudioFormat = audioFormat;
    }

    public void setDtmfChannel(DTMFChannel DTMFChannel) {
        this.dtmfChannel = DTMFChannel;
    }

    public void setAdBreak(List<AdBreak> action) {
        this.adBreak = action;
    }

    @XmlElement
    public Double getCutoffNoiseRatio() {
        return cutoffNoiseRatio;
    }

    public void setCutoffNoiseRatio(Double cutoffNoiseRatio) {
        this.cutoffNoiseRatio = cutoffNoiseRatio;
    }
}

enum DTMFChannel {
    auto, left, right, mono;
}