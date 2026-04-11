package com.example.bankcards.dto;

import com.example.bankcards.util.CardStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardResponse {
    private Long id;
    private String maskedNumber;
    private LocalDate expirationDate;
    private CardStatus status;
    private BigDecimal balance;
    private Long ownerId;
    private String ownerUsername;
}
