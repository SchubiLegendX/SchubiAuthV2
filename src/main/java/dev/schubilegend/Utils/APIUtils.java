package dev.schubilegend.Utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class APIUtils {
    public static String[] getProfileInfo(String token) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://api.minecraftservices.com/minecraft/profile");
        request.setHeader("Authorization", "Bearer " + token);
        CloseableHttpResponse response = client.execute(request);
        String jsonString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
        String IGN = jsonObject.get("name").getAsString();
        String UUID = jsonObject.get("id").getAsString();
        return new String[]{IGN, UUID};
    }
    public static Boolean validateSession(String token) throws IOException {
        try {
            String[] profileInfo = getProfileInfo(token);
            String IGN = profileInfo[0];
            String UUID = profileInfo[1];
            return IGN.equals(Minecraft.getMinecraft().getSession().getUsername()) && UUID.equals(Minecraft.getMinecraft().getSession().getPlayerID());
        } catch (Exception e) {
            return false;
        }
    }
    public static Boolean checkOnline(String UUID)
    {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet requests = new HttpGet("https://api.slothpixel.me/api/players/" + UUID);
            CloseableHttpResponse response = client.execute(requests);
            String jsonString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
            return jsonObject.get("online").getAsBoolean();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static int changeName(String newName,String token) throws IOException{
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPut request = new HttpPut("https://api.minecraftservices.com/minecraft/profile/name/" + newName);
        request.setHeader("Authorization", "Bearer " + token);
        CloseableHttpResponse response = client.execute(request);
        return response.getStatusLine().getStatusCode();
    }
    public static int changeSkin(String url,String token) throws IOException{
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost request = new HttpPost("https://api.minecraftservices.com/minecraft/profile/skins");
        request.setHeader("Authorization", "Bearer " + token);
        request.setHeader("Content-Type", "application/json");
        String jsonString = String.format("{ \"variant\": \"classic\", \"url\": \"%s\"}", url);
        request.setEntity(new StringEntity(jsonString));
        CloseableHttpResponse response = client.execute(request);
        return response.getStatusLine().getStatusCode();
    }

}
