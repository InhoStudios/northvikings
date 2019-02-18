package tech.inhostudios.vikingbot.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GsonUtils {

    private static Gson gson = new GsonBuilder().create();

    public static void writeListToFile(String filePath, ArrayList<String> array){

        try(FileWriter fw = new FileWriter(filePath)){
            gson.toJson(array, fw);
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
