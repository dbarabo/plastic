package ru.barabo.plastic.release.main.data;

import ru.barabo.afina.AfinaQuery;
import ru.barabo.db.SessionException;
import ru.barabo.plastic.release.application.data.DBStoreApplicationCard;
import ru.barabo.plastic.release.application.data.DBStoreClientFind;
import ru.barabo.plastic.release.packet.data.*;
import ru.barabo.plastic.release.reissue.data.DBStoreReIssueCard;
import ru.barabo.plastic.release.reissue.data.ReIssueCardRowField;
import ru.barabo.plastic.release.sms.packet.data.DBStoreSmsPacket;
import ru.barabo.plastic.release.sms.packet.data.DBStoreSmsPacketContent;
import ru.barabo.plastic.release.sms.select.data.DBStoreSmsSelect;
import ru.barabo.plastic.unnamed.data.*;
import ru.barabo.plastic.unnamed.general.*;
import ru.barabo.total.db.DBStore;
import ru.barabo.total.db.FilteredStore;

public class DBStorePlastic {

	private DBStoreReIssueCard reIssueCard;
	
	private DBStorePacket packet;
	
	private DBStorePacketContent content;
	
    private DBStoreSmsSelect smsSelect;  
    
    private DBStoreApplicationCard applicationCard;
    
	private DBStoreSmsPacket smsPacket;
	
	private DBStoreSmsPacketContent smsContent;
	
	private DBStoreClientFind clientFind;
	
	private DBStorePacketAllContent allContent;

    private FilteredStoreInPath<RowFieldInPath> unnamedInPath;

    private FilteredStoreInHome<RowFieldInPath> unnamedInHome;

    private FilteredStore<RowFieldInPath> unnamedError;

    private FilteredStoreOutClient<RowFieldOutClient> unnamedOutClient;

    private TotalCardInfo totalCardInfo;

    private ClientCriteriaDBStore<RowFieldClient> сlientSelect;

    static private DBStorePlastic instance = null;

	public DBStorePlastic() throws SessionException {

        instance = this;

        checkWorkplace();

		/* applicationCard = new DBStoreApplicationCard(this);

		clientFind = new DBStoreClientFind(this, applicationCard);

        unnamedInPath = new DBStoreInPath(this);

        unnamedInHome = new DBStoreInHome(this);

        unnamedError = new DBStoreError(this);

        unnamedOutClient = new DBStoreOutClient(this);

        totalCardInfo = new TotalCardInfoImpl();

        сlientSelect = new DBStoreClientSelect(this);
		*/
    }

    public ClientCriteriaDBStore<RowFieldClient> getClientSelect() {
        return сlientSelect;
    }

    public TotalCardInfo getTotalCardInfo() {
	    return totalCardInfo;
    }

    static public DBStorePlastic getInstance() {
	    return instance;
    }

    static final private String CHECK_WORKSPACE = "{ call od.PTKB_PLASTIC_AUTO.checkWorkplace }";

	private void checkWorkplace()throws SessionException {
        AfinaQuery.INSTANCE.execute(CHECK_WORKSPACE, null);
    }

    public FilteredStoreOutClient<RowFieldOutClient> getUnnamedOutClient() {
        return unnamedOutClient;
    }

    public FilteredStoreInPath<RowFieldInPath> getUnnamedInPath() {
	    return unnamedInPath;
    }

    public FilteredStoreInHome<RowFieldInPath> getUnnamedInHome() {
        return unnamedInHome;
    }

    public FilteredStore<RowFieldInPath> getUnnamedError() {
        return unnamedError;
    }

	public DBStorePacketAllContent getAllContent() {
		return allContent;
	}

	public DBStoreClientFind getClientFind() {
		return clientFind;
	}

	public DBStoreSmsSelect getSmsSelect() {
		return smsSelect;
	}

	public DBStoreSmsPacket getSmsPacket() {
		return smsPacket;
	}

	public DBStoreSmsPacketContent getSmsContent() {
		return smsContent;
	}
		
	public DBStore<PacketContentRowField> getContent() {
		return content;
	}

	public FilteredStore<ReIssueCardRowField> getReIssueCard() {
		return reIssueCard;
	}

	public FilteredStore<PacketRowField> getPacket() {
		return packet;
	}

	public DBStoreApplicationCard getApplicationCard() {
		return applicationCard;
	}

}
