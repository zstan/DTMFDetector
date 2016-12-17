package ru.amberdata.dtmf.configuration;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhenya on 2016-12-16.
 */
public class Channel {

    private String name;
    private String networkInterface;
    private String streamAddress;
    private String AudioPID;
    private String AudioFormat;
    private DTMFChannel dtmfChannel;
    private List<AdBreak> adBreak = new ArrayList<>();

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
    public String getAudioPID() {
        return AudioPID;
    }

    @XmlElement(required=true)
    public String getStreamAddress() {
        return streamAddress;
    }

    @XmlElement(required=true)
    public String getNetworkInterface() {
        return networkInterface;
    }

    @XmlElement(required=true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNetworkInterface(String networkInterface) {
        this.networkInterface = networkInterface;
    }

    public void setStreamAddress(String streamAddress) {
        this.streamAddress = streamAddress;
    }

    public void setAudioPID(String audioPID) {
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
}

enum DTMFChannel {
    auto, left, right, mono;
}

class AdBreak {
    private CueTone cueTone;

    @XmlElement(required=true)
    public CueTone getCueTone() {
        return cueTone;
    }

    public void setCueTone(CueTone cueTone) {
        this.cueTone = cueTone;
    }
}

class CueTone {

    private String stopSymbols;
    private String startSymbols;
    private Integer symbolLength;
    private Integer pauseLength;

    public String getStopSymbols() {
        return stopSymbols;
    }

    @XmlElement(required=true)
    public void setStopSymbols(String stopSymbols) {
        this.stopSymbols = stopSymbols;
    }

    @XmlElement(required=true)
    public String getStartSymbols() {
        return startSymbols;
    }

    public void setStartSymbols(String startSymbols) {
        this.startSymbols = startSymbols;
    }

    @XmlElement
    public Integer getSymbolLength() {
        return symbolLength;
    }

    public void setSymbolLength(Integer symbolLength) {
        this.symbolLength = symbolLength;
    }

    @XmlElement
    public Integer getPauseLength() {
        return pauseLength;
    }

    public void setPauseLength(Integer pauseLength) {
        this.pauseLength = pauseLength;
    }
}