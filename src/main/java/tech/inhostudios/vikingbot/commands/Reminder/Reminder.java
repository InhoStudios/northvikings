package tech.inhostudios.vikingbot.commands.Reminder;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Reminder {

    private String channelName;
    private String saveContent;
    private String user;

    private DateFormat format = new SimpleDateFormat("dd MM yy");
    private DateFormat retForm = new SimpleDateFormat("EEE, d MMM yyyy");
    private Date date;
    private String dateStr;

    public Reminder(String channelName, String saveContent, String user){
        this.channelName = channelName;
        this.saveContent = saveContent;
        this.user = user;
        date = new Date();
        dateStr = "No Date Specified";
    }

    public JsonObject getJsonObject(){
        JsonObject obj = new JsonObject();
        try{
            obj.addProperty("channel",channelName);
            obj.addProperty("content",saveContent);
            obj.addProperty("user",user);
            obj.addProperty("date",format.format(date));
        } catch(JsonIOException e){
            e.printStackTrace();
        }
        return obj;
    }

    public void parseDate(String string) throws ParseException{
        date = format.parse(string);
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

    public DateFormat getFormat() {
        return format;
    }

    public String getDate() {
        return retForm.format(date);
    }

    public String getDateStr() {
        return dateStr;
    }
}
