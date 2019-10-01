package com.ne.gs.model.templates.bonus;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "StatBonusType")
@XmlEnum
public enum StatBonusType {

    INVENTORY,
    POLISH;

    public String value() {
        return name();
    }

    public static StatBonusType fromValue(String v) {
        return valueOf(v);
    }

}