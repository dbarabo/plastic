package ru.barabo.plastic.release.packet.data;

public enum StatePlasticPacket {

	NEW("Новый"),
	OUT("Выпущен"),
	SENT("Отправлен"),
	SENT_OK("Отправка Ок"),
	SENT_ERROR("Отправка Error"),
	RESPONSE_OK_ALL("Ответ Всё Ок"),
	RESPONSE_OK_PART("Ответ Част Ок"),
	RESPONSE_ERROR_ALL("Error-Ответ Всё"),
	RESPONSE_ERROR_PART("Error-Ответ Част"),
	OCI_ALL("OCI-ВСЁ"),
	OCI_PART("OCI-Част"),
	SMS_SENT("SMS-Отправка"),
	SMS_SENT_ACCESS("SMS-Ok"),
	SMS_SENT_ERROR("SMS-Error"),
	SMS_RESPONSE_OK_ALL_OIA("SMS-Oтвет-Оk"),
	SMS_RESPONSE_OK_PART_OIA("SMS-Ответ Част Ок"),
	SMS_RESPONSE_ERROR_ALL_OIA("SMS-Oтвет-Error"),
	SMS_RESPONSE_ERROR_PART_OIA("SMS-Oтвет-Error Част"),
	CARD_GO("Карты в ГО"),
	CARD_SENT_OFFICCES("Ушли в доп.офисы"),
	CARD_HOME_OFFICCES("Карты в Доп. офисах"),
	CARD_TO_CLIENT("Выдано клиенту");
	
	private String label;

	StatePlasticPacket(String label) {
		this.label = label;
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
	
	static public boolean isErrorState(StatePlasticPacket statePlasticPacket) {
		return(statePlasticPacket == SENT_ERROR ||
		   statePlasticPacket == RESPONSE_ERROR_ALL ||
		   statePlasticPacket == RESPONSE_ERROR_PART ||
		   statePlasticPacket == SMS_RESPONSE_ERROR_ALL_OIA ||
		   statePlasticPacket == SMS_RESPONSE_ERROR_PART_OIA);
	}
}
