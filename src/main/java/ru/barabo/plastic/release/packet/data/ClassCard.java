package ru.barabo.plastic.release.packet.data;

public enum ClassCard {
    Any(0),
    CreditCard(1),
    PlatinaCard(2);

    private int dbValue;

    ClassCard(int dbValue) {
        this.dbValue = dbValue;
    }

    static public ClassCard classCardByDbValue(int dbValue) {
        for(ClassCard field : ClassCard.values()) {
            if(field.dbValue == dbValue) {
                return field;
            }
        }
        return null;
    }
}
