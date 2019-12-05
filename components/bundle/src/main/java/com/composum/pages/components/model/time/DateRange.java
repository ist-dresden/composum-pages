package com.composum.pages.components.model.time;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.WEEK_OF_YEAR;
import static java.util.Calendar.YEAR;

/**
 * a range of dates (days) - 'from' day (included) to 'to' day (exculded; the next day)
 * with a range type - one of { 'year', 'month', 'week', 'day' } - to support spinning
 */
@SuppressWarnings("MagicConstant")
public class DateRange {

    private static final Logger LOG = LoggerFactory.getLogger(DateRange.class);

    public static final long ONE_DAY = 24L * 60L * 60L * 1000L;

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String[] TYPE_TO_KEY = new String[]{null, "year", "month", "week", null, "day"};

    public static final char RULE_SEP = '|';
    public static final char DATE_SEP = '_';

    @Nonnull
    private final Locale locale; // the locale to use for formatting and week calculation

    @Nullable
    private final Integer type; // one of { Calendar.YEAR, Calendar.MONTH, Calendar.WEEK_OF_YEAR, Calendar.DAY_OF_MONTH }

    @Nonnull
    private final Calendar from, to; // the range, excluding 'to' ('from' <= ... < 'to')

    /**
     * constructs a range of a given range type (year, month, week, single day of month)
     *
     * @param type  one of { Calendar.YEAR, Calendar.MONTH, Calendar.WEEK_OF_YEAR, Calendar.DAY_OF_MONTH }
     * @param year  the year must always be specified
     * @param value depends on th type; the month (1..!) / week number, the day of month or the amount of type
     */
    public DateRange(int type, int year, int... value) {
        this(null, null, type, year, value);
    }

    /**
     * constructs a range of a given range type with specified time zone and locale
     */
    public DateRange(@Nullable TimeZone timeZone, @Nullable Locale locale, int type, int year, int... value) {
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        Calendar t, f = Calendar.getInstance(timeZone, locale);
        f.clear();
        switch (type) {
            case YEAR:
                f.set(year, 0, 1);
                t = (Calendar) f.clone();
                t.add(YEAR, 1);
                break;
            case MONTH:
                int month = Math.max(0, Math.min(11, value != null && value.length > 0
                        ? value[0] - 1 : Calendar.getInstance(timeZone, locale).get(MONTH)));
                f.set(year, month, 1);
                t = (Calendar) f.clone();
                t.add(MONTH, value != null && value.length > 1 ? value[1] : 1);
                break;
            case WEEK_OF_YEAR:
                int week = Math.max(0, Math.min(53, value != null && value.length > 0
                        ? value[0] : Calendar.getInstance(timeZone, locale).get(WEEK_OF_YEAR)));
                f.set(YEAR, year);
                f.set(WEEK_OF_YEAR, week);
                t = (Calendar) f.clone();
                t.add(WEEK_OF_YEAR, value != null && value.length > 1 ? value[1] : 1);
                break;
            case DAY_OF_MONTH:
                month = Math.max(0, Math.min(11, value != null && value.length > 0
                        ? value[0] - 1 : Calendar.getInstance(timeZone, locale).get(MONTH)));
                int day = Math.max(1, Math.min(31, value != null && value.length > 1
                        ? value[1] : Calendar.getInstance(timeZone, locale).get(DAY_OF_MONTH)));
                f.set(year, month, day);
                t = (Calendar) f.clone();
                t.add(DAY_OF_MONTH, 1);
                break;
            default:
                throw new IllegalArgumentException("unxecpected range type");
        }
        this.locale = locale;
        this.type = type;
        this.from = f;
        this.to = t;
    }

    /**
     * constructs a range without type
     *
     * @param from the start date of the date range
     * @param to   the first day after the date range
     */
    public DateRange(@Nullable final Date from, @Nullable final Date to) {
        this(null, null, from, to);
    }

    /**
     * constructs a range without type with specified time zone and locale
     */
    public DateRange(@Nullable final TimeZone timeZone, @Nullable final Locale locale,
                     @Nullable final Date from, @Nullable final Date to) {
        this(timeZone, locale, calendar(timeZone, locale, from), calendar(timeZone, locale, to));
    }

    /**
     * constructs a range without type
     *
     * @param from the start date of the date range; if 'null' a current month range is built
     * @param to   the first day after the date range; if 'null' a single day range is built
     */
    public DateRange(@Nullable final Calendar from, @Nullable final Calendar to) {
        this(null, null, from, to);
    }

