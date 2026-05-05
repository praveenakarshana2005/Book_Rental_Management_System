package model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReportReturn {

    private int returnId;
    private String customer;
    private String date;
    private String books;
    private double fine;

    public char[] getTotalFine() {
        String fineString = String.valueOf(fine);
        return fineString.toCharArray();
    }
}
