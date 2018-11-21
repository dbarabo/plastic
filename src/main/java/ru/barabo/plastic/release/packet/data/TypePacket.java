package ru.barabo.plastic.release.packet.data;

public enum TypePacket {

	REISSUE(0),
	RELEASE(1),
	SMS_ADD(2),
	SMS_REMOVE(3),
	BTRT25(4);

	private Integer dbValue;

	TypePacket(Integer dbValue) {
		this.dbValue = dbValue;
	}

	public Integer getDbValue() {
		return dbValue;
	}
}
