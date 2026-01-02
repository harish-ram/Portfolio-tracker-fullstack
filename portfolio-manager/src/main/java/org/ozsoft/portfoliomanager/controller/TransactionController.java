package org.ozsoft.portfoliomanager.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ozsoft.portfoliomanager.dto.TransactionDTO;
import org.ozsoft.portfoliomanager.dto.StockPriceDTO;
import org.ozsoft.portfoliomanager.entity.TransactionEntity;
import org.ozsoft.portfoliomanager.repository.UserRepository;
import org.ozsoft.portfoliomanager.service.TransactionService;
import org.ozsoft.portfoliomanager.service.StockPriceService;
import org.ozsoft.portfoliomanager.util.SecurityUtils;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    private StockPriceService stockPriceService;
    
    @Autowired
    private UserRepository userRepository;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<TransactionDTO> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Long userId = SecurityUtils.getCurrentUserId(userRepository);
        List<TransactionEntity> transactions = transactionService.getUserTransactions(userId);
        
        int start = Math.min(page * size, transactions.size());
        int end = Math.min(start + size, transactions.size());
        
        return transactions.subList(start, end).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TransactionDTO getTransaction(@PathVariable int id) {
        Long userId = SecurityUtils.getCurrentUserId(userRepository);
        TransactionEntity transaction = transactionService.getUserTransactionById(userId, id);
        return convertToDTO(transaction);
    }

    @PostMapping
    public TransactionDTO createTransaction(@RequestBody TransactionDTO transactionDTO) {
        Long userId = SecurityUtils.getCurrentUserId(userRepository);
        TransactionEntity entity = transactionService.createUserTransaction(userId, transactionDTO);
        return convertToDTO(entity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable int id) {
        Long userId = SecurityUtils.getCurrentUserId(userRepository);
        transactionService.deleteUserTransaction(userId, id);
        return ResponseEntity.ok().body(new MessageResponse("Transaction deleted successfully"));
    }

    @GetMapping("/price")
    public StockPriceDTO getStockPrice(@RequestParam String symbol) {
        if (symbol == null || symbol.isEmpty()) {
            throw new IllegalArgumentException("Symbol is required");
        }
        
        StockPriceDTO result = stockPriceService.getIndianStock(symbol);
        if (result == null) {
            throw new IllegalArgumentException("Stock not found: " + symbol);
        }
        
        return result;
    }

    private TransactionDTO convertToDTO(TransactionEntity entity) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(entity.getId());
        dto.setDate(entity.getDate());
        dto.setSymbol(entity.getSymbol());
        dto.setType(entity.getType());
        dto.setNoOfShares(entity.getNoOfShares());
        dto.setPrice(entity.getPrice());
        dto.setCost(entity.getCost());
        return dto;
    }

    public static class MessageResponse {
        public String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
