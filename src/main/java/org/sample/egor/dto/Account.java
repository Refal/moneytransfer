package org.sample.egor.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@ApiModel
@NoArgsConstructor(force = true)
@AllArgsConstructor
public @Data
class Account {
    private String accountNumber;
    private BigDecimal amount;
}