    /**
     * constructs a range without type with specified time zone and locale
     */
    public DateRange(@Nullable TimeZone timeZone, @Nullable Locale locale,
                     @Nullable Calendar from, @Nullable Calendar to) {
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if (from == null) {
            if (to == null) {
                from = Calendar.getInstance(timeZone, locale);
                from.clear(HOUR_OF_DAY);
                from.clear(MINUTE);
                from.clear(SECOND);
                from.clear(MILLISECOND);
                to = (Calendar) from.clone();
                to.add(DAY_OF_MONTH, 1);
            } else {
                from = (Calendar) to.clone();
                from.clear(HOUR_OF_DAY);
                from.clear(MINUTE);
                from.clear(SECOND);
                from.clear(MILLISECOND);
                from.set(DAY_OF_MONTH, 1);
            }
        } else if (to == null) {
            to = (Calendar) from.clone();
            to.add(DAY_OF_MONTH, 1);
        }
        if (!from.before(to)) {
            throw new IllegalArgumentException("'from' must be before 'to' to declare a date range");
        }
        this.locale = locale;
        this.type = null;
        this.from = from;
        this.to = to;
    }

    /**
     * constructs a range parsing a rule like a sting constructed by getRule()
     */
    public static DateRange valueOf(@Nonnull final String rule) {
        return valueOf(rule, TimeZone.getDefault(), Locale.getDefault());
    }

    /**
     * constructs a range parsing a rule with specified time zone and locale
     */
    public static DateRange valueOf(@Nonnull final String rule, TimeZone timeZone, Locale locale) {
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if (StringUtils.isNotBlank(rule) && rule.indexOf(RULE_SEP) < 0) {
            String[] items = StringUtils.split(rule, DATE_SEP);
            Calendar from = null;
            try {
                from = calendar(timeZone, locale, new SimpleDateFormat(DATE_FORMAT).parse(items[0]));
                if (from != null) {
                    timeZone = from.getTimeZone();
                }
            } catch (ParseException ex) {
                LOG.error(ex.toString());
            }
            Calendar to = null;
            if (items.length > 1) {
                try {
                    to = calendar(timeZone, locale, new SimpleDateFormat(DATE_FORMAT).parse(items[0]));
                } catch (ParseException ex) {
                    LOG.error(ex.toString());
                }
            }
            return new DateRange(timeZone, locale, from, to);
        } else {
            Calendar now = Calendar.getInstance(timeZone, locale);
            String[] items = StringUtils.split(rule, RULE_SEP);
            int type = MONTH;
            if (StringUtils.isNotBlank(rule)) {
                try {
                    type = Integer.parseInt(items[0]);
                } catch (NumberFormatException ex) {
                    switch (items[0].toUpperCase()) {
                        case "YEAR":
                            type = YEAR;
                            break;
                        case "MONTH":
                        default:
                            type = MONTH;
                            break;
                        case "WEEK":
                            type = WEEK_OF_YEAR;
                            break;
                        case "DAY":
                            type = DAY_OF_MONTH;
                            break;
                    }
                }
            }
            int[] values = new int[2];
            switch (type) {
                case MONTH:
                    values[0] = items.length > 2 && StringUtils.isNotBlank(items[2])
                            ? Integer.parseInt(items[2]) : now.get(MONTH) + 1;
                    values[1] = items.length > 3 && StringUtils.isNotBlank(items[3])
                            ? Integer.parseInt(items[3]) : 1;
                    break;
                case WEEK_OF_YEAR:
                    values[0] = items.length > 2 && StringUtils.isNotBlank(items[2])
                            ? Integer.parseInt(items[2]) : now.get(WEEK_OF_YEAR);
                    values[1] = items.length > 3 && StringUtils.isNotBlank(items[3])
                            ? Integer.parseInt(items[3]) : 1;
                    break;
                case DAY_OF_MONTH:
                    values[0] = items.length > 2 && StringUtils.isNotBlank(items[2])
                            ? Integer.parseInt(items[2]) : now.get(MONTH) + 1;
                    values[1] = items.length > 3 && StringUtils.isNotBlank(items[3])
                            ? Integer.parseInt(items[3]) : now.get(DAY_OF_MONTH);
                    break;
            }
            int year = items.length > 1 && StringUtils.isNotBlank(items[1])
                    ? Integer.parseInt(items[1]) : now.get(YEAR);
            return new DateRange(timeZone, locale, type, year, values);
        }
    }

    /**
     * builds am URL parameter friendly range definition rule string of the date range
     *
     * @return a typed rule ('type|year[|value[|...]]') or a day range ('yyyy-MM-dd[_yyyy-MM-dd]')
     */
    public String getRule() {
        StringBuilder builder = new StringBuilder();
        if (type != null) {
            int amount;
            Calendar from = getFrom();
            builder.append(TYPE_TO_KEY[type]).append(RULE_SEP).append(from.get(YEAR));
            switch (type) {
                case WEEK_OF_YEAR:
                    builder.append(RULE_SEP).append(from.get(WEEK_OF_YEAR));
                    amount = getWeekCount();
                    if (amount > 1) {
                        builder.append(RULE_SEP).append(amount);
                    }
                    break;
                case MONTH:
                    builder.append(RULE_SEP).append(from.get(MONTH) + 1);
                    amount = getMonthCount();
                    if (amount > 1) {
                        builder.append(RULE_SEP).append(amount);
                    }
                    break;
                case DAY_OF_MONTH:
                    builder.append(RULE_SEP).append(from.get(MONTH) + 1)
                            .append(RULE_SEP).append(from.get(DAY_OF_MONTH));
                    break;
            }
        } else {
            builder.append(new SimpleDateFormat(DATE_FORMAT).format(getFrom().getTime()));
            if (!isSingleDay()) {
                builder.append(DATE_SEP).append(new SimpleDateFormat(DATE_FORMAT).format(getTo().getTime()));
            }
        }
        return builder.toString();
    }

