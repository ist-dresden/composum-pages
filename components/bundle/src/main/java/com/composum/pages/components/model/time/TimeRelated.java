package com.composum.pages.components.model.time;

import com.composum.pages.commons.model.Page;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import static com.composum.pages.commons.PagesConstants.DEF_DATETIME_FMT;
import static com.composum.pages.commons.PagesConstants.DEF_DATE_FMT;
import static com.composum.pages.commons.PagesConstants.DEF_DAY_FMT;
import static com.composum.pages.commons.PagesConstants.DEF_TIME_FMT;
import static com.composum.pages.commons.PagesConstants.SP_DATETIME_FMT;
import static com.composum.pages.commons.PagesConstants.SP_DATE_FMT;
import static com.composum.pages.commons.PagesConstants.SP_DAY_FMT;
import static com.composum.pages.commons.PagesConstants.SP_TIME_FMT;

public class TimeRelated extends Page {

    public static final String PN_DATE = "date";
    public static final String PN_START_DATE = PN_DATE;
    public static final String PN_END_DATE = "dateEnd";

    public static final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000;

    public static final Calendar EMPTY = new GregorianCalendar();

    public class Moment {

        public final String property;

        private transient Calendar value;
        private transient String day;
        private transient String date;
        private transient String time;
        private transient String datetime;

        protected Moment(String property) {
            this.property = property;
        }

        public boolean isPresent() {
            return getValue() != null;
        }

        public Calendar getValue() {
            if (value == null) {
                value = getProperty(property, null, EMPTY);
            }
            return value == EMPTY ? null : value;
        }

        @Nonnull
        public String getDay() {
            if (day == null) {
                day = format(SP_DAY_FMT, DEF_DAY_FMT);
            }
            return day;
        }

        @Nonnull
        public String getDate() {
            if (date == null) {
                date = format(SP_DATE_FMT, DEF_DATE_FMT);
            }
            return date;
        }

        @Nonnull
        public String getTime() {
            if (time == null) {
                time = format(SP_TIME_FMT, DEF_TIME_FMT);
            }
            return time;
        }

        @Nonnull
        public String getDateTime() {
            if (datetime == null) {
                datetime = format(SP_DATETIME_FMT, DEF_DATETIME_FMT);
            }
            return datetime;
        }

        protected boolean isTheSameDay(Moment other) {
            Calendar cal = getValue();
            Calendar oCal = other.getValue();
            return cal != null && oCal != null &&
                    cal.getTimeInMillis() / DAY_IN_MILLIS == oCal.getTimeInMillis() / DAY_IN_MILLIS;
        }

        protected boolean seemsToBeEqual(Moment other) {
            Calendar cal = getValue();
            Calendar oCal = other.getValue();
            return cal != null && oCal != null && Math.abs(cal.getTimeInMillis() - oCal.getTimeInMillis()) < 120000;
        }

        protected String format(String fmtKey, String defFmt) {
            Locale locale = getLocale();
            Calendar value = getValue();
            return value != null
                    ? new SimpleDateFormat(getSettingsProperty(fmtKey, locale, defFmt), locale).format(value.getTime())
                    : "";
        }
    }

    private transient Moment date;
    private transient Moment dateEnd;

    private transient String tileStyle;

    @Nonnull
    public Moment getDate() {
        if (date == null) {
            date = new Moment(PN_DATE);
        }
        return date;
    }

    @Nonnull
    public Moment getStartDate() {
        return getDate();
    }


    @Nonnull
    public Moment getEndDate() {
        if (dateEnd == null) {
            dateEnd = new Moment(PN_END_DATE);
        }
        return dateEnd;
    }

    public boolean isOneDayOnly() {
        return getEndDate().isTheSameDay(getDate());
    }

    public String getTileStyle() {
        if (tileStyle == null) {
            StringBuilder style = new StringBuilder("d");
            StringBuilder endStyle = new StringBuilder();
            Moment date = getDate();
            Moment endDate = getEndDate();
            if (endDate.isPresent()) {
                Calendar cal = date.getValue();
                Calendar endCal = endDate.getValue();
                if (endCal.get(Calendar.MONTH) != cal.get(Calendar.MONTH) ||
                        endCal.get(Calendar.YEAR) != cal.get(Calendar.YEAR)) {
                    style.append("m");
                    endStyle.append("dm");
                } else {
                    if (endCal.get(Calendar.DAY_OF_MONTH) != cal.get(Calendar.DAY_OF_MONTH)) {
                        endStyle.append("dm");
                    } else {
                        style.append("mt");
                        if (!endDate.seemsToBeEqual(date)) {
                            endStyle.append("t");
                        }
                    }
                }
            } else {
                style.append("mt");
            }
            if (endStyle.length() > 0) {
                style.append('-').append(endStyle.toString());
            }
            tileStyle = style.toString();
        }
        return tileStyle;
    }
}