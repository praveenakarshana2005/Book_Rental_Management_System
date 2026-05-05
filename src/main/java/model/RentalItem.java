package model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RentalItem {
    private int id;
    private int rentalId;
    private Book book;
    private int quantity;
    private double perDayFine;
    private int returnedQuantity;

}
