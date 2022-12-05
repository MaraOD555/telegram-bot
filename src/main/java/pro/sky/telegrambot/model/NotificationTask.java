package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class NotificationTask { // entity class creation
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private LocalDateTime dateTime;
    private String textReminder;

    public NotificationTask(Long id, Long chatId, LocalDateTime dateTime, String textReminder) {
        this.id = id;
        this.chatId = chatId;
        this.dateTime = dateTime;
        this.textReminder = textReminder;
    }

    public NotificationTask() {
    }

    public long getChatId() {
        return chatId;
    }
    public void setChatId(long chatId){ this.chatId = chatId;}
    public LocalDateTime getDateTime(){return dateTime;}

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    public String getTextReminder(){ return textReminder;}
    public void setTextReminder(String textReminder){this.textReminder = textReminder;}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}


