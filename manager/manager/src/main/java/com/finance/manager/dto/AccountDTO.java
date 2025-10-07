package com.finance.manager.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDTO {
    private Long id;
    private String name;
    private String type;
    private BigDecimal balance;
}

