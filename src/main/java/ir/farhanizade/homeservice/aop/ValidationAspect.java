package ir.farhanizade.homeservice.aop;

import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.exception.BadEntryException;
import ir.farhanizade.homeservice.exception.ExpertNotAcceptedException;
import ir.farhanizade.homeservice.security.user.LoggedInUser;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ValidationAspect {
    @Pointcut("@annotation(ir.farhanizade.homeservice.security.user.Accepted)")
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    @Before("springBeanPointcut()")
    public Object validationCheck() throws Throwable {
        UserStatus status = LoggedInUser.getStatus();
        if (!UserStatus.ACCEPTED.equals(status)) throw new ExpertNotAcceptedException("User is not allowed!");
        return null;
    }

}
