package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    private final NotificationTaskRepository notificationTaskRepository;

    public TelegramBotUpdatesListener(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            Long chatId = update.message().chat().id(); // receiving the chat and its id
            String inputText = update.message().text(); // receiving an input message
            Long id = 0L;

            if (inputText.startsWith("/start")) {
                SendMessage message = new SendMessage(chatId, "Hi! Our conversation is active");
                // toward which chat and which exact message to send (as a response of the input message)
               telegramBot.execute(message); // sending the message
                /* try {
                    telegramBot.execute(message);   //Actually sending the message
                } catch (TelegramApiException e) {
                    e.printStackTrace();      //Any error will be printed here
                }*/
            } else {
                Pattern pattern = Pattern.compile("([0-9.:\\s]{16})(\\s)([\\W+]+)");// regular expression, searching
                Matcher matcher = pattern.matcher(inputText);//checking the input text
                if (matcher.matches()){ // if matches, division the result into two groups
                String dateTime = matcher.group(1);// date
                String textReminder = matcher.group(3); // text
                LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                NotificationTask notificationTask = new NotificationTask(id, chatId, localDateTime, textReminder);
                notificationTaskRepository.save(notificationTask);
                }
            }
        });
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}


