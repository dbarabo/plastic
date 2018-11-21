package ru.barabo.plastic.release.reissue.data;

public enum TypeSelect {
	Time(DBStoreReIssueCard.SEL_TIME),
	Lost(DBStoreReIssueCard.SEL_LOST),
	Application(DBStoreReIssueCard.SEL_APPLICATION);

	private String select;

	TypeSelect(String sel) {
		select = sel;
	}

	public String getSelect() {
		return select;
	}
}
