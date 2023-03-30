package com.sas.sso.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class DateTimeUtil {

    public static final String DD_MM_YYYY = "dd/MM/yyyy";
    public static final String HH_MM = "HH:mm";
    public static final String DD_MM_YYYY_MINUS = "dd-MM-yyyy";
    public static final String YYYY_MM_DD_MINUS = "yyyy-MM-dd";
    public static final String DD_MM_YYYY_HH_MM_SS = "dd-MM-yyyy HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String DDMMYYYY = "dd-MM-yyyy";
    public static final String DD_MM_YYYYDbFormat = "dd-MM-yyyy";
    public static final String D_MMM_YYYY = "d MMM yyyy";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private static final DateTimeFormatter dateTimeFormatter_DDMMYYYYHHMM = DateTimeFormatter.ofPattern("ddMMyyyyHHmm");

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final DateTimeFormatter dateFormatterReseller = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter dateFormatter_YYYYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter dateFormatter_MMDDYYYY = DateTimeFormatter.ofPattern("MM_dd_yyyy");

    private static final DateTimeFormatter dateFormatter_ddMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter dateFormatter_ddMMMYYYY = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public static String dateTimeToString(LocalDateTime localDateTime) {
        return null != localDateTime ? dateTimeFormatter.format(localDateTime) : null;
    }

    public static String dateTimeFormatter_DDMMYYYYHHMM(LocalDateTime localDateTime) {
        return null != localDateTime ? dateTimeFormatter_DDMMYYYYHHMM.format(localDateTime) : null;
    }

    public static LocalDateTime stringToDateTime(String localDateTimeString) {
        return StringUtils.hasText(localDateTimeString) ? LocalDateTime.parse(localDateTimeString, dateTimeFormatter) : null;
    }

    public static LocalDateTime stringToDateTime(String localDateTimeString, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return StringUtils.hasText(localDateTimeString) ? LocalDateTime.parse(localDateTimeString, dateTimeFormatter) : null;
    }

    public static String dateToString(LocalDate localDate) {
        return null != localDate ? dateFormatter.format(localDate) : null;
    }

    public static String dateToStringReseller(LocalDate localDate) {
        return null != localDate ? dateFormatterReseller.format(localDate) : null;
    }

    public static String localDateToString(LocalDate localDate, String dateFormat) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
        return localDate != null ? dateFormatter.format(localDate) : null;
    }

    public static LocalDate stringToDate(String localDateString) {
        return StringUtils.hasText(localDateString) ? LocalDate.parse(localDateString, dateFormatter) : null;
    }

    public static LocalDate stringToDateYYYYMMDD(String localDateString) {
        return StringUtils.hasText(localDateString) ? LocalDate.parse(localDateString, dateFormatter_YYYYMMDD) : null;
    }

    public static LocalDate stringToDateMMDDYYYY(String localDateString) {
        return StringUtils.hasText(localDateString) ? LocalDate.parse(localDateString, dateFormatter_MMDDYYYY) : null;
    }

    public static LocalDate stringToLocalDate(String localDateString, String dateFormat) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
        return StringUtils.hasText(localDateString) ? LocalDate.parse(localDateString, dateFormatter) : null;
    }

    public static String dateToStringddMMYYYY(LocalDate localDate) {
        return null != localDate ? dateFormatter_ddMMYYYY.format(localDate) : null;
    }

    public static String dateToStringddMMYYYYHHSS(LocalDateTime localDateTime) {
        return null != localDateTime ? dateFormatter_ddMMYYYY.format(localDateTime) : null;
    }

    public static String dateTimeToString(LocalDateTime localDateTime, String format) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(format);
        return localDateTime != null ? dateFormat.format(localDateTime) : null;
    }

    public static Date parseDate(String date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            return sdf.parse(date);
        } catch (Exception e) {
            log.error("Exception occured while parsing date {} with format {}", date, format);
        }
        return null;
    }

    public static int calculateAge(LocalDate birthDate) {
        if ((birthDate != null)) {
            return Period.between(birthDate, LocalDate.now()).getYears();
        } else {
            return 0;
        }
    }

    public static LocalDate parseLocalDate(String date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(date, formatter);
    }

    public static String format(Date date, String format) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        }
        return null;
    }

    public static LocalDate getLocalDateFromDate(Date date) {
        if (date != null) {
            return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }

    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static List<Integer> findRemainingYear(Integer month) {
        LocalDate pastYear = LocalDate.now().minusMonths(month);
        Integer cuInteger = LocalDate.now().getYear();
        List<Integer> yearList = new ArrayList<>();
        yearList.add(pastYear.getYear());
        for (int i = pastYear.getYear(); i < cuInteger; i++) {
            yearList.add(pastYear.plusYears(1).getYear());
            pastYear = pastYear.plusYears(1);
        }
        return yearList;
    }

    public static LocalDateTime stringTimeToDateTime(String localDate, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return StringUtils.hasText(localDate) ? LocalDate.parse(localDate, formatter).atStartOfDay() : null;
    }

    public static String karzaToFinncubFormat(String karzaDate) throws ParseException {
        DateFormat originalFormat = new SimpleDateFormat(YYYY_MM_DD_MINUS, Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat(DD_MM_YYYY_MINUS);
        Date date = originalFormat.parse(karzaDate);
        return targetFormat.format(date);
    }

    public static Date getDateBeforeXDays(Date date, int getDateBeforeXDays) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -getDateBeforeXDays);
        return c.getTime();
    }

    public static Date getDateAfterXDays(Date date, int getDateBeforeXDays) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, getDateBeforeXDays);
        return c.getTime();
    }
}