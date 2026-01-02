package org.ozsoft.portfoliomanager.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ozsoft.portfoliomanager.domain.Configuration;
import org.ozsoft.portfoliomanager.domain.Stock;
import org.ozsoft.portfoliomanager.domain.StockLevel;
import org.ozsoft.portfoliomanager.dto.StockDTO;
import org.ozsoft.portfoliomanager.dto.StockPriceDTO;
import org.ozsoft.portfoliomanager.service.StockPriceService;
import org.ozsoft.portfoliomanager.service.StockService;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;
    private final Configuration config;

    @Autowired
    private StockPriceService stockPriceService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
        this.config = Configuration.getInstance();
    }

    @GetMapping
    public List<StockDTO> getAllStocks(
            @RequestParam(required = false) String level,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        List<Stock> stocks = new java.util.ArrayList<>(config.getStocks());
        
        if (level != null && !level.isEmpty()) {
            StockLevel stockLevel = StockLevel.valueOf(level.toUpperCase());
            stocks = stocks.stream()
                    .filter(stock -> stock.getLevel() == stockLevel)
                    .collect(Collectors.toList());
        }
        
        int start = Math.min(page * size, stocks.size());
        int end = Math.min(start + size, stocks.size());
        
        return stocks.subList(start, end).stream()
                .map(StockDTO::fromStock)
                .collect(Collectors.toList());
    }

    @GetMapping("/{symbol}")
    public StockDTO getStock(@PathVariable String symbol) {
        Stock stock = config.getStock(symbol);
        if (stock == null) {
            throw new IllegalArgumentException("Stock not found: " + symbol);
        }
        return StockDTO.fromStock(stock);
    }

    @PostMapping
    public StockDTO createStock(@RequestBody StockDTO stockDTO) {
        Stock stock = new Stock(stockDTO.getSymbol(), stockDTO.getName());
        
        if (stockDTO.getPrice() == null && stockPriceService != null) {
            try {
                StockPriceDTO priceData = stockPriceService.getIndianStock(stockDTO.getSymbol());
                if (priceData != null) {
                    stock.setPrice(priceData.getPrice());
                }
            } catch (Exception e) {
                org.apache.logging.log4j.LogManager.getLogger(StockController.class).warn("Could not fetch live price for stock", e);
            }
        } else if (stockDTO.getPrice() != null) {
            stock.setPrice(stockDTO.getPrice());
        }
        
        stockService.saveStock(stock);
        return StockDTO.fromStock(stock);
    }

    @GetMapping("/search")
    public StockPriceDTO searchStock(@RequestParam String symbol) {
        if (symbol == null || symbol.isEmpty()) {
            throw new IllegalArgumentException("Symbol is required");
        }
        
        try {
            StockPriceDTO result = stockPriceService.getIndianStock(symbol);
            if (result == null) {
                throw new IllegalArgumentException("Stock not found: " + symbol + ". Please try with NSE symbol (e.g., RELIANCE, TCS, INFY) or with exchange suffix (e.g., RELIANCE.NS)");
            }
            return result;
        } catch (Exception e) {
            org.apache.logging.log4j.LogManager.getLogger(StockController.class).error("Error searching stock: " + symbol, e);
            throw new IllegalArgumentException("Error searching for stock: " + symbol + ". " + e.getMessage());
        }
    }

    @PutMapping("/{symbol}")
    public StockDTO updateStock(@PathVariable String symbol, @RequestBody StockDTO stockDTO) {
        Stock stock = config.getStock(symbol);
        if (stock == null) {
            throw new IllegalArgumentException("Stock not found: " + symbol);
        }
        stockService.updateStock(stock, stockDTO);
        return StockDTO.fromStock(stock);
    }

    @DeleteMapping("/{symbol}")
    public void deleteStock(@PathVariable String symbol) {
        Stock stock = config.getStock(symbol);
        if (stock == null) {
            throw new IllegalArgumentException("Stock not found: " + symbol);
        }
        stockService.deleteStock(stock);
    }

    @GetMapping("/{symbol}/level")
    public String getStockLevel(@PathVariable String symbol) {
        Stock stock = config.getStock(symbol);
        if (stock == null) {
            throw new IllegalArgumentException("Stock not found: " + symbol);
        }
        return stock.getLevel().name();
    }

    @PutMapping("/{symbol}/level")
    public void setStockLevel(@PathVariable String symbol, @RequestParam String level) {
        Stock stock = config.getStock(symbol);
        if (stock == null) {
            throw new IllegalArgumentException("Stock not found: " + symbol);
        }
        stockService.setStockLevel(stock, StockLevel.valueOf(level.toUpperCase()));
    }
}
