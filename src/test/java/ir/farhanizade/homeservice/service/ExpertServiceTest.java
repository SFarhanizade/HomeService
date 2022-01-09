package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.entity.service.MainService;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.exception.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ExpertServiceTest {
    @Autowired
    private ExpertService expertService;
    @Autowired
    private SubServiceService expertiseService;
    @Autowired
    private MainServiceService mainExpertiseService;

    @TestConfiguration
    @ComponentScan("ir.farhanizade.homeservice")
    public static class ExpertServiceTestConfig {
    }

    @Test
    void test_save_expert_isOK() {
        Expert expert = Expert.builder()
                .fName("expert")
                .lName("expert")
                .email("expert@expert.expert")
                .password("expert123")
                .build();
        try {
            expertService.save(expert);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        Expert byEmail = expertService.findByEmail("expert@expert.expert");
        assertEquals(expert, byEmail);
    }

    @Test
    void testSaveExpertWrongEmail_throwException() {
        Expert expert = Expert.builder()
                .fName("ali")
                .lName("alavi")
                .email("@123.ir")
                .password("abcd1234")
                .build();
        try {
            expertService.save(expert);
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
    void testSaveExpertWrongPassword_throwException() {
        Expert expert = Expert.builder()
                .fName("ali")
                .lName("alavi")
                .email("123@123.ir")
                .password("1234")
                .credit(new BigDecimal(0))
                .dateTime(new Date(System.currentTimeMillis()))
                .build();
        try {
            expertService.save(expert);
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
    void testSaveExpertWrongName_throwException() {
        Expert expert = Expert.builder()
                .fName("al")
                .lName("al")
                .email("123@123.ir")
                .password("abcd1234")
                .credit(new BigDecimal(0))
                .dateTime(new Date(System.currentTimeMillis()))
                .build();
        try {
            expertService.save(expert);
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
    void testLoadExpertByNullEmail_throwException() {
        Expert expert = Expert.builder()
                .fName("ali")
                .lName("alavi")
                .email("123@123.ir")
                .password("abcd1234")
                .credit(new BigDecimal(0))
                .dateTime(new Date(System.currentTimeMillis()))
                .build();
        try {
            expertService.save(expert);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            expertService.findByEmail(null);
            fail();
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e.getMessage().equals("Null Email"));
        }

    }

    @Test
    void testLoadExpertByCredit_isOK() {
        Expert expert1 = Expert.builder()
                .fName("expert1")
                .lName("expert1")
                .email("expert1@123.ir")
                .password("abcd1234")
                .credit(new BigDecimal(5))
                .dateTime(new Date(System.currentTimeMillis()))
                .build();
        Expert expert2 = Expert.builder()
                .fName("expert2")
                .lName("expert2")
                .email("expert2@123.ir")
                .password("abcd1234")
                .credit(new BigDecimal(5))
                .dateTime(new Date(System.currentTimeMillis()))
                .build();
        try {
            expertService.save(expert1);
            expertService.save(expert2);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        List<Expert> expertList = expertService.findByCredit(new BigDecimal(5));
        boolean resultSize = expertList.size() == 2;
        boolean resultEqual1 = expert1.equals(expertList.get(0));
        boolean resultEqual2 = expert2.equals(expertList.get(1));
        assertTrue(resultSize && resultEqual1 && resultEqual2);
    }

    @Test
    void testLoadExpertByStatus_isOK() {
        Expert expert1 = Expert.builder()
                .fName("expert1")
                .lName("expert1")
                .email("expert1@123.ir")
                .password("abcd1234")
                .credit(new BigDecimal(5))
                .dateTime(new Date(System.currentTimeMillis()))
                .build();
        Expert expert2 = Expert.builder()
                .fName("expert2")
                .lName("expert2")
                .email("expert2@123.ir")
                .password("abcd1234")
                .credit(new BigDecimal(5))
                .dateTime(new Date(System.currentTimeMillis()))
                .build();
        try {
            expertService.save(expert1);
            expertService.save(expert2);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        List<Expert> expertList = expertService.findByStatus(UserStatus.NEW);
        boolean resultSize = expertList.size() == 2;
        boolean resultEqual1 = expert1.equals(expertList.get(0));
        boolean resultEqual2 = expert2.equals(expertList.get(1));
        assertTrue(resultSize && resultEqual1 && resultEqual2);
    }

    @Test
    void test_save_add_expertise_isOK() {
        MainService parent = MainService.builder()
                .name("parent")
                .build();
        try {
            mainExpertiseService.save(parent);
        } catch (DuplicateEntityException e) {
            e.printStackTrace();
        } catch (NullFieldException e) {
            e.printStackTrace();
        }
        SubService s1 = SubService.builder()
                .name("s1")
                .basePrice(new BigDecimal(1))
                .description("")
                .parent(parent)
                .build();

        SubService s2 = SubService.builder()
                .name("s2")
                .basePrice(new BigDecimal(1))
                .description("")
                .parent(parent)
                .build();

        Expert expert = Expert.builder()
                .fName("expert")
                .lName("expert")
                .email("expert@expert.expert")
                .password("expert123")
                .expertises(List.of(s1, s2))
                .build();
        try {
            expertiseService.save(s1, parent.getId());
            expertiseService.save(s2, parent.getId());
            expertService.save(expert);
            Expert byEmail = expertService.findByEmail("expert@expert.expert");
            assertEquals(2, byEmail.getExpertises().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

    }

    @Test
    void testLoadExpertByExpertise_isOK() {
        MainService parent = MainService.builder()
                .name("parent")
                .build();
        try {
            mainExpertiseService.save(parent);
        } catch (DuplicateEntityException e) {
            e.printStackTrace();
        } catch (NullFieldException e) {
            e.printStackTrace();
        }
        SubService s1 = SubService.builder()
                .name("s1")
                .basePrice(new BigDecimal(1))
                .description("")
                .parent(parent)
                .build();

        SubService s2 = SubService.builder()
                .name("s2")
                .basePrice(new BigDecimal(1))
                .description("")
                .parent(parent)
                .build();

        Expert expert1 = Expert.builder()
                .fName("expert1")
                .lName("expert1")
                .email("expert1@expert.expert")
                .password("expert123")
                .expertises(List.of(s1, s2))
                .build();
        Expert expert2 = Expert.builder()
                .fName("expert2")
                .lName("expert2")
                .email("expert2@expert.expert")
                .password("expert123")
                .expertises(List.of(s1))
                .build();
        try {
            expertiseService.save(s1, parent.getId());
            expertiseService.save(s2, parent.getId());
            expertService.save(expert1);
            expertService.save(expert2);
            List<Expert> byExpertise = expertService.findByExpertise(s2);
            boolean resultSize = byExpertise.size() == 1;
            boolean equals = expert1.equals(byExpertise.get(0));
            assertTrue(resultSize && equals);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}