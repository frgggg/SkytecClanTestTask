package zhar_feda.skytec.clan_test_task.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import zhar_feda.skytec.clan_test_task.exceptions.ClanCreationException;
import zhar_feda.skytec.clan_test_task.exceptions.DbObjectFindException;
import zhar_feda.skytec.clan_test_task.exceptions.ClanGoldTransactionCreationException;

@Slf4j
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(DbObjectFindException.class)
    protected ResponseEntity<String> exceptionHandler(DbObjectFindException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ClanCreationException.class)
    protected ResponseEntity<String> exceptionHandler(ClanCreationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ClanGoldTransactionCreationException.class)
    protected ResponseEntity<String> exceptionHandler(ClanGoldTransactionCreationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
