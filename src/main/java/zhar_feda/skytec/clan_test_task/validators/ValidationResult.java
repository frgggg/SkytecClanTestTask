package zhar_feda.skytec.clan_test_task.validators;

import lombok.Getter;

@Getter
public class ValidationResult {
    private final boolean isValid;
    private final String msg;

    private ValidationResult(boolean isValid, String msg) {
        this.isValid = isValid;
        this.msg = msg;
    }

    public boolean isValid() {
        return isValid;
    }

    public boolean isNotValid() {
        return !isValid;
    }

    public static ValidationResult valid() {
        return new ValidationResult(true, "");
    }

    public static ValidationResult notValid(String msg) {
        return new ValidationResult(false, msg);
    }
}
