package net.alus;

import com.filter.textcorrector.TextFilter;
import com.filter.textcorrector.spellchecking.Language;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Settings {
    private final static String filePath = "./settings.json";
    private final static Gson gson = new Gson();
    private static Settings instance;
    private String username;
    private static final TextFilter textFilter = new TextFilter(Language.ENGLISH);


    private Settings() {
        username = "Guest";
    }

    public static Settings getInstance() {
        if (instance==null)
            getOrCreateInstance();
        return instance;
    }

    private static void getOrCreateInstance() {
        try {
            instance = gson.fromJson(Files.readString(Path.of(filePath)), Settings.class);
        } catch (IOException e) {
            instance = new Settings();
        }
        save();
    }

    private static void save() {
        try(FileWriter fw = new FileWriter(filePath); JsonWriter writer = new JsonWriter(fw)){
            gson.toJson(instance, Settings.class, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public synchronized void setUsername(String username) {
        username = username.replaceAll("[^a-zA-Z0-9_-]", "");
        if(username.length() > 12) {
            username = username.substring(0, 12);
        } else if(username.isEmpty()) {
            username="Guest";
        }
        if(textFilter.isProfane(username))
            username="Guest";
        this.username=username;
        save();
    }
}
