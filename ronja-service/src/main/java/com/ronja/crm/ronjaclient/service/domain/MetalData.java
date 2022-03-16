package com.ronja.crm.ronjaclient.service.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;

public class MetalData {

    private static final BigDecimal OUNCE_PER_TONNE = new BigDecimal("0.0000311034768");
    private int id;
    private LocalDate fetched;
    private String currency;
    private BigDecimal aluminum;
    private BigDecimal copper;
    private BigDecimal lead;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFetched() {
        return fetched;
    }

    public void setFetched(LocalDate fetched) {
        this.fetched = fetched;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAluminum() {
        return computePrice(aluminum);
    }

    public void setAluminum(BigDecimal aluminum) {
        this.aluminum = aluminum;
    }

    public BigDecimal getCopper() {
        return computePrice(copper);
    }

    public void setCopper(BigDecimal copper) {
        this.copper = copper;
    }

    public BigDecimal getLead() {
        return computePrice(lead);
    }

    public void setLead(BigDecimal lead) {
        this.lead = lead;
    }

    public String getAluminiumPrice() {
        return getVolumedPrice(aluminum);
    }

    public String getCopperPrice() {
        return getVolumedPrice(copper);
    }

    public String getLeadPrice() {
        return getVolumedPrice(lead);
    }

    private String getVolumedPrice(BigDecimal input) {
        return "%.2f %s/t".formatted(computePrice(input).setScale(2, RoundingMode.HALF_EVEN), currency);
    }

    private BigDecimal computePrice(BigDecimal input) {
        return new BigDecimal(1).divide(input.multiply(OUNCE_PER_TONNE), MathContext.DECIMAL32);
    }
}
