package model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Book {
    private int id;
    private String isbn;
    private String title;
    private String author;
    private String category;
    private int totalQuantity;
    private int availableQuantity;
    private double price;



}
