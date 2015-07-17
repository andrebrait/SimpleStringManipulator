package com.brait;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

public class NewDateTime implements Serializable {

	private static final long serialVersionUID = 3211067975258754805L;

	/**
	 * Formato padrão que será utilizado na conversão para string caso não seja
	 * passado algum
	 */
	private static final String DEFAULT_PATTERN = "dd/MM/yyyy HH:mm:ss";

	private Integer precision;

	private Timestamp dataUTC;

	private Timestamp dataLocal;

	private Integer offSetLocal;

	public NewDateTime() {
		this.precision = Calendar.SECOND;
		this.offSetLocal = getTimeZoneOffset(TimeZone.getDefault());
		this.setDataLocal(new Date());
	}

	public void setDataLocal(Date dataLocal) {
		if (dataLocal != null) {
			this.dataLocal = new Timestamp(DateUtils.truncate(dataLocal,
					precision).getTime());
			this.dataUTC = new Timestamp(DateUtils.addHours(this.dataLocal,
					-offSetLocal).getTime());
		}
	}

	public void setDataUTC(Date dataUTC) {
		if (dataUTC != null) {
			this.dataUTC = new Timestamp(DateUtils.truncate(dataUTC, precision)
					.getTime());
			this.dataLocal = new Timestamp(DateUtils.addHours(this.dataUTC,
					offSetLocal).getTime());
		}
	}

	public void setTimeZone(TimeZone tz) {
		if (tz != null) {
			setOffSetLocal(getTimeZoneOffset(tz));
		}
	}

	public void setPrecision(Integer precision) {
		if (precision != null) {
			if (precision < this.precision) {
				this.dataLocal = new Timestamp(DateUtils.truncate(
						this.dataLocal, precision).getTime());
				this.dataUTC = new Timestamp(DateUtils.truncate(this.dataUTC,
						precision).getTime());
			}
			this.precision = precision;
		}
	}

	/**
	 * Retorna o valor da data como string
	 * 
	 * @param pattern
	 * @param useOffSet
	 * @return String
	 */
	public String getDateAsString(String pattern, boolean useOffSet) {
		SimpleDateFormat df = new SimpleDateFormat(StringUtils.defaultIfBlank(
				pattern, DEFAULT_PATTERN));

		if (useOffSet) {
			String dateFormatted = df.format(dataLocal);
			return dateFormatted.concat(" ")
					.concat(getOffSetLocalAsGMTString());
		} else {
			return df.format(dataUTC.getTime());
		}
	}

	/**
	 * Retorna o valor da data como string
	 * 
	 * @param pattern
	 * @return String
	 */
	public String getDateAsString(String pattern) {
		return getDateAsString(pattern, false);
	}

	/**
	 * Retorna o OffSet do TimeZone da data como uma string GMT. Ex: GMT+3,
	 * GMT-1 etc.
	 * 
	 * @return o OffSet do TimeZone da data como uma string GMT.
	 */
	public String getOffSetLocalAsGMTString() {
		String gmt = offSetLocal < 0 ? "GMT" : "GMT+";
		return gmt.concat(String.valueOf(offSetLocal));
	}

	/**
	 * Gets the date as string output gmt.
	 *
	 * @param pattern
	 *            the pattern
	 * @param outputGMT
	 *            the output gmt
	 * @return the date as string output gmt
	 */
	public String getDateAsStringOutputGMT(String pattern, boolean outputGMT) {
		SimpleDateFormat df = new SimpleDateFormat(StringUtils.defaultIfBlank(
				pattern, DEFAULT_PATTERN));

		if (outputGMT) {
			String dateFormatted = df.format(dataLocal);
			return dateFormatted.concat(" ")
					.concat(getOffSetLocalAsGMTString());
		} else {
			return df.format(dataLocal.getTime());
		}
	}

	@Override
	public String toString() {
		return getDateAsString("", true);
	}

	private static Integer getTimeZoneOffset(TimeZone tz) {
		return (int) (tz.getRawOffset() / DateUtils.MILLIS_PER_HOUR);
	}

	/*
	 * 
	 * Início da camada de compatibilidade com DateTimeZone e
	 * DateTimeZoneWithUTC
	 */

	/**
	 * Cria uma instância de DateTimeZone.
	 *
	 * @param timeZone
	 *            the time zone
	 */
	public NewDateTime(TimeZone timeZone) {
		this.precision = Calendar.SECOND;
		this.offSetLocal = getTimeZoneOffset(timeZone);
		this.setDataLocal(Calendar.getInstance(timeZone).getTime());
	}

	/**
	 * Cria uma instância de DateTimeZone
	 * 
	 * @param data
	 * @param pattern
	 * @param timeZone
	 */
	public DateTimeZoneWithUTC(String data, String pattern, TimeZone timeZone) {
		SimpleDateFormat df = new SimpleDateFormat(pattern);

		try {
			this.dataLocal = DateUtils.toTimestamp(df.parse(data), timeZone);
			this.dataUTC = DateUtils.toGmtTimestamp(df.parse(data));
			this.offSetLocal = timeZone.getRawOffset()
					/ DateUtils.MILLIS_PER_HOUR;
		} catch (ParseException e) {

		}
	}

	/**
	 * Cria uma instância de DateTimeZone
	 * 
	 * @param data
	 * @param pattern
	 * @param timeZone
	 */
	public DateTimeZoneWithUTC(Date data, TimeZone timeZone) {

		try {
			this.dataLocal = DateUtils.toTimestamp(data, timeZone);
			this.dataUTC = DateUtils.toGmtTimestamp(data);
			this.offSetLocal = timeZone.getRawOffset()
					/ DateUtils.MILLIS_PER_HOUR;
		} catch (Exception e) {

		}
	}

	public Timestamp getDataUTC() {
		return (Timestamp) dataUTC.clone();
	}

	public Timestamp getDataLocal() {
		return (Timestamp) dataLocal.clone();
	}

	public int getOffSetLocal() {
		return offSetLocal;
	}

	public void setOffSetLocal(int newOffSetLocal) {
		Integer diff = newOffSetLocal - offSetLocal;
		dataLocal = new Timestamp(DateUtils.addHours(dataLocal, diff).getTime());
		offSetLocal = newOffSetLocal;
	}

	/**
	 * Setter dataLocal
	 * 
	 * @param dataLocal
	 *            Date
	 */
	public void setDate(Date dataLocal) {
		setDataLocal(dataLocal);
	}

	/**
	 * Setter dataLocal
	 * 
	 * @param dataLocal
	 *            Date
	 */
	public Date getDate() {
		return getDataLocal();
	}

}
