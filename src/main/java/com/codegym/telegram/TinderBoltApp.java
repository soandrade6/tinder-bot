package com.codegym.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;

public class TinderBoltApp extends SimpleTelegramBot {

    public static final String TELEGRAM_BOT_TOKEN = ""; //TODO: añadir el token de Telegram entre comillas
    public static final String OPEN_AI_TOKEN = ""; //TODO: añadir el token de ChatGPT entre comillas

    private ChatGPTService chatGPt = new ChatGPTService(OPEN_AI_TOKEN);
    private DialogMode mode;
    private ArrayList<String> list = new ArrayList<String>();

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

        var myMessage = sendTextMessage("gpt is typing...");
        String answer = chatGPt.sendMessage(prompt, text);
        updateTextMessage(myMessage, answer);

    }

    public void dateCommand(){
        mode = DialogMode.DATE;
        String text = loadMessage("date");
        sendPhotoMessage("date");
        sendTextMessage(text);
        sendTextButtonsMessage(text,
                "date_grande", "Ariana Grande",
                "date_robbie", "Margot Robbie",
                "date_zendaya", "Zendaya",
                "date_gosling", "Ryan Gosling",
                "date_hardy", "Tom Hard");
    }

    public void dateButton(){
        String key = getButtonKey();
        sendPhotoMessage(key);
        sendHtmlMessage(key);
        String prompt = loadPrompt(key);
        chatGPt.setPrompt(prompt);
    }

    public void dateDialog(){
        String text = getMessageText();

        var myMessage = sendTextMessage("Typing...");
        String answer = chatGPt.addMessage(text);
        //sendTextMessage(answer);
        updateTextMessage(myMessage, answer);
    }

    public void messageCommand(){
        mode = DialogMode.MESSAGE;
        String text = loadMessage("message");
        sendPhotoMessage("message");
        sendTextButtonsMessage(text,
                "message_next", "Write next message.",
                "message_date", "Ask the person out on a date."
                );

        list.clear();
    }

    public void messageButton(){
        String key = getButtonKey();
        String prompt = loadPrompt(key);
        String history = String.join("\n\n", list);

        var myMessage = sendTextMessage("gpt is typing...");
        String answer = chatGPt.sendMessage(prompt, history);
        updateTextMessage(myMessage, answer);
    }

    public void messageDialog(){
        String text = getMessageText();
        list.add(text);

    }

    public void profileCommand(){
        mode = DialogMode.PROFILE;
        String text = loadMessage("profile");
        sendPhotoMessage("profile");
        sendTextMessage(text);

        sendTextMessage("Enter your name: ");
        user = new UserInfo();
        questionCount = 0;
    }

    private UserInfo user = new UserInfo();
    private int questionCount = 0;

    public void profileDialog(){
        String text = getMessageText();
        questionCount++;

        if (questionCount == 1){
            user.name = text;
            sendTextMessage("Enter your age: ");
        }else if(questionCount==2){
            user.age = text;
            sendTextMessage("Mention one hobby: ");
        }else if(questionCount==3){
            user.hobby = text;
            sendTextMessage("Wha are your objetives to interact with this person?: ");
        }else if(questionCount==4){
            user.goals = text;
            String prompt = loadPrompt("profile");
            String userInfo = user.toString();

            var myMessage = sendTextMessage("gpt is typing...");
            String answer = chatGPt.sendMessage(prompt, userInfo);
            updateTextMessage(myMessage, answer);

        }


    }

    public void openerCommand(){
        mode = DialogMode.OPENER;
        String text = loadMessage("opener");
        sendPhotoMessage("opener");
        sendTextMessage(text);

        sendTextMessage("Cual es su nombre");
        user = new UserInfo();
        questionCount = 0;
    }

    public void openerDialog(){
        String text = getMessageText();
        questionCount++;


        if (questionCount == 1){
            user.name = text;
            sendTextMessage("Su edad: ");
        }else if(questionCount==2){
            user.age = text;
            sendTextMessage("Su ocupación: ");
        }else if(questionCount==3){
            user.occupation = text;
            sendTextMessage("Qué tan guapo es del 1 al 10: ");
        }else if(questionCount==4){
            user.handsome = text;

            String prompt = loadPrompt("opener");
            String userInfo = user.toString();

            var myMessage = sendTextMessage("gpt is typing...");
            String answer = chatGPt.sendMessage(prompt, userInfo);
            updateTextMessage(myMessage, answer);

        }
    }

    public void hello(){

        if(mode == DialogMode.GPT){
            gptDialog();
        }else if(mode == DialogMode.DATE){
            dateDialog();
        }
        else if(mode == DialogMode.MESSAGE){
            messageDialog();
        }else if(mode == DialogMode.PROFILE){
            profileDialog();
        }else if(mode == DialogMode.OPENER){
            openerDialog();
        }
        else {
            String text = getMessageText();
            sendTextMessage("*Welcome to my tinder bot*");
            sendPhotoMessage("avatar_main");
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
        addCommandHandler("date", this::dateCommand);
        addCommandHandler("message", this::messageCommand);
        addCommandHandler("profile", this::profileCommand);
        addCommandHandler("opener", this::openerCommand);

        addMessageHandler(this::hello);
        //addButtonHandler("^.*", this::helloButton);
        addButtonHandler("^date_.*", this::dateButton);
        addButtonHandler("^message_.*", this::dateButton);
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
