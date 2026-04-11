package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.security.JwtAuthenticationFilter;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void create_shouldReturnCard() throws Exception {
        CardResponse response = buildCardResponse();

        when(cardService.create(any())).thenReturn(response);

        String requestBody = """
                  {
                    "cardNumber": "4111111111111111",
                    "expirationDate": "2028-12-31",
                    "status": "ACTIVE",
                    "balance": 1000,
                    "ownerId": 2
                  }
                  """;

        mockMvc.perform(post("/cards")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.maskedNumber").value("**** **** **** 1111"))
                .andExpect(jsonPath("$.ownerId").value(2));
    }

    @Test
    void getAll_shouldReturnCards() throws Exception {
        when(cardService.getAll()).thenReturn(List.of(buildCardResponse()));

        mockMvc.perform(get("/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getMyCards_shouldReturnPage() throws Exception {
        CardResponse response = buildCardResponse();

        when(cardService.getMyCards(eq(0), eq(10), eq("1111")))
                .thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/cards/my")
                        .param("page", "0")
                        .param("size", "10")
                        .param("search", "1111"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].maskedNumber").value("**** **** **** 1111"));
    }

    @Test
    void getCardById_shouldReturnCard() throws Exception {
        when(cardService.getMyCardById(1L)).thenReturn(buildCardResponse());

        mockMvc.perform(get("/cards/my/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getBalance_shouldReturnBalance() throws Exception {
        when(cardService.getBalance(1L)).thenReturn("1000.00");

        mockMvc.perform(get("/cards/balance/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("1000.00"));
    }

    @Test
    void transfer_shouldReturnOk() throws Exception {
        doNothing().when(cardService).transfetBetweenMyCards(any());

        String requestBody = """
                  {
                    "fromCardId": 1,
                    "toCardId": 2,
                    "amount": 150
                  }
                  """;

        mockMvc.perform(patch("/cards/my/transfer")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void blockCard_shouldReturnUpdatedCard() throws Exception {
        CardResponse response = buildCardResponse();
        response.setStatus(CardStatus.BLOCKED);

        when(cardService.blockCard(1L)).thenReturn(response);

        mockMvc.perform(patch("/cards/1/block"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }

    @Test
    void deleteCard_shouldReturnOk() throws Exception {
        doNothing().when(cardService).deleteCard(1L);

        mockMvc.perform(delete("/cards/1"))
                .andExpect(status().isOk());
    }

    private CardResponse buildCardResponse() {
        CardResponse response = new CardResponse();
        response.setId(1L);
        response.setMaskedNumber("**** **** **** 1111");
        response.setExpirationDate(LocalDate.of(2028, 12, 31));
        response.setStatus(CardStatus.ACTIVE);
        response.setBalance(new BigDecimal("1000.00"));
        response.setOwnerId(2L);
        response.setOwnerUsername("mike01");
        return response;
    }
}
