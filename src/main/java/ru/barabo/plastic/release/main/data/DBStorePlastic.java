package ru.barabo.plastic.release.main.data;

import ru.barabo.plastic.release.application.data.DBStoreApplicationCard;
import ru.barabo.plastic.release.application.data.DBStoreClientFind;
import ru.barabo.plastic.release.packet.data.*;
import ru.barabo.plastic.release.reissue.data.DBStoreReIssueCard;
import ru.barabo.plastic.release.reissue.data.ReIssueCardRowField;
import ru.barabo.plastic.release.sms.packet.data.DBStoreSmsPacket;
import ru.barabo.plastic.release.sms.packet.data.DBStoreSmsPacketContent;
import ru.barabo.plastic.release.sms.select.data.DBStoreSmsSelect;
import ru.barabo.total.db.DBStore;

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

	public DBStorePlastic() {
		reIssueCard = new DBStoreReIssueCard(this);
		
		packet = new DBStorePacket(this);
		
		content = new DBStorePacketContent(this, packet);
		
		smsSelect = new DBStoreSmsSelect(this);
		
		smsPacket = new DBStoreSmsPacket(this);
		
		smsContent = new DBStoreSmsPacketContent(this, smsPacket);
		
		applicationCard = new DBStoreApplicationCard(this);

		clientFind = new DBStoreClientFind(this, applicationCard);

		allContent = new DBStorePacketAllContent();
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

	public DBStore<ReIssueCardRowField> getReIssueCard() {
		return reIssueCard;
	}

	public DBStore<PacketRowField> getPacket() {
		return packet;
	}

	public DBStoreApplicationCard getApplicationCard() {
		return applicationCard;
	}

}
