package ru.barabo.plastic.release.packet.data;

import ru.barabo.db.SessionException;
import ru.barabo.plastic.afina.AfinaQuery;
import ru.barabo.total.db.FieldItem;
import ru.barabo.total.db.impl.AbstractFilterStore;

import java.util.ArrayList;
import java.util.List;

public class DBStorePacketAllContent extends AbstractFilterStore<PacketContentRowField> {

	final static private String SEL_ALL_CONTENT = "{ ? = call od.PTKB_PLASTIC_AUTO.getPlasticAllContent }";

	@Override
	public List<FieldItem> getFields() {
		PacketContentRowField cursor = getRow();

		if (cursor == null) {
			cursor = new PacketContentRowField();
		}

		return cursor.fieldItems();
	}

	@Override
	protected List<PacketContentRowField> initData() {

		List<PacketContentRowField> data = new ArrayList<>();

		try {
			List<Object[]> datas = AfinaQuery.INSTANCE.selectCursor(SEL_ALL_CONTENT, null);

			for (Object[] row : datas) {
				data.add(PacketContentRowField.create(row));
			}

		} catch (SessionException ignored) {
		}
		return data;
	}

	@Override
	public void setViewType(int type) {
		// TODO Auto-generated method stub
	}

	@Override
	public int getTypeSelect() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void moveRow(int rowFrom, int rowTo) {
		// TODO Auto-generated method stub

	}

	@Override
	protected PacketContentRowField createEmptyRow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PacketContentRowField cloneRow(PacketContentRowField row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void insertRow(PacketContentRowField row) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateRow(PacketContentRowField oldData, PacketContentRowField newData) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void remove(PacketContentRowField row) {
		// TODO Auto-generated method stub

	}

}
