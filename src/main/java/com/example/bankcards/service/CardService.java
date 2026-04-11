package com.example.bankcards.service;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.TransfetRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.MaskUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final MaskUtil maskUtil;
    private final CardEncryptionService cardEncryptionService;

//    public CardService(CardRepository cardRepository, UserRepository userRepository, MaskUtil maskUtil){
//        this.cardRepository = cardRepository;
//        this.userRepository = userRepository;
//        this.maskUtil = maskUtil;
//    }

    public CardResponse create(CardRequest request){
        User owner = userRepository.findById(request.getOwnerId()).orElseThrow(()->new IllegalArgumentException(("owner not found " + request.getOwnerId())));
        String normalizedNumber = request.getCardNumber().replaceAll("\\s","");

        Card card = new Card();
        card.setCardNumber(cardEncryptionService.encrypt(normalizedNumber));
        card.setExpirationDate(request.getExpirationDate());
        card.setStatus(request.getStatus());
        card.setBalance(request.getBalance());
        card.setOwner(owner);

        Card savedCard = cardRepository.save(card);

        return toResponse(savedCard);
    }
    public List<CardResponse> getAll(){
        return cardRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CardResponse blockCard(Long id){
        Card card = cardRepository.findById(id).orElseThrow(()->new IllegalArgumentException("card not found"));
        card.setStatus(CardStatus.BLOCKED);
        Card savedCard = cardRepository.save(card);
        return toResponse(savedCard);
    }
    public CardResponse activateCard(Long id){
        Card card = cardRepository.findById(id).orElseThrow(()->new IllegalArgumentException("card not found"));
        card.setStatus(CardStatus.ACTIVE);
        Card savedCard = cardRepository.save(card);
        return toResponse(savedCard);
    }
    public void deleteCard(Long id){
        Card card = cardRepository.findById(id).orElseThrow(()->new IllegalArgumentException("card not found"));
        cardRepository.delete(card);
    }
    public String getBalance(Long id){
        User currentUser = userService.getCurrentUser();
        Card userCard = cardRepository.findByIdAndOwner(id,currentUser).orElseThrow(()->new IllegalArgumentException("card not found"));
        return userCard.getBalance().toString();

    }
    public Page<CardResponse> getMyCards(int page, int size, String search){
        if (page < 0) {
            throw new IllegalArgumentException("page must be greater than or equal to 0");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than 0");
        }

        User currentUser = userService.getCurrentUser();

        List<CardResponse> filteredCards = cardRepository.findByOwner(currentUser).stream()
                .sorted(Comparator.comparing(Card::getId))
                .map(this::toResponse)
                .filter(card -> matchesSearch(card, search))
                .toList();

        int start = page * size;
        if (start >= filteredCards.size()) {
            return new PageImpl<>(List.of(), PageRequest.of(page, size), filteredCards.size());
        }

        int end = Math.min(start + size, filteredCards.size());
        List<CardResponse> pageContent = filteredCards.subList(start, end);

        return new PageImpl<>(pageContent, PageRequest.of(page, size), filteredCards.size());
    }
    public CardResponse requestBlockMyCard(Long id){
        User currentUser = userService.getCurrentUser();
        Card card = cardRepository.findByIdAndOwner(id,currentUser).orElseThrow(()->new IllegalArgumentException("card not found"));
        card.setStatus(CardStatus.BLOCKED);
        Card savedCard = cardRepository.save(card);
        return toResponse(savedCard);
    }
    public List<CardResponse> getMyCards(){
        User currentUser = userService.getCurrentUser();
        return cardRepository.findByOwner(currentUser).stream().map(this::toResponse).toList();
    }
    public CardResponse getMyCardById(Long cardId){
        User currentUser = userService.getCurrentUser();

        Card card = cardRepository.findByIdAndOwner(cardId,currentUser).orElseThrow(()->new IllegalArgumentException("card not found"));
        return toResponse(card);
    }

    public CardResponse toResponse(Card card){
        CardResponse response = new CardResponse();
        response.setId(card.getId());
        response.setBalance(card.getBalance());
        response.setStatus(card.getStatus());
        response.setExpirationDate(card.getExpirationDate());
        String decryptedNumber = cardEncryptionService.decrypt(card.getCardNumber());
        response.setMaskedNumber(maskUtil.mask(decryptedNumber));

        if(card.getOwner()!=null){
            response.setOwnerId(card.getOwner().getId());
            response.setOwnerUsername(card.getOwner().getUsername());
        }

        return response;
    }

    @Transactional
    public void transfetBetweenMyCards(TransfetRequest transfetRequest){
        User currentUser = userService.getCurrentUser();
        Card fromCard = cardRepository.findByIdAndOwner(transfetRequest.getFromCardId(), currentUser).orElseThrow(()->new IllegalArgumentException("source not found"));
        Card toCard = cardRepository.findByIdAndOwner(transfetRequest.getToCardId(), currentUser).orElseThrow(()->new IllegalArgumentException("destination not found"));

        if(fromCard.getId().equals(toCard.getId())){
            throw new IllegalArgumentException("cannot transfer to same card");
        }

        if(fromCard.getStatus()!=CardStatus.ACTIVE){
            throw new IllegalArgumentException("source card not active");
        }
        if(toCard.getStatus()!=CardStatus.ACTIVE){
            throw new IllegalArgumentException("destination card not active");
        }
        if(fromCard.getBalance().compareTo(transfetRequest.getAmount())<0){
            throw new IllegalArgumentException("insufficient funds");
        }
        fromCard.setBalance(fromCard.getBalance().subtract(transfetRequest.getAmount()));
        toCard.setBalance(toCard.getBalance().add(transfetRequest.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

    }
    private boolean matchesSearch(CardResponse card, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }

        String normalizedSearch = search
                .replace(" ", "")
                .replace("*", "")
                .toLowerCase();

        String normalizedMaskedNumber = card.getMaskedNumber()
                .replace(" ", "")
                .replace("*", "")
                .toLowerCase();

        return normalizedMaskedNumber.contains(normalizedSearch);
    }
}
