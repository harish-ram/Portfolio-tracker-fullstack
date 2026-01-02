package org.ozsoft.portfoliomanager.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ozsoft.portfoliomanager.domain.Configuration;
import org.ozsoft.portfoliomanager.domain.Portfolio;
import org.ozsoft.portfoliomanager.domain.Position;
import org.ozsoft.portfoliomanager.dto.PortfolioDTO;
import org.ozsoft.portfoliomanager.dto.PositionDTO;
import org.ozsoft.portfoliomanager.entity.PortfolioEntity;
import org.ozsoft.portfoliomanager.repository.UserRepository;
import org.ozsoft.portfoliomanager.service.PortfolioService;
import org.ozsoft.portfoliomanager.util.SecurityUtils;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final Configuration config;

    @Autowired
    private PortfolioService portfolioService;
    
    @Autowired
    private UserRepository userRepository;

    public PortfolioController() {
        this.config = Configuration.getInstance();
    }

    @GetMapping
    public PortfolioDTO getPortfolio() {
        Portfolio portfolio = config.getPortfolio();
        if (portfolio == null) {
            return new PortfolioDTO();
        }
        return PortfolioDTO.fromPortfolio(portfolio);
    }

    @GetMapping("/metadata")
    public ResponseEntity<?> getPortfolioMetadata() {
        Long userId = SecurityUtils.getCurrentUserId(userRepository);
        PortfolioEntity portfolio = portfolioService.getOrCreateUserPortfolio(userId);
        return ResponseEntity.ok(portfolio);
    }

    @GetMapping("/positions")
    public List<PositionDTO> getPositions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Portfolio portfolio = config.getPortfolio();
        if (portfolio == null || portfolio.getPositions() == null) {
            return new java.util.ArrayList<>();
        }
        List<Position> positions = new java.util.ArrayList<>(portfolio.getPositions());
        
        int start = Math.min(page * size, positions.size());
        int end = Math.min(start + size, positions.size());
        
        return positions.subList(start, end).stream()
                .map(PositionDTO::fromPosition)
                .collect(Collectors.toList());
    }

    @GetMapping("/positions/{symbol}")
    public PositionDTO getPosition(@PathVariable String symbol) {
        Portfolio portfolio = config.getPortfolio();
        if (portfolio == null || portfolio.getPositions() == null) {
            throw new IllegalArgumentException("Portfolio not found");
        }
        for (Position position : portfolio.getPositions()) {
            if (position != null && position.getStock() != null && position.getStock().getSymbol().equals(symbol)) {
                return PositionDTO.fromPosition(position);
            }
        }
        throw new IllegalArgumentException("Position not found for symbol: " + symbol);
    }

    @PutMapping("/metadata/name")
    public ResponseEntity<?> updatePortfolioName(@RequestBody PortfolioNameRequest request) {
        Long userId = SecurityUtils.getCurrentUserId(userRepository);
        PortfolioEntity portfolio = portfolioService.updatePortfolioName(userId, request.getName());
        return ResponseEntity.ok(portfolio);
    }

    @PutMapping("/metadata/description")
    public ResponseEntity<?> updatePortfolioDescription(@RequestBody PortfolioDescriptionRequest request) {
        Long userId = SecurityUtils.getCurrentUserId(userRepository);
        PortfolioEntity portfolio = portfolioService.updatePortfolioDescription(userId, request.getDescription());
        return ResponseEntity.ok(portfolio);
    }

    public static class PortfolioNameRequest {
        public String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class PortfolioDescriptionRequest {
        public String description;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
