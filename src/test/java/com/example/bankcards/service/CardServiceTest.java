
package com.example.bankcards.service;

import com.example.bankcards.dto.TransfetRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.MaskUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private MaskUtil maskUtil;

    @Mock
    private CardEncryptionService cardEncryptionService;

    @InjectMocks
    private CardService cardService;

    private User currentUser;
    private Card fromCard;
    private Card toCard;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("mike");

        fromCard = new Card();
        fromCard.setId(10L);
        fromCard.setOwner(currentUser);
        fromCard.setBalance(new BigDecimal("1000.00"));
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setExpirationDate(LocalDate.of(2028, 12, 31));
        fromCard.setCardNumber("encrypted-1");

        toCard = new Card();
        toCard.setId(20L);
        toCard.setOwner(currentUser);
        toCard.setBalance(new BigDecimal("100.00"));
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setExpirationDate(LocalDate.of(2028, 12, 31));
        toCard.setCardNumber("encrypted-2");
    }

    @Test
    void transferBetweenMyCards_shouldTransferMoneySuccessfully() {
        TransfetRequest request = new TransfetRequest();
        request.setFromCardId(10L);
        request.setToCardId(20L);
        request.setAmount(new BigDecimal("150.00"));

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(cardRepository.findByIdAndOwner(10L,
                currentUser)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndOwner(20L, currentUser)).thenReturn(Optional.of(toCard));

        cardService.transfetBetweenMyCards(request);

        assertEquals(new BigDecimal("850.00"), fromCard.getBalance());
        assertEquals(new BigDecimal("250.00"), toCard.getBalance());

        verify(cardRepository, times(1)).save(fromCard);
        verify(cardRepository, times(1)).save(toCard);
    }

    @Test
    void transferBetweenMyCards_shouldThrowWhenSourceCardNotFound() {
        TransfetRequest request = new TransfetRequest();
        request.setFromCardId(10L);
        request.setToCardId(20L);
        request.setAmount(new BigDecimal("150.00"));

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(cardRepository.findByIdAndOwner(10L, currentUser)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cardService.transfetBetweenMyCards(request)
        );

        assertEquals("source not found", exception.getMessage());
    }

    @Test
    void transferBetweenMyCards_shouldThrowWhenCardsAreSame() {
        TransfetRequest request = new TransfetRequest();
        request.setFromCardId(10L);
        request.setToCardId(10L);
        request.setAmount(new BigDecimal("150.00"));

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(cardRepository.findByIdAndOwner(10L,
                currentUser)).thenReturn(Optional.of(fromCard));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cardService.transfetBetweenMyCards(request)
        );

        assertEquals("cannot transfer to same card", exception.getMessage());
    }

    @Test
    void transferBetweenMyCards_shouldThrowWhenSourceCardBlocked() {
        fromCard.setStatus(CardStatus.BLOCKED);

        TransfetRequest request = new TransfetRequest();
        request.setFromCardId(10L);
        request.setToCardId(20L);
        request.setAmount(new BigDecimal("150.00"));

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(cardRepository.findByIdAndOwner(10L,
                currentUser)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndOwner(20L, currentUser)).thenReturn(Optional.of(toCard));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cardService.transfetBetweenMyCards(request)
        );

        assertEquals("source card not active", exception.getMessage());
    }

    @Test
    void transferBetweenMyCards_shouldThrowWhenDestinationCardBlocked() {
        toCard.setStatus(CardStatus.BLOCKED);

        TransfetRequest request = new TransfetRequest();
        request.setFromCardId(10L);
        request.setToCardId(20L);
        request.setAmount(new BigDecimal("150.00"));

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(cardRepository.findByIdAndOwner(10L,
                currentUser)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndOwner(20L, currentUser)).thenReturn(Optional.of(toCard));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cardService.transfetBetweenMyCards(request)
        );

        assertEquals("destination card not active", exception.getMessage());
    }

    @Test
    void transferBetweenMyCards_shouldThrowWhenInsufficientFunds() {
        TransfetRequest request = new TransfetRequest();
        request.setFromCardId(10L);
        request.setToCardId(20L);
        request.setAmount(new BigDecimal("5000.00"));

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(cardRepository.findByIdAndOwner(10L,
                currentUser)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndOwner(20L, currentUser)).thenReturn(Optional.of(toCard));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cardService.transfetBetweenMyCards(request)
        );

        assertEquals("insufficient funds", exception.getMessage());
    }
}
