package zhar_feda.skytec.clan_test_task.validators.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedRuntimeException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import zhar_feda.skytec.clan_test_task.models.ClanGoldTransactionGoal;
import zhar_feda.skytec.clan_test_task.validators.ValidationResult;
import zhar_feda.skytec.clan_test_task.validators.Validator;

@Component
public class ClanGoldTransactionCreationValidator implements Validator<ClanGoldTransactionCreationValidator.Args> {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Args {
        private Long clanId;
        private String operationOwner;
        private String operationName;
        private Long goldDiff;
        private ClanGoldTransactionGoal operationGoal;
    }

    private static final String CLAN_NOT_EXIST = "Clan not exist";
    public static final String SQL_EXCEPTION_MSG = "Can't create clan. Sql problem: %s";

    public static final String EMPTY_OWNER_EXCEPTION_MSG = "Empty owner name";
    public static final String WRONG_LEN_OWNER_EXCEPTION_MSG = "Owner name is too long";
    public static final int MAX_OWNER_NAME_LEN = 255;

    public static final int MAX_OPERATION_NAME_LEN = 255;
    public static final String EMPTY_OPERATION_EXCEPTION_MSG = "Empty operation name";
    public static final String WRONG_LEN_OPERATION_EXCEPTION_MSG = "Operation name is too long";

    public static final String WRONG_GOLD_DIF_EXCEPTION_MSG = "Wrong gold dif: %s";
    public static final String NULL_OPERATION_GOAL_EXCEPTION_MSG = "ull operation goal";



    private static final String EXIST_BY_NAME_QUERY = "select count(*) as count from clan where id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ClanGoldTransactionCreationValidator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ValidationResult isValid(Args validatedObject) {
        if(validatedObject.goldDiff == null ||  validatedObject.goldDiff <= 0) {
            return ValidationResult.notValid(WRONG_GOLD_DIF_EXCEPTION_MSG);
        }
        if(validatedObject.operationGoal == null) {
            return ValidationResult.notValid(NULL_OPERATION_GOAL_EXCEPTION_MSG);
        }
        if(StringUtils.isEmpty(validatedObject.operationName)) {
            return ValidationResult.notValid(EMPTY_OPERATION_EXCEPTION_MSG);
        }
        if(validatedObject.operationName.length() > MAX_OPERATION_NAME_LEN) {
            return ValidationResult.notValid(WRONG_LEN_OPERATION_EXCEPTION_MSG);
        }

        if(StringUtils.isEmpty(validatedObject.operationOwner)) {
            return ValidationResult.notValid(EMPTY_OWNER_EXCEPTION_MSG);
        }
        if(validatedObject.operationOwner.length() > MAX_OWNER_NAME_LEN) {
            return ValidationResult.notValid(WRONG_LEN_OWNER_EXCEPTION_MSG);
        }

        try {
            if (isClanNotExistById(validatedObject.clanId)) {
                return ValidationResult.notValid(CLAN_NOT_EXIST);
            }
        } catch (NestedRuntimeException e) {
            return ValidationResult.notValid(String.format(SQL_EXCEPTION_MSG, e.getMessage()));
        }
        return ValidationResult.valid();
    }

    private boolean isClanNotExistById(Long clanId) {
        long countByName = jdbcTemplate.queryForObject(
                EXIST_BY_NAME_QUERY,
                new Object[]{clanId},
                Long.class);
        return countByName <= 0;
    }
}
