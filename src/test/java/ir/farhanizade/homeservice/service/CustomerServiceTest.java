package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.entity.Transaction;
import ir.farhanizade.homeservice.entity.order.Comment;
import ir.farhanizade.homeservice.entity.order.ServiceOrder;
import ir.farhanizade.homeservice.entity.order.message.Request;
import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.entity.order.message.SuggestionStatus;
import ir.farhanizade.homeservice.entity.service.MainService;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.exception.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CustomerServiceTest {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ExpertService expertService;
    @Autowired
    private SubServiceService expertiseService;
    @Autowired
    private MainServiceService mainExpertiseService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RequestService requestService;
    @Autowired
    private SuggestionService suggestionService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private CommentSevice commentService;

    @TestConfiguration
    @EnableAspectJAutoProxy
    @ComponentScan("ir.farhanizade.homeservice")
    public static class CustomerServiceTestConfig {
    }

    @Test
    void testSaveCustomer_isOK() {
        Customer customer = Customer.builder()
                .fName("ali")
                .lName("alavi")
                .email("123@123.ir")
                .password("abcd12345")
                .credit(new BigDecimal(0))
                .dateTime(new Date(System.currentTimeMillis()))
                .build();
        try {
            customerService.save(customer);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        Customer result = customerService.findByEmail("123@123.ir");
        assertEquals(customer.getId(), result.getId());
    }

    @Test
    void testSaveCustomerWrongEmail_throwException() {
        Customer customer = Customer.builder()
                .fName("ali")
                .lName("alavi")
                .email("@123.ir")
                .password("abcd1234")
                .credit(new BigDecimal(0))
                .dateTime(new Date(System.currentTimeMillis()))
                .build();
        try {
            customerService.save(customer);
            fail();
        } catch (UserNotValidException e) {
            e.printStackTrace();
        } catch (DuplicateEntityException e) {
            e.printStackTrace();
        } catch (NameNotValidException e) {
            e.printStackTrace();
        } catch (EmailNotValidException e) {
            e.printStackTrace();
            assertTrue(true);
        } catch (PasswordNotValidException e) {
            e.printStackTrace();
        } catch (NullFieldException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSaveCustomerWrongPassword_throwException() {
        Customer customer = Customer.builder()
                .fName("ali")
                .lName("alavi")
                .email("123@123.ir")
                .password("1234")
                .credit(new BigDecimal(0))
                .dateTime(new Date(System.currentTimeMillis()))
                .build();
        try {
            customerService.save(customer);
            fail();
        } catch (UserNotValidException e) {
            e.printStackTrace();
        } catch (DuplicateEntityException e) {
            e.printStackTrace();
        } catch (NameNotValidException e) {
            e.printStackTrace();
        } catch (EmailNotValidException e) {
            e.printStackTrace();
        } catch (PasswordNotValidException e) {
            e.printStackTrace();
            assertTrue(true);
        } catch (NullFieldException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSaveCustomerWrongName_throwException() {
        Customer customer = Customer.builder()
                .fName("al")
                .lName("al")
                .email("123@123.ir")
                .password("abcd1234")
                .credit(new BigDecimal(0))
                .dateTime(new Date(System.currentTimeMillis()))
                .build();
        try {
            customerService.save(customer);
            fail();
        } catch (UserNotValidException e) {
            e.printStackTrace();
        } catch (DuplicateEntityException e) {
            e.printStackTrace();
        } catch (NameNotValidException e) {
            e.printStackTrace();
            assertTrue(true);
        } catch (EmailNotValidException e) {
            e.printStackTrace();
        } catch (PasswordNotValidException e) {
            e.printStackTrace();
        } catch (NullFieldException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testLoadCustomerByCredit_isOK() {
        Customer customer1 = Customer.builder()
                .fName("customer1")
                .lName("customer1")
                .email("customer1@123.ir")
                .password("abcd1234")
                .credit(new BigDecimal(5))
                .dateTime(new Date(System.currentTimeMillis()))
                .build();
        Customer customer2 = Customer.builder()
                .fName("customer2")
                .lName("customer2")
                .email("customer2@123.ir")
                .password("abcd1234")
                .credit(new BigDecimal(5))
                .dateTime(new Date(System.currentTimeMillis()))
                .build();
        try {
            customerService.save(customer1);
            customerService.save(customer2);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        List<Customer> customerList = customerService.findByCredit(new BigDecimal(5));
        boolean resultSize = customerList.size() == 2;
        boolean resultEqual1 = customer1.equals(customerList.get(0));
        boolean resultEqual2 = customer2.equals(customerList.get(1));
        assertTrue(resultSize && resultEqual1 && resultEqual2);
    }


    //Let's call it a whole proj. test :)
    @Test
    void test_Choose_Service_isOK() {

        //Creating a customer
        Customer customer = Customer.builder()
                .fName("ali")
                .lName("alavi")
                .email("123@123.ir")
                .password("abcd12345")
                .credit(new BigDecimal(200))
                .dateTime(new Date(System.currentTimeMillis()))
                .build();

        //Creating a MainService named "parent"
        MainService parent = MainService.builder()
                .name("parent")
                .build();
        try {
            //Saving parent
            mainExpertiseService.save(parent);
        } catch (DuplicateEntityException e) {
            e.printStackTrace();
        }

        //Creating a SubService of parent called "s1"
        SubService s1 = SubService.builder()
                .name("s1")
                .basePrice(new BigDecimal(1))
                .description("")
                .parent(parent)
                .build();

        //Creating a SubService of parent called "s2"
        SubService s2 = SubService.builder()
                .name("s2")
                .basePrice(new BigDecimal(1))
                .description("")
                .parent(parent)
                .build();

        //Creating a Expert with s1 and s2 expertises
        Expert expert1 = Expert.builder()
                .fName("expert1")
                .lName("expert1")
                .email("expert1@expert.expert")
                .password("expert123")
                .expertises(new ArrayList<>(Arrays.asList(s1, s2)))
                .credit(new BigDecimal(0))
                .build();

        //Creating a Expert with s1 expertise
        Expert expert2 = Expert.builder()
                .fName("expert2")
                .lName("expert2")
                .email("expert2@expert.expert")
                .password("expert123")
                .expertises(new ArrayList<>(Arrays.asList(s1)))
                .build();
        try {
            //Saving SubService s1
            expertiseService.save(s1);

            //Saving SubService s2
            expertiseService.save(s2);

            //Saving Expert expert1
            expertService.save(expert1);

            //Saving Expert expert2
            expertService.save(expert2);

            //Saving the customer
            customerService.save(customer);
        } catch (Exception e) {
            e.printStackTrace();

            //Check if saving process is done perfectly
            fail();
        }
        //Loading the MainService I just saved
        List<MainService> mainServices = mainExpertiseService.loadAll();
        MainService mainService = mainServices.get(0);

        //Loading the SubServices I just saved
        List<SubService> subServices = mainService.getSubServices();
        SubService subService = subServices.get(0);

        //Creating an Order with SubService -> s1
        ServiceOrder order = ServiceOrder.builder()
                .service(subService)
                .build();

        //Creating a Request for the order
        Request request = Request.builder()
                .order(order)
                .address("address")
                .details("details")
                .owner(customer)
                .suggestedDateTime(new Date(System.currentTimeMillis()))
                .price(new BigDecimal(100))
                .build();

        //Saving the order with the request
        try {
            requestService.save(request);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        List<ServiceOrder> orderServices = orderService.loadByExpertises(expert1.getExpertises());
        ServiceOrder orderService = orderServices.get(0);

        //Creating a suggestion for that order
        Suggestion suggestion = Suggestion.builder()
                .order(orderService)
                .details("details")
                .owner(expert1)
                .price(new BigDecimal(120))
                .suggestedDateTime(new Date(System.currentTimeMillis()))
                .duration(1.5D)
                .build();

        //Saving the suggestion
        try {
            suggestionService.save(suggestion);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        //Loading the customer to see suggestions for the order
        Customer resultCustomer = customerService.findByEmail("123@123.ir");
        ServiceOrder currentOrder = resultCustomer.getOrders().get(0);
        Suggestion currentSuggestion = currentOrder.getSuggestions().get(0);

        //Accepting the suggestion
        orderService.acceptSuggestion(currentSuggestion);

        /*//Creating a Transaction for the order to pay the expert
        Transaction transaction = Transaction.builder()
                .order(currentOrder)
                .amount(currentOrder.getSuggestion().getPrice())
                .payer(order.getRequest().getOwner())
                .recipient(order.getSuggestion().getOwner())
                .build();
        try {

            //Saving the transaction
            transactionService.save(transaction);
        } catch (Exception e) {
            e.printStackTrace();

            //Check if the saving of transaction is done perfectly
            fail();
        }

        //Creating a Comment for the order
        Comment comment = Comment.builder()
                .order(currentOrder)
                .sender(order.getRequest().getOwner())
                .recipient(order.getSuggestion().getOwner())
                .points(5)
                .description("")
                .build();

        //Saving the comment
        commentService.save(comment);

        //Loading the order to check if the comment is saved flawlessly
        ServiceOrder order1 = this.orderService.loadAll().get(0);
        Comment comment1 = order1.getComment();*/
        assertEquals(SuggestionStatus.ACCEPTED,currentSuggestion.getSuggestionStatus());
    }

}