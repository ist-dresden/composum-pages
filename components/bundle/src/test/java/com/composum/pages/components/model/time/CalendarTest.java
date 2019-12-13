package com.composum.pages.components.model.time;

import org.apache.commons.lang3.LocaleUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarTest {

    public static final Locale LOCALE_US = Locale.US;
    public static final Locale LOCALE_DE = Locale.GERMANY;

    public Calendar getTime(Locale locale) {
        Calendar time = Calendar.getInstance(locale);
        time.clear();
        time.set(2019, Calendar.OCTOBER, 1);
        return time;
    }

    protected class TestCalendar extends CalendarModel {

        @Override
        public int getColumns() {
            return 1;
        }

        @Override
        public int getRows() {
            return 3;
        }
    }

    @Test
    public void testLocaleDe() {
        Calendar time = getTime(LOCALE_DE);
        CalendarModel calendar = new TestCalendar();
        calendar.setRange(new DateRange(null, LOCALE_DE,
                Calendar.MONTH, time.get(Calendar.YEAR), time.get(Calendar.MONTH) + 1, 3));
        System.out.println(LOCALE_DE.toString());
        String result = printCalendar(calendar);
        Assert.assertTrue(result.contains("2019/09: 40  _30_  01   02   03   04   05   06"));
        Assert.assertTrue(result.contains("2019/11: 47   18   19   20   21   22   23   24"));
        Assert.assertTrue(result.contains("2019/11: 48  _25_ _26_ _27_ _28_ _29_ _30_  01"));
    }

    @Test
    public void testLocaleUs() {
        Calendar time = getTime(LOCALE_US);
        CalendarModel calendar = new TestCalendar();
        calendar.setRange(new DateRange(null, LOCALE_US,
                Calendar.MONTH, time.get(Calendar.YEAR), time.get(Calendar.MONTH) + 1, 3));
        System.out.println(LOCALE_US.toString());
        String result = printCalendar(calendar);
        Assert.assertTrue(result.contains("2019/09: 39  _29_ _30_  01   02   03   04   05"));
        Assert.assertTrue(result.contains("2019/10: 43  _27_ _28_ _29_ _30_ _31_  01   02"));
        Assert.assertTrue(result.contains("2019/12: 48   01   02   03   04   05   06   07"));
    }

    @Test
    public void testLocaleAe() {
        Locale locale = LocaleUtils.toLocale("ar_AE");
        Calendar time = getTime(locale);
        CalendarModel calendar = new TestCalendar();
        calendar.setRange(new DateRange(null, locale,
                Calendar.MONTH, time.get(Calendar.YEAR), time.get(Calendar.MONTH) + 1, 3));
        System.out.println(locale.toString());
        String result = printCalendar(calendar);
        Assert.assertTrue(result.contains("2019/09: 39  _28_ _29_ _30_  01   02   03   04"));
        Assert.assertTrue(result.contains("2019/10: 43  _26_ _27_ _28_ _29_ _30_ _31_  01"));
        Assert.assertTrue(result.contains("2019/12: 52   28   29   30   31  _01_ _02_ _03_"));
    }

    public <Type extends TimeRelated> String printCalendar(CalendarModel calendar) {
        StringBuilder builder = new StringBuilder();
        List<CalendarModel.Row> months = calendar.getMonthRows();
        for (CalendarModel.Row row : months) {
            for (CalendarModel.Month month : row.getMonths()) {
                builder.append("----");
                int index = 0;
                for (CalendarModel.Month.Day day : month) {
                    Calendar date = day.getDate();
                    if (day.isFirstDayOfWeek()) {
                        builder.append(new SimpleDateFormat("\nyyyy/MM: ww ").format(date.getTime()));
                    }
                    builder.append(" ")
                            .append(day.isDayOfMonth() ? " " : "_")
                            .append(new SimpleDateFormat("dd").format(date.getTime()))
                            .append(day.isDayOfMonth() ? " " : "_");
                    index++;
                }
                builder.append("\n");
            }
        }
        builder.append("----\n");
        String result = builder.toString();
        System.out.print(result);
        return result;
    }
}
