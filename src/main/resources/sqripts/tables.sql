create table card
(
    cardId        serial primary key,
    cardNumber    varchar(80) not null,
    cvv           varchar(3)  not null,
    expiredTime   varchar(5)  not null,
    actualBalance numeric     not null
);

create table users
(
    userId    serial primary key,
    userName  varchar(80) not null,
    firstName varchar(80) not null,
    lastName  varchar(80),
    chatId    varchar(80) not null,
    cardId    int4 references card (cardId)
);

create table transactionhistory
(
    transactionsHistoryId serial primary key,
    fromCardId            int4 references card (cardId),
    toCardId              int4 references card (cardId),
    processDate           timestamp not null,
    amount                numeric   not null
);
