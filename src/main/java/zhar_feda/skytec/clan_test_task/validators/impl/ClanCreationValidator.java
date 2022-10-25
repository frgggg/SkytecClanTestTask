package zhar_feda.skytec.clan_test_task.validators.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedRuntimeException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import zhar_feda.skytec.clan_test_task.validators.ValidationResult;
import zhar_feda.skytec.clan_test_task.validators.Validator;

@Component
public class ClanCreationValidator implements Validator<String> {
    public static final int MAX_CLAN_NAME_LEN = 255;
    public static final String EMPTY_NAME_EXCEPTION_MSG = "Can't create clan: name is empty";
    public static final String EXIST_NAME_EXCEPTION_MSG = "Can't create clan: name is busy";
    public static final String WRONG_LEN_NAME_EXCEPTION_MSG = "Can't create clan: name is too long";
    public static final String SQL_EXCEPTION_MSG = "Can't create clan. Sql problem: %s";

    private static final String EXIST_BY_NAME_QUERY = "select count(*) as count from clan where name = ?";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ClanCreationValidator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ValidationResult isValid(String name) {
        if(StringUtils.isEmpty(name)) {
            return ValidationResult.notValid(EMPTY_NAME_EXCEPTION_MSG);
        }
        if(name.length() > MAX_CLAN_NAME_LEN) {
            return ValidationResult.notValid(WRONG_LEN_NAME_EXCEPTION_MSG);
        }
        try {
            if (isNameBusy(name)) {
                return ValidationResult.notValid(EXIST_NAME_EXCEPTION_MSG);
            }
        } catch (NestedRuntimeException e) {
            return ValidationResult.notValid(String.format(SQL_EXCEPTION_MSG, e.getMessage()));
        }
        return ValidationResult.valid();
    }

    private boolean isNameBusy(String name) {
        long countByName = jdbcTemplate.queryForObject(
                EXIST_BY_NAME_QUERY,
                new Object[]{name},
                Long.class);
        return countByName > 0;
    }
}
