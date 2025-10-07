package com.finance.manager.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDTO {
    private Long id;
    private Long accountId;
    private BigDecimal amount;
    private String type;
    private String category;
    private String description;
    private LocalDate date;
}
