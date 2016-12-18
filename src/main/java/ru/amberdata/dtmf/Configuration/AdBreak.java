package ru.amberdata.dtmf.configuration;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by zhenya on 2016-12-17.
 */
public class AdBreak {
    private CueTone cueTone;

    @XmlElement(required=true)
    public CueTone getCueTone() {
        return cueTone;
    }

    public void setCueTone(CueTone cueTone) {
        this.cueTone = cueTone;
    }
}
