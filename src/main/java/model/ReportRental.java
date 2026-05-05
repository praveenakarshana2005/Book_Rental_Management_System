package model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class ReportRental {

        private int rentalId;
        private String customer;
        private String date;
        private String books;
        private int totalQty;


}
