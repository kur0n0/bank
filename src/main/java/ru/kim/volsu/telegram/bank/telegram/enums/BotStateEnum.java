package ru.kim.volsu.telegram.bank.telegram.enums;

public enum BotStateEnum {

    MAIN_MENU("Главное меню"),
    TRANSFER_MONEY("Перевод денег"),
    ACCOUNT_DETAILS("Данные банковского счета");

    private String text;

    BotStateEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
