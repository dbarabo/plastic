package ru.barabo.total.db.impl.formatter;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

public class CardFormat extends Format {

	private DecimalFormat delegate;

	public CardFormat() {
		delegate = new DecimalFormat("0000,0000,0000,0000");
	}

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {

		if (obj == null) {
			return null;
		}

		BigInteger val = new BigInteger((String) obj, 10);

		return delegate.format(val, toAppendTo, pos);
	}

	@Override
	public Object parseObject(String source, ParsePosition pos) {

		Object value = delegate.parseObject(source, pos);

		return (value == null) ? null : value.toString();
	}

}
