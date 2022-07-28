package ir.farhanizade.homeservice.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsernameAndPasswordAuthenticationRequest implements Serializable {

    private String username;
    private String password;
}
