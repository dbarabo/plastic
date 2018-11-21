package ru.barabo.plastic.main.resources.owner;

import org.aeonbits.owner.ConfigFactory;

public class Cfg {

	static {
		ConfigFactory.setProperty("cfgpath", "classpath:main/resources/properties");
	}

	private static class SingletonPath {
		static final Path path = ConfigFactory.create(Path.class);
	}

	private static class SingletonQuery {
		static final Query query = ConfigFactory.create(Query.class);
	}

	private static class SingletonMsg {
		static final Msg msg = ConfigFactory
				.create(Msg.class);
	}

	public static Path path() {
		return SingletonPath.path;
	}

	public static Query query() {
		return SingletonQuery.query;
	}

	public static Msg msg() {
		return SingletonMsg.msg;
	}

}
