package ru.kim.volsu.telegram.bank.telegram.enums;

public enum BotStateEnum {
    // основные состояни бота
    MAIN_MENU, // Главное меню
    MAIN_MENU_BALANCE, // Узнать баланс по счету
    TRANSFER_MONEY_MENU, // Разделе перевод денег

    MAIN_MENU_TRANSACTIONS_HISTORY, // История переводов
    MAIN_MENU_TRANSACTIONS_HISTORY_LAST, // Последние 5 переводов
    MAIN_MENU_TRANSACTIONS_HISTORY_ALL, // Все переводы

    // состояние для переводов
    TRANSFER_MONEY_ASK_USERNAME, // Запрос username для перевода средств
    TRANSFER_MONEY_REGISTRATE_CARD, // Добавление карты
    TRANSFER_MONEY_ASK_CARD_NUMBER, // Запрос номера карты
    TRANSFER_MONEY_ASK_EXPIRE_DATE, // Запрос даты действия карты
    TRANSFER_MONEY_ASK_CVV, // Запрос cvv кода карты
    TRANSFER_MONEY_TRANSACTION, // Перевод денег
    TRANSFER_MONEY_ASK_AMOUNT, // Запрос суммы денег для перевода
    TRANSFER_MONEY_CONFIRMATION, // Подтверждение платежа
}
