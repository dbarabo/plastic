package ru.barabo.plastic.main.resources.owner;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources({ "${cfgpath}/msg.properties" })
public interface Msg extends Config {

	@DefaultValue("В картах не указан дизайн")
	String subjDesignCardIsEmpty();

	@DefaultValue("В картах не указан дизайн, т.к. в старых заявлениях его не было\n "
			+ "Для его явного указания нужно нажить <Заявление>, выбрать дизайн и сохранить\n"
			+ " Список карт без дизайна:\n%s")
	String msgDesignCardIsEmpty(String cards);
}
