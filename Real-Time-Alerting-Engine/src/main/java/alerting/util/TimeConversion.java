package alerting.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeConversion {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    public static final ZoneId zoneId = ZoneId.systemDefault();

    // String Time -> LocalDateTime Time -> EpochMilli Time

    public static LocalDateTime stringToLocalDateTime(String time) {
        return LocalDateTime.parse(time, formatter);
    }

    public static long localDateTimeToEpochMilli(LocalDateTime time) {
        return time.toInstant(zoneId.getRules().getOffset(time)).toEpochMilli();
    }

    public static long stringToEpochMilli(String time) {
        return localDateTimeToEpochMilli(stringToLocalDateTime(time));
    }


    // EpochMilli Time -> LocalDateTime Time -? String Time

    public static LocalDateTime epochMilliToLocalDateTime(long epochMilli) {
        return Instant.ofEpochMilli(epochMilli).atZone(zoneId).toLocalDateTime();
    }

    public static String localDateTimeToString(LocalDateTime time) {
        return time.format(formatter);
    }

    public static String epochMilliToString(long time) {
        return localDateTimeToString(epochMilliToLocalDateTime(time));
    }

}
