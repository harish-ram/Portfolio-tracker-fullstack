package org.ozsoft.portfoliomanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ozsoft.portfoliomanager.dto.StockPriceDTO;
import org.ozsoft.portfoliomanager.service.StockPriceService;

@RestController
@RequestMapping("/api/search")
public class StockSearchController {

    @Autowired
    private StockPriceService stockPriceService;

    @GetMapping("/stock")
    public StockPriceDTO searchStock(@RequestParam String symbol) {
        if (symbol == null || symbol.isEmpty()) {
            throw new IllegalArgumentException("Symbol is required");
        }
        
        StockPriceDTO result = stockPriceService.getIndianStock(symbol);
        if (result == null) {
            throw new IllegalArgumentException("Stock not found: " + symbol);
        }
        
        return result;
    }

    @GetMapping("/stock/price")
    public StockPriceDTO getStockPrice(@RequestParam String symbol) {
        return searchStock(symbol);
    }
}
