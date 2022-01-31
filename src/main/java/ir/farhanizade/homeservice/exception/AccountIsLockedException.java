package ir.farhanizade.homeservice.exception;

public class AccountIsLockedException extends Exception {
    public AccountIsLockedException(String message) {
        super(message);
    }
}
