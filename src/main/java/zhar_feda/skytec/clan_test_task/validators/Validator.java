package zhar_feda.skytec.clan_test_task.validators;

public interface Validator<I> {
    ValidationResult isValid(I validatedObject);
}
