package ru.amberdata.dtmf.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by zhenya on 2016-12-17.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AdBreak {
    private CueTone cueTone;
    @XmlAttribute()
    private Integer id;

    public CueTone getCueTone() {
        return cueTone;
    }

    public void setCueTone(CueTone cueTone) {
        this.cueTone = cueTone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
