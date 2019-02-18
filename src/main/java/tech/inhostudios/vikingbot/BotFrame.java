package tech.inhostudios.vikingbot;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import tech.inhostudios.vikingbot.commands.Reminder.Reminder;
import tech.inhostudios.vikingbot.commands.Reminder.ReminderManager;
import tech.inhostudios.vikingbot.commands.Commands;

import javax.security.auth.login.LoginException;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class BotFrame extends ListenerAdapter {
    private List messageList;
    private JPanel panel;

    private JFrame botFrame;
    private JDA botJda;
    private ReminderManager reminderManager;

    private final int maxMessages = 30;

    public BotFrame(int width, int height, String title) throws LoginException {
        botFrame = new JFrame();
        botFrame.setSize(width, height);
        botFrame.setTitle(title);
        botFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        botFrame.setContentPane(panel);
        botFrame.setVisible(true);
        botFrame.setLocationRelativeTo(null);

        botJda = new JDABuilder(Token.token).build();
        botJda.addEventListener(this);

        reminderManager = new ReminderManager();
    }

    public void onMessageReceived(MessageReceivedEvent event){
        // handle command

        // message reception
        MessageChannel msgCh = event.getChannel();
        Message msg = event.getMessage();
        User author = event.getAuthor();
        String content = msg.getContentRaw();

        // for measuring ping
        long time = System.currentTimeMillis();

        //checking for prefix
        if(content.startsWith(Commands.prefix)){
            String command = content.substring(Commands.prefix.length());
            if(command.startsWith(Commands.ping)){
                long ping = event.getJDA().getPing();
                msgCh.sendMessage("```Pong! " + ping + "ms```").complete();
            }
            if(command.startsWith(Commands.help)){
                msgCh.sendMessage("```Available Commands (using >): \n" +
                        ">" + Commands.forget + "[Index of reminder] - Removes the reminder from the list\n" +
                        ">" + Commands.ping + " - Returns the current ping\n" +
                        ">" + Commands.reminder + " [Message] - Adds a reminder to a list of reminders\n" +
                        ">" + Commands.reminders + " - Returns all the current reminders for the channel\n" +
                        "```").complete();
            }
            if(command.startsWith(Commands.reminder)){
                String reminder = command.substring(Commands.reminder.length());
                if(!reminder.replaceAll(" ", "").equals("")){
                    Reminder curReminder = new Reminder(msgCh.getName(),reminder,author.getName());
                    try {
                        reminderManager.addReminder(curReminder);
                        msgCh.sendMessage("```Message Saved```").complete();
                    } catch (IOException e) {
                        msgCh.sendMessage("```An error has occured```").complete();
                        e.printStackTrace();
                    }
                } else {
                    msgCh.sendMessage("```Please add a full message to add it to the reminders```").complete();
                }
            }
            if(command.equalsIgnoreCase(Commands.reminders)){
                msgCh.sendMessage("```" + reminderManager.getRemindersAsString() + "```").complete();
            }
            if(command.startsWith(Commands.forget)){
                String index = command.substring(Commands.forget.length());
                String result = reminderManager.removeFromReminders(index);
                msgCh.sendMessage("```" + result + "```").complete();
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
