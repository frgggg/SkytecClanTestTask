package zhar_feda.skytec.clan_test_task.validators.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;
import zhar_feda.skytec.clan_test_task.models.Clan;
import zhar_feda.skytec.clan_test_task.validators.ValidationResult;
import zhar_feda.skytec.clan_test_task.validators.Validator;

@Component
public class ClanGoldUpValidation implements Validator<ClanGoldUpValidation.Args> {
    @Data
    @AllArgsConstructor
    public static class Args {
        private final Clan clan;
        private final Long upFor;
    }

    public static final String NULL_CLAN_MSG = "Null clan";
    public static final String NULL_UP_FOR = "Null golds for up";
    public static final String WRONG_UP_FOR = "Wrong golds for up";
    public static final String CANT_UP_FOR = "Can't up. Result count more then max count";

    @Override
    public ValidationResult isValid(Args upArgs) {
        if(upArgs.getClan() == null) {
            return ValidationResult.notValid(NULL_CLAN_MSG);
        }
        if(upArgs.getUpFor() == null) {
            return ValidationResult.notValid(NULL_UP_FOR);
        }
        if(upArgs.getUpFor() <= 0) {
            return ValidationResult.notValid(WRONG_UP_FOR);
        }
        if((Long.MAX_VALUE - upArgs.clan.getGold()) < upArgs.upFor) {
            return ValidationResult.notValid(CANT_UP_FOR);
        }
        return ValidationResult.valid();
    }
}
