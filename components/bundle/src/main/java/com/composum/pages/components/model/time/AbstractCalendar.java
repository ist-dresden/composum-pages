package com.composum.pages.components.model.time;

import com.composum.pages.commons.model.Page;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.composum.pages.components.model.time.DateRange.ONE_DAY;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

/**
 * a calendar of time related content resources (renders a set of months)
 *
 * @param <Type> the model type of the calendar items
 */
public abstract class AbstractCalendar<Type extends TimeRelated> extends TimeNavigator<Type> {

    public static final String PN_COLUMNS = "columns";
    public static final String PN_ROWS = "rows";

    public static final String PN_DETAIL_PAGE = "detailPage";

    public static final String PN_SHOW_WEEKDAY_LABELS = "showWeekdayLabels";
    public static final String PN_SHOW_WEEK_NUMBERS = "showWeekNumbers";

    /**
     * the model to visualize a month as iterable of days
     */
    public class Month implements Iterator<Month.Day>, Iterable<Month.Day> {

        /**
         * a day model of one of the days of a calendars month
         */
        public class Day {

            protected final Calendar date;
            protected final DateRange dayRange;

            private transient List<Type> dayItems;

            public Day(Calendar date) {
                this.date = (Calendar) date.clone();
                dayRange = new DateRange(date.getTimeZone(), monthRange.getLocale(),
                        DAY_OF_MONTH, date.get(YEAR), date.get(MONTH) + 1, date.get(DAY_OF_MONTH));
            }

            /**
             * @return 'true' if there are time related resources for this day
             */
            public boolean isHasItems() {
                return getDayItems().size() > 0;
            }

            /**
             * @return the list of time related content resources of this day
             */
            public List<Type> getDayItems() {
                if (dayItems == null) {
                    dayItems = new ArrayList<>();
                    for (Type item : getItems()) {
                        if (dayRange.contains(item.getDateRange())) {
                            dayItems.add(item);
                        }
                    }
                }
                return dayItems;
            }

            public Calendar getDate() {
                return date;
            }

            public int getDay() {
                return date.get(Calendar.DAY_OF_MONTH);
            }

            public int getWeek() {
                return date.get(Calendar.WEEK_OF_YEAR);
            }

            public int getDayOfWeek() {
                return date.get(Calendar.DAY_OF_WEEK);
            }

            /**
             * @return 'true' if this day is the first of a week and a new row must be rendered
             */
            public boolean isFirstDayOfWeek() {
                return date.getFirstDayOfWeek() == date.get(Calendar.DAY_OF_WEEK);
            }

            /**
             * @return 'true' if the month contains the day;
             * 'false' if days is 'outside' but element of the first or last week of the month
             */
            public boolean isDayOfMonth() {
                return monthRange.contains(date);
            }

            public String getDayRange() {
                return dayRange.getRule();
            }
        }

        protected final DateRange monthRange;   // the months date range
        protected final int row, column;

        private transient String label;         // the localized label of the month itself

        protected int weekdayOffset;            // the weekday offset of the first day of the month

        protected final List<Day> days = new ArrayList<>();

        protected Calendar current;             // the current iterator state

        public Month(DateRange range, int row, int column) {
            this.monthRange = range;
            this.row = row;
            this.column = column;
            Calendar fdofm = (Calendar) range.getFrom().clone();
            fdofm.set(Calendar.DAY_OF_MONTH, 1);
            int fwday = fdofm.get(Calendar.DAY_OF_WEEK);
            int fdofw = fdofm.getFirstDayOfWeek();
            weekdayOffset = fwday < fdofw ? fwday + 7 - fdofw : fwday - fdofw;
            reset();
        }

        /**
         * resets the iterator state to the first day to render (the first day of the first week of the month)
         */
        public void reset() {
            current = (Calendar) monthRange.getFrom().clone();
            current.add(Calendar.DAY_OF_MONTH, -weekdayOffset);
            current.add(Calendar.MINUTE, 30);
        }

        public int getYear() {
            return monthRange.getFrom().get(YEAR);
        }

        public String getMonthRange() {
            return monthRange.getRule();
        }

        /**
         * @return the localized label ot the month
         */
        public String getLabel() {
            if (label == null) {
                SimpleDateFormat format = new SimpleDateFormat("MMMM", getLocale());
                label = format.format(monthRange.getFrom().getTime());
            }
            return label;
        }

        protected Day getDay(Calendar date) {
            Day day = null;
            int index = indexOf(date);
            if (index < days.size()) {
                day = days.get(index);
            } else {
                for (int i = days.size(); i <= index; i++) {
                    days.add(null);
                }
            }
            if (day == null) {
                days.set(index, day = new Day(date));
            }
            return day;
        }

        protected int indexOf(Calendar date) {
            int dayIndex = (int) (date.getTimeInMillis() / ONE_DAY - monthRange.getFrom().getTimeInMillis() / ONE_DAY);
            return weekdayOffset + dayIndex;
        }

        // Iterable<Day>

