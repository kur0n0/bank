package ru.kim.volsu.telegram.bank.telegram.enums;

public enum BotStateEnum {

    MAIN_MENU("Главное меню"),
    TRANSFER_MONEY("Перевод денег"),
    ACCOUNT_DETAILS("Данные банковского счета");

    private String text;

    BotStateEnum(String text) {
        this.text = text;
    }

    public static BotStateEnum getByValue(String value) throws IllegalAccessException {
        for(BotStateEnum state : BotStateEnum.values()) {
            if(state.getText().equals(value)) {
                return state;
            }
        }

        throw new IllegalAccessException("Enum not exists: " + value);
    }

    public String getText() {
        return text;
    }
}
