import app.Bot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        Runnable r = () -> {
            try {
                telegramBotsApi.registerBot(new Bot());
            } catch (TelegramApiRequestException e) {
                e.printStackTrace();
            }

        };

        new Thread(r).start();

        while (true) {
            try {
                Thread.sleep(80000L);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
    }
}
