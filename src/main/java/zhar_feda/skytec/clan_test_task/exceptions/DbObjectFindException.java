package zhar_feda.skytec.clan_test_task.exceptions;


import zhar_feda.skytec.clan_test_task.utils.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DbObjectFindException extends RuntimeException {
    public static final String MSG_WITHOUT_CONSTRAINTS = "No %s";
    public static final String MSG_WITH_CONSTRAINTS = "No %s for %s";

    public DbObjectFindException(String objName, Pair<String, Object>... constraints) {
        super(createMsg(objName, constraints));
    }

    private static String createMsg(String objName, Pair<String, Object>... constraints) {
        List<Pair<String, Object>> constraintsList = Arrays.stream(constraints)
                .filter(it -> Objects.nonNull(it.getKey()))
                .collect(Collectors.toList());

        if(constraintsList.size() == 0) {
            return String.format(MSG_WITHOUT_CONSTRAINTS, objName);
        }

        StringBuilder constraintsString = new StringBuilder();
        constraintsList.forEach(it -> {
            String constraintName = it.getKey();
            String constraintVal = it.getValue() != null ? it.getValue().toString() : "null";
            constraintsString.append(constraintName).append("=").append(constraintVal).append(" ");
        });

        return String.format(MSG_WITH_CONSTRAINTS, objName, constraintsString.toString());
    }
}
