package ir.farhanizade.homeservice.service.util;

import ir.farhanizade.homeservice.entity.user.User;
import ir.farhanizade.homeservice.exception.EmailNotValidException;
import ir.farhanizade.homeservice.exception.NameNotValidException;
import ir.farhanizade.homeservice.exception.PasswordNotValidException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {
    public static boolean isValid(User user) throws EmailNotValidException, PasswordNotValidException, NameNotValidException {
        if(user==null)
            throw new IllegalArgumentException();
        String email = user.getEmail();
        String emailPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        boolean emailIsValid = matcher.matches();
        if(!emailIsValid) {
            throw new EmailNotValidException("");
        }

        String password = user.getPassword();
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8}$";
        pattern = Pattern.compile(passwordPattern);
        matcher = pattern.matcher(password);
        boolean passwordIsValid = matcher.matches();
        if (!passwordIsValid)
            throw new PasswordNotValidException("");

        String fName = user.getFName();
        String lName = user.getLName();
        if(fName.length()<3 || lName.length()<3) {
            throw new NameNotValidException("");
        }
        return true;
    }
}
