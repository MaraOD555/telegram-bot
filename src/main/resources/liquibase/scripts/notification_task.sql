-- liquibase formatted sql

-- changeset tamaraD:1
CREATE TABLE notification_task (
    id BIGSERIAL PRIMARY KEY,
    chat_id BIGINT,
    date_time TIMESTAMP,
    text_reminder TEXT
);

