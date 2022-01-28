package ir.farhanizade.homeservice.service.util;

import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.order.OrderStatus;
import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import ir.farhanizade.homeservice.entity.order.message.Request;
import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.entity.service.MainService;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.User;
import ir.farhanizade.homeservice.exception.*;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {
    static Pattern pattern;
    static Matcher matcher;

    public static boolean isValid(User user) throws EmailNotValidException, PasswordNotValidException, NameNotValidException, NullFieldException {
        if (user == null)
            throw new NullFieldException("User is null!");
        isEmailValid(user.getEmail());
        String password = user.getPassword();
        if (!passwordIsValid(password))
            throw new PasswordNotValidException("Password is not valid!");

        String fName = user.getFName();
        String lName = user.getLName();
        if (fName.length() < 3 || lName.length() < 3) {
            throw new NameNotValidException("Name is not valid!");
        }
        return true;
    }

    public static boolean isValid(Request request) throws NullFieldException, BadEntryException, NameNotValidException, EmailNotValidException, PasswordNotValidException {
        Order order = request.getOrder();
        isValid(order);
        if (request.getAddress() == null)
            throw new NullFieldException("The address is null");

        SubService service = order.getService();
        if (request.getPrice().compareTo(service.getBasePrice()) == -1) {
            throw new BadEntryException("The price show be equal or greater than the service base price!");
        }
        if (System.currentTimeMillis() - request.getSuggestedDateTime().getTime() > 0) {
            throw new BadEntryException("The requested time is sooner than the present time");
        }
        return true;
    }

    private static boolean isValid(Order order) throws NullFieldException, BadEntryException {
        if (order == null)
            throw new NullFieldException("Order is null!");
        SubService service = order.getService();
        boolean serviceIsValid = isValid(service);
        return true && serviceIsValid;
    }

    private static boolean isValid(SubService service) throws NullFieldException, BadEntryException {
        if (service == null)
            throw new NullFieldException("Service is null!");
        isValid(service.getParent());
        if (service.getName() == null)
            throw new NullFieldException("Service name is null!");
        BigDecimal basePrice = service.getBasePrice();
        if (basePrice.compareTo(new BigDecimal(0)) <= 0)
            throw new BadEntryException("The service price is not valid!");
        return true;
    }

    private static boolean isValid(MainService parent) throws NullFieldException {
        if (parent == null)
            throw new NullFieldException("MainService is null!");
        if (parent.getName() == null)
            throw new NullFieldException("MainService name is null!");
        return true;
    }

    public static boolean isValid(Suggestion suggestion) throws NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, BusyOrderException {
        if (suggestion.getId() != null) return true;
        Expert owner = suggestion.getOwner();
        Order order = suggestion.getOrder();
        if (!owner.getExpertises().contains(order.getService()))
            throw new BadEntryException("This Order Is Not Available For This Expert!");
        Request request = order.getRequest();
        isValid(order);
        SubService service = order.getService();
        if ((!(order.getStatus().equals(OrderStatus.WAITING_FOR_SUGGESTION) ||
                order.getStatus().equals(OrderStatus.WAITING_FOR_SELECTION))) ||
                !request.getStatus().equals(BaseMessageStatus.WAITING))
            throw new BusyOrderException("The order is not open to suggest!");
        if (suggestion.getDuration() <= 0)
            throw new BadEntryException("The duration is 0 or less!");
        if (System.currentTimeMillis() - suggestion.getSuggestedDateTime().getTime() > 0)
            throw new BadEntryException("The suggested time is sooner than the present time");
        if (service.getBasePrice().compareTo(suggestion.getPrice()) > 0)
            throw new BadEntryException("The suggested price is lower than the base price!");
        return true;
    }

    public static boolean passwordIsValid(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,20}$";
        pattern = Pattern.compile(passwordPattern);
        matcher = pattern.matcher(password);
        boolean passwordIsValid = matcher.matches();
        return passwordIsValid;
    }

    public static boolean isEmailValid(String email) throws EmailNotValidException, NullFieldException {
        if (email.isEmpty()) throw new NullFieldException("Email is Empty!");

        String emailPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(emailPattern);
        matcher = pattern.matcher(email);
        boolean emailIsValid = matcher.matches();
        if (!emailIsValid) {
            throw new EmailNotValidException("Email is not valid!");
        }
        return true;
    }

    public static void enableUser(User user) {
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
    }
}