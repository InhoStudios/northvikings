package tech.inhostudios.vikingbot.commands.Reminder;

import tech.inhostudios.vikingbot.Utils.GsonUtils;

import java.util.ArrayList;

public class ReminderManager {

    private ArrayList<String> reminders;

    private String filePath = System.getProperty("user.dir") + "\\src\\main\\java\\resources\\reminders.json";

    public ReminderManager(){
        reminders = new ArrayList<String>();
    }

    public void addReminder(String reminder){
        reminders.add(reminder);
        GsonUtils.writeListToFile(filePath, reminders);
    }

    public void removeReminder(int index){
        reminders.remove(index);
    }

    public ArrayList getReminders() {
        return reminders;
    }
}
