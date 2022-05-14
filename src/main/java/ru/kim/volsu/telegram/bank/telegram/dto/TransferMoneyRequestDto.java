package ru.kim.volsu.telegram.bank.telegram.dto;

public class TransferMoneyRequestDto {
    private String fromUserName;
    private String toUsername;

    public TransferMoneyRequestDto(String fromUserName, String toUsername) {
        this.fromUserName = fromUserName;
        this.toUsername = toUsername;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getToUsername() {
        return toUsername;
    }

    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }
}