    @Override
    @Nonnull
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (isSingleMonth()) {
            builder.append(new SimpleDateFormat("MMMM yyyy", getLocale()).format(getFrom().getTime()));
        } else {
            builder.append(getFromString());
            if (!isSingleDay()) {
                builder.append(" - ").append(getToString());
            }
        }
        return builder.toString();
    }

    @Nonnull
    public Locale getLocale() {
        return locale;
    }

    @Nonnull
    public Calendar getFrom() {
        return from;
    }

    @Nonnull
    public String getFromString() {
        return DateFormat.getDateInstance(DateFormat.MEDIUM, getLocale()).format(getFrom().getTime());
    }

    @Nonnull
    public Calendar getTo() {
        return to;
    }

    @Nonnull
    public String getToString() {
        Calendar beforeTo = (Calendar) getTo().clone();
        beforeTo.add(SECOND, -1);
        return DateFormat.getDateInstance(DateFormat.MEDIUM, getLocale()).format(beforeTo.getTime());
    }

    public boolean contains(Calendar date) {
        return !date.before(getFrom()) && date.before(getTo());
    }

    public boolean contains(DateRange range) {
        return range.getTo().after(getFrom()) && range.getFrom().before(getTo());
    }

    public boolean isSingleDay() {
        Calendar f = (Calendar) getFrom().clone();
        f.add(DAY_OF_MONTH, 1);
        return (f.equals(getTo()));
    }

    public boolean isSingleMonth() {
        Calendar f = (Calendar) getFrom().clone();
        f.add(MONTH, 1);
        return (f.equals(getTo()));
    }

    public int getDayCount() {
        return (int) ((to.getTimeInMillis() - from.getTimeInMillis()) / ONE_DAY);
    }

    public int getWeekCount() {
        Calendar m = (Calendar) from.clone();
        int count = 0;
        do {
            count++;
            m.add(WEEK_OF_YEAR, 1);
        } while (m.before(to));
        return count;
    }

    public int getMonthCount() {
        Calendar m = (Calendar) from.clone();
        int count = 0;
        do {
            count++;
            m.add(MONTH, 1);
        } while (m.before(to));
        return count;
    }

    /**
     * @return a list of months of this range as date ranges
     */
    public List<DateRange> splitMonths() {
        List<DateRange> months = new ArrayList<>();
        Calendar date = (Calendar) getFrom().clone();
        do {
            date.set(DAY_OF_MONTH, 1);
            months.add(new DateRange(date.getTimeZone(), locale, MONTH, date.get(YEAR), date.get(MONTH) + 1));
            date = (Calendar) date.clone();
            date.add(MONTH, 1);
        } while (contains(date));
        return months;
    }

    /**
     * constructs a new range of the same type moved relative to this range
     *
     * @param stepsize the amount of the ranges type to move; the amount of days if the range has no type
     */
    public DateRange move(int stepsize) {
        Calendar f = (Calendar) getFrom().clone();
        if (type != null) {
            switch (type) {
                case YEAR:
                    f.add(YEAR, stepsize);
                    return new DateRange(f.getTimeZone(), getLocale(), type, f.get(YEAR));
                case MONTH:
                    f.add(MONTH, stepsize);
                    return new DateRange(f.getTimeZone(), getLocale(), type, f.get(YEAR), f.get(MONTH) + 1, getMonthCount());
                case WEEK_OF_YEAR:
                    f.add(WEEK_OF_YEAR, stepsize);
                    return new DateRange(f.getTimeZone(), getLocale(), type, f.get(YEAR), f.get(WEEK_OF_YEAR), getWeekCount());
                case DAY_OF_MONTH:
                    f.add(DAY_OF_YEAR, stepsize);
                    return new DateRange(f.getTimeZone(), getLocale(), type, f.get(YEAR), f.get(MONTH) + 1, f.get(DAY_OF_MONTH));
            }
        }
        Calendar t = (Calendar) getTo().clone();
        f.add(DAY_OF_YEAR, stepsize);
        t.add(DAY_OF_YEAR, stepsize);
        return new DateRange(f.getTimeZone(), getLocale(), f, t);
    }

    /**
     * transforms a Date in to a Calendar
     */
    @Nullable
    public static Calendar calendar(@Nullable TimeZone timeZone, @Nullable Locale locale, @Nullable final Date date) {
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        Calendar calendar = null;
        if (date != null) {
            calendar = Calendar.getInstance(timeZone, locale);
            calendar.setTime(date);
        }
        return calendar;
    }
}
