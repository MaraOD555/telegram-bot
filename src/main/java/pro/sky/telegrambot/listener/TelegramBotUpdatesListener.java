package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    private final NotificationTaskRepository notificationTaskRepository;
    private final Pattern pattern = Pattern.compile("([0-9.:\\s]{16})(\\s)([\\W+]+)"); // regular expression
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"); // dataFormat
    public TelegramBotUpdatesListener(NotificationTaskRepository notificationTaskRepository, TelegramBot telegramBot) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.telegramBot = telegramBot;
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
            Matcher matcher = pattern.matcher(inputText);//checking the input text
            if (inputText.startsWith("/start")) {
                SendMessage message = new SendMessage(chatId, "Hi! Our conversation is active");
                // toward which chat and which exact message to send (as a response of the input message)
               telegramBot.execute(message); // sending the message
            } else if (matcher.matches()){ // if matches, division the result into two groups
                String dateTime = matcher.group(1);// date
                String textReminder = matcher.group(3); // text
                LocalDateTime localDateTime = LocalDateTime.parse(dateTime, dateTimeFormatter); // creating localDateTime
                NotificationTask notificationTask = new NotificationTask(id, chatId, localDateTime, textReminder); // creating new item
                notificationTaskRepository.save(notificationTask); // saving a new item in DB via repository
                } else { // code in case incorrect input message
                SendMessage message = new SendMessage(chatId, "Data format is incorrect! Please, check.");
                telegramBot.execute(message);
            }
        });
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
    @Scheduled(cron = "0 0/1 * * * *") // set up a schedule (cron for every minute)
    public void notificationsToBeSend() {
        LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES); // limit for time - minutes
        Collection<NotificationTask> toBeSent = notificationTaskRepository.findAllByDateTime(dateTime); // collection of appropriate messages by dateTime
        toBeSent
                .forEach(notificationTask -> { // get chatId and textReminder for each member of list and send them according to the data
                    telegramBot.execute(new SendMessage(notificationTask.getChatId(), notificationTask.getTextReminder()));
                    logger.info("SendOut activated: <{}>, to chat: {}.", notificationTask.getTextReminder(), notificationTask.getChatId());
                });
    }

}


