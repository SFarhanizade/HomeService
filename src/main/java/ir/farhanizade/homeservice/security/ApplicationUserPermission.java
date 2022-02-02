package ir.farhanizade.homeservice.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

public enum ApplicationUserPermission {
    ADMIN_WRITE,
    ADMIN_READ,
    USER_WRITE,
    USER_READ,
    CUSTOMER_WRITE,
    CUSTOMER_READ,
    EXPERT_WRITE,
    EXPERT_READ,
    ORDER_WRITE,
    ORDER_READ,
    SUGGESTION_WRITE,
    SUGGESTION_READ,
    COMMENT_WRITE,
    COMMENT_READ,
    CREDIT_WRITE,
    CREDIT_READ,
    TRANSACTION_WRITE,
    TRANSACTION_READ
}
