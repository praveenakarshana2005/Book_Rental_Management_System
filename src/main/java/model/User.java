package model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString


public class User {

    private int id;
    private String username;
    private String passwordHash;
    private String fullName;

    private Role role;


}

