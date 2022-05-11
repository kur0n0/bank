package ru.kim.volsu.telegram.bank.core.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table (name = "transactionhistory")
public class TransactionHistory {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer transactionsHistoryId;

    @ManyToOne
    @JoinColumn (name = "fromCardId")
    private Card from;

    @ManyToOne
    @JoinColumn (name = "toCardId")
    private Card to;

    @Column
    private LocalDateTime processDate;

    @Column
    private BigDecimal amount;

    public Integer getTransactionsHistoryId() {
        return transactionsHistoryId;
    }

    public void setTransactionsHistoryId(Integer transactionsHistoryId) {
        this.transactionsHistoryId = transactionsHistoryId;
    }

    public Card getTo() {
        return to;
    }

    public void setTo(Card to) {
        this.to = to;
    }

    public Card getFrom() {
        return from;
    }

    public void setFrom(Card from) {
        this.from = from;
    }

    public LocalDateTime getProcessDate() {
        return processDate;
    }

    public void setProcessDate(LocalDateTime processDate) {
        this.processDate = processDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
