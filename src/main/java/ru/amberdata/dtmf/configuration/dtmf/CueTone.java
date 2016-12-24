package ru.amberdata.dtmf.configuration.dtmf;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by zhenya on 2016-12-17.
 */
public class CueTone {

    private String stopSymbols;
    private String startSymbols;

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
}