        @NotNull
        @Override
        public Iterator<Day> iterator() {
            return this;
        }

        // Iterator<Day>

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Day next() {
            Day day = null;
            if (current != null) {
                day = getDay(current);
                current.add(Calendar.DAY_OF_MONTH, 1);
                if (!monthRange.contains(current) && current.get(Calendar.DAY_OF_WEEK) == current.getFirstDayOfWeek()) {
                    current = null; // stop on first day of week after last day of month
                }
            }
            return day;
        }

        @Override
        public void remove() {
        }
    }

    public class Row {

        private transient String label;

        protected final List<Month> months = new ArrayList<>();

        public boolean isHasLabel() {
            return StringUtils.isNotBlank(getLabel());
        }

        public String getLabel() {
            if (label == null) {
                StringBuilder builder = new StringBuilder();
                int index = monthRows.indexOf(this);
                int cols = getColumns();
                int y, year = months.get(0).getYear();
                if (index == 0 || monthRows.get(index - 1).getMonths().get(getColumns() - 1).getYear() != year) {
                    builder.append(year);
                }
                for (int i = 1; i < size(); i++) {
                    if ((y = months.get(i).getYear()) != year) {
                        if (builder.length() < 1) {
                            builder.append(year);
                        }
                        builder.append(" / ").append(y);
                        year = y;
                    }
                }
                label = builder.toString();
            }
            return label;
        }

        public List<Month> getMonths() {
            return months;
        }

        public int size() {
            return months.size();
        }

        public void add(Month month) {
            months.add(month);
        }
    }

    private transient Page detailPage;
    private transient Boolean showNavigation;

    private transient Boolean showWeekNumbers;
    private transient Boolean showWeekdayLabels;
    private transient List<String> weekdayLabels;

    private transient Integer columns, rows;

    protected List<Row> monthRows;

    /**
     * @return the current date related scope; default: the current month
     */
    @Nonnull
    public DateRange getDateRange() {
        if (dateRange == null) {
            dateRange = fromRequest(false);
            if (dateRange == null) {
                Locale locale = getLocale();
                TimeZone timeZone = TimeZone.getDefault();
                dateRange = new DateRange(timeZone, locale, MONTH, getYear(), getMonth(), getColumns() * getRows());
            }
        }
        return dateRange;
    }


    public void setRange(DateRange range) {
        monthRows = new ArrayList<>();
        int cols = getColumns();
        List<DateRange> ranges = range.splitMonths();
        Row row = new Row();
        monthRows.add(row);
        for (int i = 0; i < ranges.size(); ) {
            row.add(new Month(ranges.get(i), i / cols, i % cols));
            if (++i < ranges.size() && row.size() == cols) {
                row = new Row();
                monthRows.add(row);
            }
        }
    }

    public List<Row> getMonthRows() {
        if (monthRows == null) {
            setRange(getDateRange());
        }
        return monthRows;
    }

    public int getColumns() {
        if (columns == null) {
            columns = getProperty(PN_COLUMNS, 1);
        }
        return columns;
    }

    public int getRows() {
        if (rows == null) {
            rows = getProperty(PN_ROWS, 1);
        }
        return rows;
    }

    public String getDetailPage() {
        if (detailPage == null) {
            String path = getProperty(PN_DETAIL_PAGE, "");
            detailPage = getPageManager().getPage(context, path);
        }
        return detailPage != null ? detailPage.getCanonicalUrl() : "";
    }

    public String getBackwardRange() {
        return getDateRange().move(-getColumns()).getRule();
    }

    public String getForwardRange() {
        return getDateRange().move(getColumns()).getRule();
    }

    public boolean isShowNavigation() {
        if (showNavigation == null) {
            showNavigation = getProperty(PN_SHOW_NAVIGATION, Boolean.FALSE);
        }
        return showNavigation;
    }

    public boolean isShowWeekNumbers() {
        if (showWeekNumbers == null) {
            showWeekNumbers = getProperty(PN_SHOW_WEEK_NUMBERS, Boolean.FALSE);
        }
        return showWeekNumbers;
    }

    public boolean isShowWeekdayLabels() {
        if (showWeekdayLabels == null) {
            showWeekdayLabels = getProperty(PN_SHOW_WEEKDAY_LABELS, Boolean.FALSE);
        }
        return showWeekdayLabels;
    }

    public List<String> getWeekdayLabels() {
        if (weekdayLabels == null) {
            weekdayLabels = new ArrayList<>();
            Calendar day = (Calendar) getDateRange().getFrom().clone();
            while (day.get(Calendar.DAY_OF_WEEK) != day.getFirstDayOfWeek()) {
                day.add(Calendar.DAY_OF_WEEK, 1);
            }
            SimpleDateFormat format = new SimpleDateFormat("EE", getLocale());
            for (int i = 0; i < 7; i++) {
                weekdayLabels.add(format.format(day.getTime()).substring(0, 1));
                day.add(Calendar.DAY_OF_WEEK, 1);
            }
        }
        return weekdayLabels;
    }
}
