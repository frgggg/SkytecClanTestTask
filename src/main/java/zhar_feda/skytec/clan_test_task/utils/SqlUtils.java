package zhar_feda.skytec.clan_test_task.utils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SqlUtils {

    public static final int DEF_PAGE = 0;
    public static final int DEF_PAGE_SIZE = 10;
    public static String longListToSqlList(List<Long> list) {
        if(!isListNotNullAndNotEmpty(list)) {
            return "";
        }

        List<String> listOfString = list.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.toList());

        return String.join(",", listOfString);
    }

    public static <T> String stringListToSqlList(List<T> list) {
        if(!isListNotNullAndNotEmpty(list)) {
            return "";
        }

        List<String> listOfString = list.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .map(SqlUtils::stringToSqlVal)
                .collect(Collectors.toList());

        return String.join(",", listOfString);
    }

    private static String stringToSqlVal(String str) {
        return String.format("'%s'", str);
    }

    public static boolean isListNotNullAndNotEmpty(List list) {
        return list != null && list.size() > 0;
    }
}
