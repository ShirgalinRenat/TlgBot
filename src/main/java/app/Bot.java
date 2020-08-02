package app;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Bot extends TelegramLongPollingBot {
    private static final String botUserName = "NiceNote_Bot";
    private static final String token = "1315228782:AAGonhEou6GC5jAX5sjqNqKOxNc0lbXwqH8";
    private static InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
    private static InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
    private List<InlineKeyboardButton> usersNote = new ArrayList<>();
    private List<List<InlineKeyboardButton>> noteRowList = new ArrayList<>();
    private ArrayList<String> oldMessage = new ArrayList<>();
    private Message msg;

    /**
     * Метод для приема сообщений.
     *
     * @param update Содержит сообщение от пользователя.
     */
    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = new SendMessage();
        if (update.hasMessage()) {

            if (update.getMessage().getText().equals("/notes")) {
                message.setChatId(update.getMessage().getChatId()).setText("Ваши заметки:");
                inlineKeyboardMarkup.setKeyboard(noteRowList);
                message.setReplyMarkup(inlineKeyboardMarkup);
                try {
                    if (oldMessage.isEmpty()) {
                        message.setChatId(update.getMessage().getChatId()).setText("У вас нет заметок.");
                    }
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }

            if (update.getMessage().getText().equals("/help")) {
                SendMessage help = new SendMessage()
                        .setChatId(update.getMessage().getChatId())
                        .setText("Здравствуйте, для добавления заметки введите текст заметки и бот предложит вам добавить заметку. Для того чтобы просмотреть свои заметки воспользуйтесь командой /notes, а чтобы удалить заметку просто нажмите на нее во время просмотра заметок.");
                try {
                    execute(help);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

            if (update.getMessage().hasText() && !update.getMessage().isCommand()) {
                //для хранения сохранения заметок
                try {
                    msg = update.getMessage();
                    oldMessage.add(msg.getText());
                    execute(sendInlineKeyBoardMessage(update.getMessage().getChatId()));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

        } else if (update.hasCallbackQuery()) {

            /*Вывод уведомления, если такое потребуется*/
            // answerCallbackQuery(update.getCallbackQuery().getId(), "Заметка создана");
            if (update.getCallbackQuery().getData().equals("Заметка создана")) {
                //usersNote.add(new InlineKeyboardButton().setText(oldMessage.get(oldMessage.size()-1)).setCallbackData("Заметка удалена"));
                usersNote.add(new InlineKeyboardButton().setText(msg.getText()).setCallbackData("Заметка " + msg.getText() + " удалена"));
                noteRowList.clear();
                noteRowList.add(usersNote);
            }
            if (update.getCallbackQuery().getData().equals("Заметка не создана")) {
                inlineKeyboardMarkup.getKeyboard().clear();
            }

            Iterator itr = oldMessage.iterator();
            while (itr.hasNext()) {
                String x = (String) itr.next();
                if (update.getCallbackQuery().getData().equals("Заметка " + x + " удалена")) {
                    itr.remove();
                    usersNote.clear();
                    for (int i = 0; i < oldMessage.size(); i++) {
                        usersNote.add(new InlineKeyboardButton().setText(oldMessage.get(i)).setCallbackData("Заметка " + oldMessage.get(i) + " удалена"));
                    }
                    noteRowList.clear();
                    noteRowList.add(usersNote);
                }

            }

            try {
                execute(new SendMessage().setText(
                        update.getCallbackQuery().getData())
                        .setChatId(update.getCallbackQuery().getMessage().getChatId()));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Метод возвращает имя бота, указанное при регистрации.
     *
     * @return имя бота
     */
    @Override
    public String getBotUsername() {
        return botUserName;
    }

    /**
     * Метод возвращает token бота для связи с сервером Telegram
     *
     * @return token для бота
     */
    @Override
    public String getBotToken() {
        return token;
    }

    public synchronized void answerCallbackQuery(String callbackId, String message) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackId);
        answer.setText(message);
        answer.setShowAlert(true);
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static SendMessage sendInlineKeyBoardMessage(long chatId) {
        inlineKeyboardButton1.setText("Да").setCallbackData("Заметка создана");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Нет").setCallbackData("Заметка не создана"));
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return new SendMessage().setChatId(chatId).setText("Хотите добавить заметку?").setReplyMarkup(inlineKeyboardMarkup);
    }
}