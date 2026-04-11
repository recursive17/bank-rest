package com.example.bankcards.controller;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.TransfetRequest;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardController {
    private final CardService cardService;

    public CardController(CardService cardService){
        this.cardService = cardService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CardResponse create(@Valid @RequestBody CardRequest request){
        return cardService.create(request);
    }
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my")
    public Page<CardResponse> getMyCards(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search){
        return cardService.getMyCards(page, size, search);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<CardResponse> getAll(){
        return cardService.getAll();
    }




    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my/{id}")
    public CardResponse getCardById(@PathVariable Long id){
        return cardService.getMyCardById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/block")
    public CardResponse blockCard(@PathVariable Long id){
        return cardService.blockCard(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activate")
    public CardResponse activateCard(@PathVariable Long id){
        return cardService.activateCard(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCard(@PathVariable Long id){
        cardService.deleteCard(id);
    }
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/balance/{id}")
    public String balanceCard(@PathVariable Long id){
        return cardService.getBalance(id);
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{id}/request-block")
    public CardResponse requestBlockMyCard(@PathVariable Long id){
        return cardService.requestBlockMyCard(id);
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/my/transfer")
    public void transferBetweenMyCards(@Valid @RequestBody TransfetRequest transfetRequest){
        cardService.transfetBetweenMyCards(transfetRequest);
    }


}
