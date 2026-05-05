package model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Customer {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String membershipNo;

}
