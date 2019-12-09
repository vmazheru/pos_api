package pos.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import pos.model.PointOfSaleTerminal;
import pos.model.Pricing;

/**
 * In-memory implementation of {@code PointOfSaleService}. This implementation keeps all
 * POS terminal objects in a map, where keys are their IDs.
 * 
 * Every method will find the POS terminal by its ID, and then call a similar method on it.
 */
@Service
final class PointOfSaleServiceImpl implements PointOfSaleService {

    private final Map<String, PointOfSaleTerminal> terminals;
    
    public PointOfSaleServiceImpl() {
        terminals = new ConcurrentHashMap<>();
    }

    /**
     * Create a new POS terminal with the unique ID, and save it under this ID
     */
    @Override
    public String activate() {
        PointOfSaleTerminal terminal = new PointOfSaleTerminalImpl();
        terminals.put(terminal.getId(), terminal);
        return terminal.getId();
    }

    /**
     * Find a POS terminal by ID, and set pricing on it
     */
    @Override
    public void setPricing(String terminalId, Pricing... pricings) {
        withTerminal(terminalId, t -> {t.setPricing(pricings); return null;});
    }

    /**
     * Find a POS terminal by ID, and scan a product on it
     */
    @Override
    public void scan(String terminalId, String productCode) {
        withTerminal(terminalId, t -> {t.scan(productCode); return null;});
    }

    /**
     * Find a POS terminal by ID, calculate its total, and remove the terminal, so that 
     * all subsequent method calls on this terminal would result in exception
     */
    @Override
    public BigDecimal calculateTotal(String terminalId) {
        BigDecimal total = withTerminal(terminalId, t -> t.calculateTotal());
        terminals.remove(terminalId);
        return total;
    }
    
    private <T> T withTerminal(String terminalId, Function<PointOfSaleTerminal, T> f) {
        PointOfSaleTerminal terminal = terminals.get(terminalId);
        if (terminal == null) {
            throw new PointOfSaleServiceException("Unknown terminal id: " + terminalId);
        }
        return f.apply(terminal);
    }
    
}
