package ru.barabo.plastic.release.packet.data;

public enum StatePlasticPacket {

	NEW("Новый", 0),
	OUT("Выпущен", 1),
	SENT("Отправлен", 2),
	SENT_OK("Отправка Ок", 3),
	SENT_ERROR("Отправка Error", 4),
	RESPONSE_OK_ALL("Ответ Всё Ок", 5),
	RESPONSE_OK_PART("Ответ Част Ок", 6),
	RESPONSE_ERROR_ALL("Error-Ответ Всё", 7),
	RESPONSE_ERROR_PART("Error-Ответ Част", 8),
	OCI_ALL("OCI-ВСЁ", 9),
	OCI_PART("OCI-Част", 10),
	SMS_SENT("SMS-Отправка", 11),
	SMS_SENT_ACCESS("SMS-Ok", 12),
	SMS_SENT_ERROR("SMS-Error", 13),
	SMS_RESPONSE_OK_ALL_OIA("SMS-Oтвет-Оk", 14),
	SMS_RESPONSE_OK_PART_OIA("SMS-Ответ Част Ок", 15),
	SMS_RESPONSE_ERROR_ALL_OIA("SMS-Oтвет-Error", 16),
	SMS_RESPONSE_ERROR_PART_OIA("SMS-Oтвет-Error Част", 17),
	CARD_GO("Карты в ГО", 18),
	CARD_SENT_OFFICCES("Ушли в доп.офисы", 19),
	CARD_HOME_OFFICCES("Карты в Доп. офисах", 20),
	CARD_TO_CLIENT("Выдано клиенту", 21),
	PREPARE_CARD_TO_OUT("Подготовить к выдаче", 22);
	
	private String label;

	private int dbValue;

	StatePlasticPacket(String label, int dbValue) {
		this.label = label;
		this.dbValue = dbValue;
	}

	public Integer getDbValue() {
		return dbValue;
	}
	
	public String getLabel() {
		return label;
	}
	
	static public StatePlasticPacket getMinValue() {
		return StatePlasticPacket.values()[0];
	}
	
	static public StatePlasticPacket getMaxValue() {
		return StatePlasticPacket.values()[StatePlasticPacket.values().length - 1];
	}

	static public StatePlasticPacket getStateByDbValue(int dbValue) {

		for(StatePlasticPacket value : StatePlasticPacket.values()) {
			if(value.dbValue == dbValue) {
				return value;
			}
		}
		return null;
	}
	
	static public boolean isErrorState(StatePlasticPacket statePlasticPacket) {
		return(statePlasticPacket == SENT_ERROR ||
		   statePlasticPacket == RESPONSE_ERROR_ALL ||
		   statePlasticPacket == RESPONSE_ERROR_PART ||
		   statePlasticPacket == SMS_RESPONSE_ERROR_ALL_OIA ||
		   statePlasticPacket == SMS_RESPONSE_ERROR_PART_OIA);
	}

	static public String[] labels() {
		String[] list = new String[StatePlasticPacket.values().length];

		for(int index = 0; index < StatePlasticPacket.values().length; index++) {
			list[index] = StatePlasticPacket.values()[index].label;
		}

		return list;
	}

	static public Integer[] dbValues() {
		Integer[] list = new Integer[StatePlasticPacket.values().length];

		for(int index = 0; index < StatePlasticPacket.values().length; index++) {
			list[index] = StatePlasticPacket.values()[index].getDbValue();
		}
		return list;
	}
}
