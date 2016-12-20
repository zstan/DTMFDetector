package ru.amberdata.dtmf.configuration;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by zhenya on 2016-12-17.
 */
public class CueTone {

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
