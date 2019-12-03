package tech.inhostudios.vikingbot.commands.Reminder;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import tech.inhostudios.vikingbot.Utils.GsonUtils;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;

public class ReminderManager {

    private ArrayList<Reminder> reminders;

    private String filePath = System.getProperty("user.dir") + "\\saveres\\reminders.json";

    public ReminderManager(){
        try{
            reminders = initReminders();
        } catch(IOException e){
            reminders = new ArrayList<Reminder>();
            e.printStackTrace();
        } catch(NullPointerException ne){
            reminders = new ArrayList<Reminder>();
            ne.printStackTrace();
        }
    }

    public void refreshReminderJson(ArrayList<Reminder> refresh) throws IOException{
        JsonArray jsonArray = new JsonArray();

        for(Reminder curRem : refresh){
            jsonArray.add(curRem.getJsonObject());
        }

        File file = new File(filePath);
        FileWriter fw = new FileWriter(file);
        fw.write(jsonArray.toString());
        System.out.println("Successfully copied Json to file");
        System.out.println("Json Object: " + jsonArray);

        fw.close();

        reminders = initReminders();
    }

    public void addReminder(Reminder reminder) throws IOException{
        reminders.add(reminder);
        refreshReminderJson(reminders);
    }

    public void removeReminder(int index){
        reminders.remove(index);
    }

    public ArrayList<Reminder> getReminders() {
        return reminders;
    }

    public Reminder getRecent(){
        if(reminders.size() >= 1){
            return reminders.get(reminders.size() - 1);
        } else {
            return new Reminder();
        }
    }

    public ArrayList<Reminder> initReminders() throws IOException {
        Gson gson = new Gson();
        File file = new File(filePath);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        ArrayList<Reminder> rets = new ArrayList<>();

        JsonArray saves = gson.fromJson(br, JsonArray.class);
        if(saves != null){
            System.out.println("Read " + saves + " from " + filePath);
        }
        fr.close();

        JsonParser jsonParser = new JsonParser();
        if(saves != null){
            for(JsonElement jElement : saves){
                String thisObj = jElement.toString();
                JsonObject element = jsonParser.parse(thisObj).getAsJsonObject();

                String channel = element.getAsJsonPrimitive("channel").getAsString();
                String content = element.getAsJsonPrimitive("content").getAsString();
                String auth = element.getAsJsonPrimitive("user").getAsString();
                String date = element.getAsJsonPrimitive("date").getAsString();

                Reminder reminder = new Reminder(channel,content,auth);
                try {
                    reminder.parseDate(date);
                    System.out.println("Date read");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                rets.add(reminder);
            }
        }

        return rets;
    }

    public String getRemindersAsString(){
        String ret = "";
        String initial = ret;
        for(int i = 0; i < reminders.size(); i++){
            Reminder rem = reminders.get(i);

            ret = ret + (i+1) + " " + rem.toString() + "\n";
        }
        if(ret.equalsIgnoreCase(initial)){
            ret = "No reminders found";
        }
        return ret;
    }

    public String removeFromReminders(String index){
        int remIndex = Integer.parseInt(index) - 1;
        if(remIndex < reminders.size()){
            reminders.remove(remIndex);
            try {
                refreshReminderJson(reminders);
                return "Reminder removed";
            } catch (IOException e) {
                e.printStackTrace();
                return "Unable to refresh list";
            }
        } else {
            return "Index out of range";
        }
    }
}
