package tech.inhostudios.vikingbot.commands.Reminder;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Reminder {

    private String channelName;
    private String saveContent;
    private String user;

    private DateFormat dd_mm_yyFormat = new SimpleDateFormat("dd MM yy");
    private DateFormat retForm = new SimpleDateFormat("EEE. dd MMM yyyy");
    private Date date;
    private String dateStr;

    public Reminder(){
        channelName = "";
        saveContent = "";
        user = "";
        dateStr = "";
    }

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
            obj.addProperty("date", getJsonDate());
        } catch(JsonIOException e){
            e.printStackTrace();
        }
        return obj;
    }

    public void parseDate(String string) throws ParseException{
        date = dd_mm_yyFormat.parse(string);
        System.out.println("Saved date to reminder: " + getDate());
    }

    public String toString(){
        return "[" + getDate() + "] from " + user + ": " + saveContent;
    }

    public void setSavedDate(Date date){
        this.date = date;
    }

    public Date getSavedDate(){
        return date;
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
        return dd_mm_yyFormat;
    }

    public String getJsonDate(){ return dd_mm_yyFormat.format(date); }

    public String getDate() {
        return retForm.format(date);
    }

    public String getDateStr() {
        return dateStr;
    }
}
