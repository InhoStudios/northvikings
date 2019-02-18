package tech.inhostudios.vikingbot.commands.Reminder;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import net.dv8tion.jda.core.entities.MessageChannel;

public class Reminder {

    private String channelName;
    private String saveContent;
    private String user;

    public Reminder(String channelName, String saveContent, String user){
        this.channelName = channelName;
        this.saveContent = saveContent;
        this.user = user;
    }

    public JsonObject getJsonObject(){
        JsonObject obj = new JsonObject();
        try{
            obj.addProperty("channel",channelName);
            obj.addProperty("content",saveContent);
            obj.addProperty("user",user);
        } catch(JsonIOException e){
            e.printStackTrace();
        }
        return obj;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getSaveContent() {
        return saveContent;
    }

    public String getUser() {
        return user;
    }
}
