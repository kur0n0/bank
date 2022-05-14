package ru.kim.volsu.telegram.bank.telegram.dto;

import java.math.BigDecimal;

public class TransferMoneyDto {
    private String fromUserName;
    private String toUserName;
    private BigDecimal amount;

    public TransferMoneyDto(String fromUserName, String toUserName, BigDecimal amount) {
        this.fromUserName = fromUserName;
        this.toUserName = toUserName;
        this.amount = amount;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
