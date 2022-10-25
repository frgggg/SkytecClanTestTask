package zhar_feda.skytec.clan_test_task.validators.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;
import zhar_feda.skytec.clan_test_task.models.Clan;
import zhar_feda.skytec.clan_test_task.validators.ValidationResult;
import zhar_feda.skytec.clan_test_task.validators.Validator;

@Component
public class ClanGoldDownValidation implements Validator<ClanGoldDownValidation.Args> {
    @Data
    @AllArgsConstructor
    public static class Args {
        private final Clan clan;
        private final Long downFor;
    }

    public static final String NULL_CLAN_MSG = "Null clan";
    public static final String NULL_DOWN_FOR = "Null golds for down";
    public static final String WRONG_DOWN_FOR = "Wrong golds for down";
    public static final String CANT_DOWN_FOR = "Can't down. Result count less then min count";

    @Override
    public ValidationResult isValid(Args downArgs) {
        if(downArgs.getClan() == null) {
            return ValidationResult.notValid(NULL_CLAN_MSG);
        }
        if(downArgs.getDownFor() == null) {
            return ValidationResult.notValid(NULL_DOWN_FOR);
        }
        if(downArgs.getDownFor() <= 0) {
            return ValidationResult.notValid(WRONG_DOWN_FOR);
        }
        if(downArgs.clan.getGold() < downArgs.downFor) {
            return ValidationResult.notValid(CANT_DOWN_FOR);
        }
        return ValidationResult.valid();
    }
}
