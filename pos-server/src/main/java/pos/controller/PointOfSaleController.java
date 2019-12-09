package pos.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pos.model.PointOfSaleTerminalException;
import pos.model.Pricing;
import pos.service.PointOfSaleService;
import pos.service.PointOfSaleServiceException;

/**
 * This controller implements {@link PointOfSaleService} by parsing HTTP requests, delegating
 * to the underlying service, and conveying responses back to the HTTP client.
 */
@RestController
@RequestMapping
public class PointOfSaleController implements PointOfSaleService {

    private static final String URN_TERMINAL = "/terminal";
    private static final String URN_TERMINAL_ACTIVATE = URN_TERMINAL + "/activate";
    private static final String URN_TERMINAL_ID = URN_TERMINAL + "/{terminalId}";
    private static final String URN_PRICING = URN_TERMINAL_ID + "/pricing";
    private static final String URN_SCAN = URN_TERMINAL_ID + "/scan";
    private static final String URN_TOTAL = URN_TERMINAL_ID + "/total";
    
    private final PointOfSaleService posService;
    
    @Autowired
    public PointOfSaleController(PointOfSaleService posService) {
        this.posService = posService;
    }
    
    @PostMapping(URN_TERMINAL_ACTIVATE)
    @Override
    public String activate() {
        return posService.activate();
    }

    @PutMapping(URN_PRICING)
    @Override
    public void setPricing(@PathVariable String terminalId, @RequestBody Pricing... pricings) {
        posService.setPricing(terminalId, pricings);
    }

    @PutMapping(URN_SCAN)
    @Override
    public void scan(@PathVariable String terminalId, @RequestBody String productCode) {
        // product code comes from JSON and it might have quotes around the product code
        posService.scan(terminalId, productCode.replaceAll("\"", ""));
    }

    @PostMapping(URN_TOTAL)
    @Override
    public BigDecimal calculateTotal(@PathVariable String terminalId) {
        return posService.calculateTotal(terminalId);
    }
    
    /**
     * Return HTTP status "Bad Request" when either terminal ID is incorrect,
     * or product code is not found in pricing
     */
    @ControllerAdvice(assignableTypes = {PointOfSaleController.class})
    static class ErrorHandler {
        @ExceptionHandler(PointOfSaleServiceException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        String badTerminalId(PointOfSaleServiceException e) {
            return e.getMessage();
        }
        
        @ExceptionHandler(PointOfSaleTerminalException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        String badTerminalState(PointOfSaleTerminalException e) {
            return e.getMessage();
        }
    }
}
