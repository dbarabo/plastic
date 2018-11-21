package ru.barabo.plastic.release.resources;

import org.aeonbits.owner.Config;

public interface BbrReportLoanPactCredit extends Config {

	@DefaultValue("BBR_PTKB_LOANPACT_CARD_FL")
	String procedureName();

	@DefaultValue("{ call BBR.BBR_PTKB_LOANPACT_CARD_FL(?) }")
	String procedureCallSql();

	@DefaultValue("1169446447")
	Long bbrId();
}
