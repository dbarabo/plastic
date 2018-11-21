package ru.barabo.plastic.release.packet.data;


public enum SelectTypes {

	ALL,
	ALL_MY,
	NEW,
	NEW_MY,
	WORK,
	WORK_MY,
	END,
	END_MY,

	ERROR,
	ERROR_MY;
	
	public String getSelUser() {
		return (this.ordinal() % 2 == 0) ? "CREATOR" :
			"USER";
	}
	
	public StatePlasticPacket getSelMinState() {
		switch(this) {
		case ALL:
		case ALL_MY:
			return StatePlasticPacket.getMinValue();
			
		case NEW:
		case NEW_MY:
			return StatePlasticPacket.NEW;
			
		case END:
		case END_MY:
			return StatePlasticPacket.getMaxValue();
			
		case ERROR:
		case ERROR_MY:
			return StatePlasticPacket.RESPONSE_ERROR_ALL;

		default:
			return StatePlasticPacket.values()[StatePlasticPacket.getMinValue().ordinal() + 1];
			
		}
	}
	
	public StatePlasticPacket getSelMaxState() {
		
		switch(this) {
		case ALL:
		case ALL_MY:
			return StatePlasticPacket.getMaxValue();
			
		case NEW:
		case NEW_MY:
			return StatePlasticPacket.NEW;
			
		case END:
		case END_MY:
			return StatePlasticPacket.getMaxValue();

		case ERROR:
		case ERROR_MY:
			return StatePlasticPacket.RESPONSE_ERROR_PART;
			
		default:
			return StatePlasticPacket.values()[StatePlasticPacket.getMaxValue().ordinal() - 1];
			
		}
	}
}
