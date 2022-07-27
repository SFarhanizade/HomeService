package ir.farhanizade.homeservice.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut("within(@org.springframework.stereotype.Service *)" +
            " || within(ir.farhanizade.homeservice.service.util.*)")
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    @Around("springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        log.info("Params: ");
        for (Object arg : args) {
            if (arg == null)
                log.info(null);
            else log.info(arg.toString());
        }
        Object result = joinPoint.proceed();
        Object target = joinPoint.getTarget();
        String value = "";
        if (target == null)
            value = null;
        else value = target.toString();
        log.debug("Return value: " + value);
        return result;
    }
}
