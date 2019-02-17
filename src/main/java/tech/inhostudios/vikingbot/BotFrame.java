package tech.inhostudios.vikingbot;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import tech.inhostudios.vikingbot.commands.Commands;

import javax.security.auth.login.LoginException;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class BotFrame extends ListenerAdapter {
    private List messageList;
    private JPanel panel;

    private JFrame botFrame;
    private JDA botJda;

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
                msgCh.sendMessage("```Pong! " + ping + "ms```").queue();
            }
            if(command.startsWith(Commands.help)){
                msgCh.sendMessage("```Available Commands (using >): \n" +
                        ">ping - Returns the current ping\n" +
                        ">reminder [Message] - Adds a reminder to a list of reminders" +
                        "```").queue();
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
