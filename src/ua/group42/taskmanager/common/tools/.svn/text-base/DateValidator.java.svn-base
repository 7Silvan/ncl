package ua.group42.taskmanager.tools;

/**
 * 
 * @author Silvan
 */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Perform date validations.
 */
public class DateValidator {

    /**
     * <p>Checks if the field is a valid date.  The pattern is used with
     * <code>java.text.SimpleDateFormat</code>.  If strict is true, then the
     * length will be checked so '2/12/1999' will not pass validation with
     * the format 'MM/dd/yyyy' because the month isn't two digits.
     * The setLenient method is set to <code>false</code> for all.</p>
     *
     * @param value The value validation is being performed on.
     * @param datePattern The pattern passed to <code>SimpleDateFormat</code>.
     * @param strict Whether or not to have an exact match of the datePattern.
     * @return true if the date is valid.
     */
    public static boolean isValid(String value, String datePattern, boolean strict) {

        if (value == null
                || datePattern == null
                || datePattern.length() <= 0) {

            return false;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        formatter.setLenient(false);

        try {
            formatter.parse(value);
        } catch(ParseException e) {
            return false;
        }

        if (strict && (datePattern.length() != value.length())) {
            return false;
        }

        return true;
    }

    /**
     * <p>Checks if the field is a valid date.  The <code>Locale</code> is
     * used with <code>java.text.DateFormat</code>.  The setLenient method
     * is set to <code>false</code> for all.</p>
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the date format, defaults to the default
     * system default if null.
     * @return true if the date is valid.
     */
    public static boolean isValid(String value, Locale locale) {

        if (value == null) {
            return false;
        }

        DateFormat formatter = null;
        if (locale != null) {
            formatter = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        } else {
            formatter =
                    DateFormat.getDateInstance(
                            DateFormat.SHORT,
                            Locale.getDefault());
        }

        formatter.setLenient(false);

        try {
            formatter.parse(value);
        } catch(ParseException e) {
            return false;
        }

        return true;
    }
}   