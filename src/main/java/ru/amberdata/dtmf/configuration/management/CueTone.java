package ru.amberdata.dtmf.configuration.management;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by zhenya on 2016-12-24.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CueTone {
    @XmlAttribute(required = true)
    private Integer id;

    @XmlAttribute(required = true)
    private Integer commandTimeShift;

    @XmlAttribute(required = true)
    private Integer cueTimeShift;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCommandTimeShift() {
        return commandTimeShift;
    }

    public void setCommandTimeShift(Integer сommandTimeShift) {
        this.commandTimeShift = сommandTimeShift;
    }

    public Integer getCueTimeShift() {
        return cueTimeShift;
    }

    public void setCueTimeShift(Integer сueTimeShift) {
        this.cueTimeShift = сueTimeShift;
    }
}
