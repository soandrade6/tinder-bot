package com.codegym.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TinderBoltApp extends SimpleTelegramBot {

    public static final String TELEGRAM_BOT_TOKEN = ""; //TODO: añadir el token de Telegram entre comillas
    public static final String OPEN_AI_TOKEN = ""; //TODO: añadir el token de ChatGPT entre comillas

    private ChatGPTService chatGPt = new ChatGPTService(OPEN_AI_TOKEN);
    private DialogMode mode;

    public TinderBoltApp() {
        super(TELEGRAM_BOT_TOKEN);
    }

    //TODO: escribiremos la funcionalidad principal del bot aquí
    public void startCommand(){
        mode = DialogMode.MAIN;
        String text = loadMessage("main");
        sendPhotoMessage("main");
        sendTextMessage(text);

        showMainMenu(
                "start", "menú principal del bot",
                "profile", "generación de perfil de TinderBoltApp \uD83D\uDE0E",
                "opener", "mensaje para iniciar conversación \uD83E\uDD70",
                "date", "correspondencia en su nombre \uD83D\uDE08",
                "start", "correspondencia con celebridades \uD83D\uDD25",
                "gpt", "hacer una pregunta a chat GPT \uD83E\uDDE0"
        );
    }

    public void gptCommand(){
        mode = DialogMode.GPT;

        String text = loadMessage("gpt");
        sendPhotoMessage("gpt");
        sendTextMessage(text);
    }

    public void gptDialog(){
        String text = getMessageText();
        String prompt = loadPrompt("gpt");
        String answer = chatGPt.sendMessage(prompt, text);
        sendTextMessage(answer);

    }

    public void hello(){

        if(mode == DialogMode.GPT){
            gptDialog();
        }else {
            String text = getMessageText();
            sendTextMessage("*Hello from Javaaaa*");
            sendTextMessage("_What's going on?_");
            sendTextMessage("You wrote: " + text);

            sendPhotoMessage("avatar_main");
            sendTextButtonsMessage("Launch process",
                    "start", "Start",
                    "stop", "Stop");
        }


    }

    public void helloButton(){
        String key = getButtonKey();
        if(key.equals("start")){
            sendTextMessage("The process has been started.");

        }else {
            sendTextMessage("Process gas been stopped.");
        }
    }


    @Override
    public void onInitialize() {
        //TODO: y un poco más aquí :)
        addCommandHandler("start", this::startCommand);
        addCommandHandler("gpt", this::gptCommand);
        addMessageHandler(this::hello);
        addButtonHandler("^.*", this::helloButton);
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
