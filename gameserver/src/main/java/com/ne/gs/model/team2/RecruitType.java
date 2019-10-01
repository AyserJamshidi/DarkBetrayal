package com.ne.gs.model.team2;

public enum RecruitType {

    ASMO_APPLY_FOR_GROUP(0x04),
    DELETE_APPLY_FOR_GROUP(0x05),
    ELYOS_APPLY_FOR_GROUP(0x06);
    private int type;
    private int subType;

    private RecruitType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public boolean isAutoTeam() {
        return this.getType() == 0x02;
    }
}