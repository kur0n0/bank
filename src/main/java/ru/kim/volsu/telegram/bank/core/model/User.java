package ru.kim.volsu.telegram.bank.core.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table (name = "users")
public class User {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column
    private String userName;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String chatId;

    @OneToOne
    @JoinColumn (name = "cardId")
    private Card card;

    @OneToMany (mappedBy = "to", fetch = FetchType.EAGER)
    private List<TransactionHistory> incometransactionHistory;

    @OneToMany (mappedBy = "from", fetch = FetchType.EAGER)
    private List<TransactionHistory> outcomeTransactionhistory;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public List<TransactionHistory> getIncometransactionHistory() {
        return incometransactionHistory;
    }

    public void setIncometransactionHistory(List<TransactionHistory> incometransactionHistory) {
        this.incometransactionHistory = incometransactionHistory;
    }

    public List<TransactionHistory> getOutcomeTransactionhistory() {
        return outcomeTransactionhistory;
    }

    public void setOutcomeTransactionhistory(List<TransactionHistory> outcomeTransactionhistory) {
        this.outcomeTransactionhistory = outcomeTransactionhistory;
    }
}
