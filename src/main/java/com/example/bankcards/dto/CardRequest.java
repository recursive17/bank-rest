package com.example.bankcards.dto;

import com.example.bankcards.util.CardStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data

public class CardRequest {
    @NotBlank
    private String cardNumber;

    @NotNull
    private LocalDate expirationDate;

    @NotNull
    private CardStatus status;

    @NotNull
    @PositiveOrZero
    private BigDecimal balance;

    @NotNull
    @Positive
    private Long ownerId;
}
