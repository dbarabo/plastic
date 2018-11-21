package ru.barabo.plastic.release.resources;

import org.aeonbits.owner.ConfigFactory;
import ru.barabo.total.report.rtf.RtfAfinaData;

public class RtfDataLoanPactCredit implements RtfAfinaData {

	private Object[] params;

	private static class SingletonReport {
		static final BbrReportLoanPactCredit report = ConfigFactory
				.create(BbrReportLoanPactCredit.class);
	}

	@Override
	public String procedureName() {
		return SingletonReport.report.procedureName();
	}

	@Override
	public String procedureCallSql() {
		return SingletonReport.report.procedureCallSql();
	}

	@Override
	public Long bbrId() {
		return SingletonReport.report.bbrId();
	}

	@Override
	public Object[] paramCall() {
		return params;
	}

	public RtfDataLoanPactCredit(Number applicationId) {
		params = new Object[] { applicationId };
	}
}

