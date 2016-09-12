package net.fararise.possessed.client.update;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.fararise.possessed.Possessed;
import net.fararise.possessed.client.gui.GameOverlayGUI;
import net.fararise.possessed.server.util.Version;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.InputStreamReader;
import java.net.URL;

@SideOnly(Side.CLIENT)
public class UpdateHandler {
    public static final String UPDATE_URL = "https://raw.githubusercontent.com/Fararise/Possessed/master/version.json";
    private static final JsonParser JSON_PARSER = new JsonParser();

    private static String updateAvailable;

    public static void checkUpdates() {
        new Thread(() -> {
            try {
                URL url = new URL(UpdateHandler.UPDATE_URL);
                JsonReader reader = new JsonReader(new InputStreamReader(url.openStream()));
                reader.setLenient(true);
                JsonObject root = UpdateHandler.JSON_PARSER.parse(reader).getAsJsonObject();
                Version current = Version.parse(root.get("current").getAsString());
                Version downloaded = Version.parse(Possessed.VERSION);
                if (current.isNewer(downloaded)) {
                    UpdateHandler.updateAvailable = current.toString();
                    GameOverlayGUI.displayUpdate();
                }
           } catch (Exception e) {
                System.err.println("An error occurred while checking for updates!");
                e.printStackTrace();
            }
        }).start();
    }

    public static String getUpdateAvailable() {
        return UpdateHandler.updateAvailable;
    }
}
