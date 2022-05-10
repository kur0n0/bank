package ru.kim.volsu.telegram.bank.core.model;

import javax.persistence.*;

@Entity
public class Card {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer cardId;

    @Column
    private String cardNumber;

    @Column
    private String cvv;

    @Column
    private String expiredTime;

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(String expiredTime) {
        this.expiredTime = expiredTime;
    }
}
