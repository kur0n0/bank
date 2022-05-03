package ru.kim.volsu.telegram.bank.telegram.enums;

public enum TransferMoneyEnum {

    TRANSFER_MONEY("Перевод по номеру карты");

    private String text;

    TransferMoneyEnum(String text) {
        this.text = text;
    }

    public static TransferMoneyEnum getByValue(String value) throws IllegalAccessException {
        for(TransferMoneyEnum state : TransferMoneyEnum.values()) {
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
