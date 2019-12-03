package tech.inhostudios.vikingbot;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import tech.inhostudios.vikingbot.commands.Reminder.Reminder;
import tech.inhostudios.vikingbot.commands.Reminder.ReminderManager;
import tech.inhostudios.vikingbot.commands.Commands;

import javax.security.auth.login.LoginException;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class BotFrame extends ListenerAdapter {
    private List messageList;
    private JPanel panel;

    private JFrame botFrame;
    private JDA botJda;
    private ReminderManager reminderManager;

    // reminder shit
    private boolean waitingForDate = false;
    private User curRemUser = null;
    private Reminder curReminder = null;

    // reminder date management
    private String pastDay = "";

    private final int maxMessages = 75;

    public BotFrame(int width, int height, String title) throws LoginException {
        botFrame = new JFrame();
        botFrame.setSize(width, height);
        botFrame.setTitle(title);
        botFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        botFrame.setContentPane(panel);
        botFrame.setVisible(true);
        botFrame.setLocationRelativeTo(null);

        reminderManager = new ReminderManager();

        botJda = new JDABuilder(Token.token).build();
        botJda.addEventListener(this);
    }

    public void onMessageReceived(MessageReceivedEvent event){
        // handle command

        MessageChannel bulletinChannel = botJda.getTextChannelById("546066386099634186");

        // message reception
        MessageChannel msgCh = event.getChannel();
        Message msg = event.getMessage();
        User author = event.getAuthor();
        String content = msg.getContentRaw();


        // handle reminders
        if(!reminderManager.getRecent().getFormat().format(new Date()).equals(pastDay) &&
                !author.isBot() &&
                !content.startsWith(Commands.prefix) &&
                !waitingForDate) {
            // send daily reminders
            // clear message channel

            refreshMessages(bulletinChannel);
            pastDay = reminderManager.getRecent().getFormat().format(new Date());
        }

        // get date for the reminders
        if(waitingForDate && author.equals(curRemUser)){
            try {
                curReminder.parseDate(content);
                waitingForDate = false;
                curRemUser = null;
            } catch (ParseException e) {
                msgCh.sendMessage("```Date not set. Try again.```").complete();
                e.printStackTrace();
                return;
            }
            try {
                // reminder not created properly? doesn't exist at this point

                // testing reminder object
                // gonna create a random reminder object and add that instead

                System.out.println(curReminder.getJsonObject().toString());
                reminderManager.addReminder(curReminder);

                // if it's a new reminder for today, post it in the channel
                refreshMessages(bulletinChannel);
                // save reminder
                msgCh.sendMessage("```Reminder Saved```").complete();
            } catch (IOException e) {
                msgCh.sendMessage("```An error has occurred```").complete();
                e.printStackTrace();
            } catch (NullPointerException ne){
                msgCh.sendMessage("```WHY THE FUCK IS IT BREAKING.```").complete();
                ne.printStackTrace();
            }
        }

        //checking for prefix
        if(content.startsWith(Commands.prefix)){
            String command = content.substring(Commands.prefix.length());

            // ping
            if(command.startsWith(Commands.ping)){
                long ping = event.getJDA().getPing();
                msgCh.sendMessage("```Pong! " + ping + "ms```").complete();
            }

            // get all commands from help
            if(command.startsWith(Commands.help)){
                msgCh.sendMessage("```Available Commands (using >): \n" +
                        ">" + Commands.forget + "[Index of reminder] - Removes the reminder from the list\n" +
                        ">" + Commands.ping + " - Returns the current ping\n" +
                        ">" + Commands.reminder + " [Message] - Adds a reminder to a list of reminders\n" +
                        ">" + Commands.reminders + " - Returns all the current reminders for the channel\n" +
                        "```").complete();
            }

            // bind the reminder channel
            if(command.equalsIgnoreCase(Commands.refreshBulletin)){
                // clear message channel
                refreshMessages(bulletinChannel);
            }

            // add a reminder
            if(command.startsWith(Commands.reminder)){
                if(curRemUser == null){
                    String reminder = command.substring(Commands.reminder.length());
                    if(!reminder.replaceAll(" ", "").equals("")){
                        curReminder = new Reminder(msgCh.getName(),reminder,author.getName());
                    } else {
                        msgCh.sendMessage("```Please add a full message to add it to the reminders```").complete();
                    }
                    msgCh.sendMessage("```Add a date (DD MM YY)```").complete();
                    waitingForDate = true;
                    curRemUser = author;
                } else {
                    msgCh.sendMessage("`Please wait for the last user to create a reminder`").complete();
                }
            }

            // get all the reminders
            if(command.equalsIgnoreCase(Commands.reminders)){
                msgCh.sendMessage("```" + reminderManager.getRemindersAsString() + "```").complete();
            }

            // remove a reminder
            if(command.startsWith(Commands.forget)){
                String index = command.substring(Commands.forget.length());
                String result = reminderManager.removeFromReminders(index);
                msgCh.sendMessage("```" + result + "```").complete();
                refreshMessages(bulletinChannel);
            }
        }

        // printing messages
        String messages;
        if (event.isFromType(ChannelType.PRIVATE))
        {
            messages = "[PM] " + event.getAuthor().getName() + ": " + event.getMessage().getContentDisplay();
            System.out.println(messages);
            prune(messages);
        }
        else
        {
            messages = "[" + event.getGuild().getName() + "][" + event.getTextChannel().getName() + "] "+ event.getMember().getEffectiveName() + ": " + event.getMessage().getContentDisplay();
            System.out.println(messages);
            prune(messages);
        }
    }

    public void refreshMessages(MessageChannel bulletinChannel){
        // clear message channel
        boolean isWorking = true;

        while(isWorking) {
            java.util.List<Message> messages = bulletinChannel.getHistory().retrievePast(50).complete();

            if (messages.isEmpty()) {
                System.out.println("Done deleting: " + bulletinChannel);
                isWorking = false;
                return;
            }

            ((TextChannel) bulletinChannel).deleteMessages(messages).complete();
        }

        if(!isWorking){
            Date today = new Date();
            for(Reminder rem : reminderManager.getReminders()){
                if(rem.getFormat().format(today).equalsIgnoreCase(rem.getJsonDate())){
                    bulletinChannel.sendMessage("```" + rem.toString() + "```").complete();
                }
            }
        }
    }

    public void prune(String newMsg){
        ArrayList<String> temp = new ArrayList<String>();
        for(String message : messageList.getItems()){
            if(temp.size() < maxMessages) temp.add(message);
        }
        messageList.removeAll();
        messageList.add(newMsg);
        for(String message : temp){
            messageList.add(message);
        }
    }
}