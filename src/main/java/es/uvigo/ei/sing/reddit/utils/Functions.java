package es.uvigo.ei.sing.reddit.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@UtilityClass
public class Functions {
    public static LocalDateTime convertToLocalDateTime(Date date) {
        return date != null ? date.toInstant().atZone(ZoneId.of("CET")).toLocalDateTime() : null;
    }

    public static Date convertToDate(LocalDateTime date) {
        return date != null ? Date.from(date.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }

    public static Date unixTimestampToDate(int unixDate) {
        return new Date((long) unixDate * 1000);
    }

    public static LocalDateTime unixTimestampToLocalDateTime(int unixDate) {
        return convertToLocalDateTime(new Date((long) unixDate * 1000));
    }

    public static Date minimumDate() {
        return new GregorianCalendar(1900, Calendar.JANUARY, 1).getTime();
    }

    public static LocalDateTime minimumLocalDateTime() {
        return LocalDateTime.of(1900, 1, 1, 1, 1);
    }

    public static boolean hasChangesToSave(LocalDateTime entityEditDate, LocalDateTime editDate) {
        // Both dates are null or the edit date is the same, then no changes to save
        boolean hasToSave = false;

        // Submission was edited, must update the content
        if ((entityEditDate == null && editDate != null) ||
                (entityEditDate != null && editDate != null) && (entityEditDate.isBefore(editDate)))
            hasToSave = true;

        return hasToSave;
    }

    public static Map<String, String> decomposePSParameters(String urlQuery) {
        Map<String, String> toRet = new HashMap<>();

        // Split query parameters and iterate them
        String[] parameters = urlQuery.split("&");
        for (String parameter : parameters) {
            // Split into query and value
            String[] splitParam = parameter.split("=");

            // Validate empty parameters (e.g. ids=)
            String query = splitParam[0];
            String value = splitParam.length == 1 ? "" : splitParam[1];

            toRet.put(query, value);
        }

        return toRet;
    }

    public static List<String> splitIDsInSublists(Collection<String> ids) {
        final AtomicInteger counter = new AtomicInteger();
        final AtomicInteger group = new AtomicInteger();

        // Split the IDs in multiple sublists based on the length of the URL
        return ids.stream().collect(Collectors.groupingBy(id -> {
            // Sum the ids length + a comma separator (e.g. 123456,)
            int acuLength = counter.accumulateAndGet(id.length() + 1, (left, right) -> left + right);

            // Assign the corresponding sublist based on the acu length
            if (acuLength <= Constants.PS_URL_LIMIT)
                return group.get();
            else {
                // Reset the counter and consider the leftovers
                counter.set(counter.get() - Constants.PS_URL_LIMIT);
                return group.incrementAndGet();
            }
        })).values().parallelStream().map(list -> String.join(",", list)).collect(Collectors.toList());
    }
}
