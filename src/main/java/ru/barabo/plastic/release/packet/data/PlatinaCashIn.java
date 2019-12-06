package ru.barabo.plastic.release.packet.data;

import java.util.List;

public class PlatinaCashIn {
    private String label;

    private Double amount;

    private String accountCodeCashBox;

    private String departmentLevel1;

    private String descriptionDefault;

    private String accountCode70601;

    private String customer;

    static public PlatinaCashIn createFromList(List<Object> data, String customer) {
        if(data == null || data.isEmpty()) return null;

        PlatinaCashIn result = new PlatinaCashIn();

        result.label = (String)data.get(0);

        result.customer = customer;

        result.amount = data.size() > 1  && data.get(1) != null ? ((Number)data.get(1)).doubleValue() : null;

        result.accountCodeCashBox = data.size() > 2  ? (String)data.get(2) : null;

        result.departmentLevel1 = data.size() > 3  ? (String)data.get(3) : null;

        result.descriptionDefault = data.size() > 4  ? (String)data.get(4) : null;

        result.accountCode70601 = data.size() > 5  ? (String)data.get(5) : null;

        return result;
    }

    public String getLabel() {
        return label;
    }

    public Double getAmount() {
        return amount;
    }

    public String getAccountCodeCashBox() {
        return accountCodeCashBox;
    }

    public String getDepartmentLevel1() {
        return departmentLevel1;
    }

    public String getDescriptionDefault() {
        return descriptionDefault;
    }

    public String getAccountCode70601() {
        return accountCode70601;
    }

    public String getCustomer() {
        return customer;
    }

    public void setDescriptionDefault(String descriptionDefault) {
        this.descriptionDefault = descriptionDefault;
    }
}

