package ru.barabo.plastic.main.resources.owner;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources({ "${cfgpath}/query.properties" })
public interface Query extends Config {

	@DefaultValue("1169446447L")
	Long bbrReportLoanCardFlId();

	@DefaultValue("{ call BBR.BBR_PTKB_LOANPACT_CARD_FL(?) }")
	String executeBbrReportLoanCardFl();

	@DefaultValue("BBR_PTKB_LOANPACT_CARD_FL")
	String procedureBbrReportLoanCardFl();

	/**
	 * @param = newPhone, newCardId
	 * @return
	 */
	@DefaultValue("{ call od.PTKB_PLASTIC_AUTO.updatePhoneCard(?, ?) }")
	String updatePhoneCard();

	@DefaultValue("{ ? = call od.PTKB_PLASTIC_AUTO.getIvrCardInfo(?) }")
	String selectIvrInfo();

	@DefaultValue("select state from od.PTKB_IVR_REGISTER where id = ?")
	String selectIvrStateById();

	@DefaultValue("select state, SERVICE_PHONE, WAIT_MINUTE from od.PTKB_IVR_REGISTER where id = ?")
	String selectIvrById();

	@DefaultValue("select p.PROBLEM from od.users u, od.PTKB_JAVA_PROBLEM p "
			+ "where u.USERID = ? and u.department = p.department")
	String selectUserProblems();

	@DefaultValue("insert into PTKB_IVR_REGISTER "
			+ "(id, CARD_NUMBER, PHONE, CARD_VALID_TO, CLIENT_NAME, FILE_NAME) values "
			+ "(?, ?, ?, ?, ?, ?)")
	String insertIvrRequest();

	@DefaultValue("select classified.nextval from dual")
	String getClassifiedNextVal();

	@DefaultValue("{ ? = call od.PTKB_PLASTIC_AUTO.selectEmptyDesign( ? ) }")
	String selectEmptyDesign();

	@DefaultValue("select PartData from od.BlankFilePart where BlankCmd = ? order by OrderNum")
	String selectReadBlobFile();

	@DefaultValue("out.rtf")
	String outRtf();

	@DefaultValue("out.txt")
	String outTxt();
}
