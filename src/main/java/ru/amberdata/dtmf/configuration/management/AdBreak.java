package ru.amberdata.dtmf.configuration.management;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by zhenya on 2016-12-24.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public final class AdBreak {
    @XmlAttribute(required = true)
    private Integer id;

    private CueTone cueTone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    //@XmlElement(required=true)
    public CueTone getCueTone() {
        return cueTone;
    }

    public void setCueTone(CueTone cueTone) {
        this.cueTone = cueTone;
    }
}
