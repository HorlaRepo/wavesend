package com.shizzy.moneytransfer.serviceimpl;

import com.shizzy.moneytransfer.api.ApiResponse;
import com.shizzy.moneytransfer.model.Wallet;
import com.shizzy.moneytransfer.repository.WalletRepository;
import com.shizzy.moneytransfer.service.CardService;
import com.shizzy.moneytransfer.dto.GenerateCardRequest;
import com.shizzy.moneytransfer.exception.DuplicateResourceException;
import com.shizzy.moneytransfer.exception.IllegalArgumentException;
import com.shizzy.moneytransfer.exception.ResourceNotFoundException;
import com.shizzy.moneytransfer.model.Card;
import com.shizzy.moneytransfer.repository.CardRepository;
import com.shizzy.moneytransfer.service.KeycloakService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletRepository walletRepository;
    private final KeycloakService keycloakService;

    Random random = new Random();

    @Override
    public ApiResponse<Card> generateCard(GenerateCardRequest cardRequest) {

        Wallet wallet = walletRepository.findById(cardRequest.getWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id " + cardRequest.getWalletId()));

        String userFirstName = keycloakService.existsUserByEmail(cardRequest.getUserEmail()).getData().getFirstName();
        String userLastName = keycloakService.existsUserByEmail(cardRequest.getUserEmail()).getData().getLastName();

        List<Card> cards = cardRepository.findCardByWalletId(cardRequest.getWalletId());

        for (Card card : cards) {
            if (card.getCardType().equalsIgnoreCase(cardRequest.getCardType())) {
                throw new DuplicateResourceException("User already has a " + cardRequest.getCardType() + " card");
            }
        }

        if (cards.size() > 2 ) {
            throw new IllegalArgumentException("You can only have a maximum of 2 cards");
        }

        Card card = Card.builder()
                .cardType(cardRequest.getCardType())
                .cardNumber(generateCardNumber(cardRequest.getCardType()))
                .expiryDate(generateExpiryDate())
                .cvv(generateCVV())
                .isLocked(true)
                .cardName(userFirstName + " " + userLastName)
                .wallet(wallet)
                .build();
        cardRepository.save(card);

        return ApiResponse.<Card>builder()
                .data(card)
                .message("Card generated successfully")
                .build();
    }

    @Override
    public ApiResponse<List<Card>> findCardByWalletId(Long walletId) {
        List<Card> cards =  cardRepository.findCardByWalletId(walletId);

        return ApiResponse.<List<Card>>builder()
                .data(cards)
                .message("Cards retrieved successfully")
                .build();
    }

    @Override
    public ApiResponse<String> lockCard(Integer cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found with id " + cardId));
        card.setLocked(true);
        cardRepository.save(card);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Card locked successfully")
                .build();
    }

    @Override
    public ApiResponse<String> unlockCard(Integer cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found with id " + cardId));
        card.setLocked(false);
        cardRepository.save(card);

        return ApiResponse.<String>builder()
                .success(true)
                .data("Card unlocked successfully")
                .message("Card unlocked successfully")
                .build();
    }


    @Transactional
    @Override
    public ApiResponse<String> setCardPin(Integer cardId, String pin){
        cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id " + cardId));

        String userPin = passwordEncoder.encode(pin);
        System.out.println(userPin);

        cardRepository.createPin(cardId, userPin);

        return ApiResponse.<String>builder()
                .success(true)
                .data("Card pin set successfully")
                .message("Card pin set successfully")
                .build();
    }

    @Override
    public boolean checkPin(Integer cardId, String enteredPin) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found with id " + cardId));
        if(passwordEncoder.matches(enteredPin, card.getPin())){
            return true;
        }else{
            throw new IllegalArgumentException("Invalid PIN");
        }
    }

    @Override
    public ApiResponse<String> deleteCard(Integer cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(()-> new ResourceNotFoundException("Card not found"));
        cardRepository.delete(card);

        return ApiResponse.<String>builder()
                .data("Card deleted successfully")
                .message("Card deleted successfully")
                .success(true)
                .build();
    }

    private  String generateCardNumber(String cardType) {
        String prefix = "";
        int length = 16;

        if ("Visa".equalsIgnoreCase(cardType)) {
            prefix = "4";
        } else if ("MasterCard".equalsIgnoreCase(cardType)) {
            int[] masterCardPrefixes = {51, 52, 53, 54, 55};
            prefix = String.valueOf(masterCardPrefixes[random.nextInt(masterCardPrefixes.length)]);
        } else {
            throw new IllegalArgumentException("Invalid card type. Only 'Visa' or 'MasterCard' are supported.");
        }

        StringBuilder cardNumber = new StringBuilder(prefix);

        while (cardNumber.length() < length - 1) {
            cardNumber.append(random.nextInt(10));
        }

        cardNumber.append(calculateLuhnCheckDigit(cardNumber.toString()));

        return cardNumber.toString();
    }

    private  String generateExpiryDate() {
        LocalDate expiryDate = LocalDate.now().plusYears(5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        return expiryDate.format(formatter);
    }

    private String generateCVV() {
        int cvv = random.nextInt(900) + 100;
        return String.valueOf(cvv);
    }

    private static int calculateLuhnCheckDigit(String cardNumberWithoutCheckDigit) {
        int sum = 0;
        boolean alternate = true;

        for (int i = cardNumberWithoutCheckDigit.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumberWithoutCheckDigit.substring(i, i + 1));

            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }

            sum += n;
            alternate = !alternate;
        }

        return (10 - (sum % 10)) % 10;
    }

}
