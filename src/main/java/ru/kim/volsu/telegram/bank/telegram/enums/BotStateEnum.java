package ru.kim.volsu.telegram.bank.telegram.enums;

public enum BotStateEnum {
    // основные состояни бота
    MAIN_MENU("Главное меню"),
    ACCOUNT_DETAILS_MENU("Данные банковского счета"),
    TRANSFER_MONEY_MENU("Перевод денег"),

    // состояние для переводов
    TRANSFER_MONEY_ASK_PHONE_NUMBER("Запрос номера телефона для перевода средств"),

    // состояние для получения информации о счете
    ACOUNT_DETAILS_INFO("Вывести информацию о счете");

    private String text;

    BotStateEnum(String text) {
        this.text = text;
    }

    public static BotStateEnum getByValue(String value) throws IllegalAccessException {
        for (BotStateEnum state : BotStateEnum.values()) {
            if (state.getText().equals(value)) {
                return state;
            }
        }

        throw new IllegalAccessException("Enum not exists: " + value);
    }

    public String getText() {
        return text;
    }
}
