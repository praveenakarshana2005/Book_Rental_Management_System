package model;

import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Rental {
    private int id;
    private String rentalNo;
    private Customer customer;
    private User issuedBy;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;
    private double totalFine;
    private List<RentalItem> items = new ArrayList<>();



}
