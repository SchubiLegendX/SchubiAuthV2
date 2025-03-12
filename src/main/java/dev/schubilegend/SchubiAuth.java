package dev.schubilegend;

import dev.schubilegend.GUI.ChangerGUI;
import dev.schubilegend.GUI.SessionGUI;
import dev.schubilegend.Utils.APIUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.util.Session;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.IOException;
import java.text.ParseException;

@Mod(modid = SchubiAuth.MODID, version = SchubiAuth.VERSION)
public class SchubiAuth {

    public static final String MODID = "SchubiAuth";
    public static final String VERSION = "2.0";
    public static Minecraft mc = Minecraft.getMinecraft();
    public static Session originalSession = mc.getSession();
    public static String onlineStatus = "§4╳ Offline";
    public static String isSessionValid = "§2✔ Valid";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        Display.setTitle("SchubiAuth " + VERSION);
    }

    @SubscribeEvent
    public void onInitGuiPost(GuiScreenEvent.InitGuiEvent.Post e) throws IOException, ParseException {
        if (e.gui instanceof GuiMultiplayer) {
            e.buttonList.add(new GuiButton(2100, e.gui.width - 90, 5, 80, 20, "Login"));
            e.buttonList.add(new GuiButton(2200,  e.gui.width - 180, 5, 80, 20, "Changer"));
            new Thread(() -> {
                try {
                    isSessionValid = APIUtils.validateSession(mc.getSession().getToken()) ? "§2✔ Valid" : "§4╳ Invalid";
                    onlineStatus = APIUtils.checkOnline(mc.getSession().getUsername()) ? "§2✔ Online" : "§4╳ Offline";
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        }
    }
    @SubscribeEvent
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post e) throws IOException, ParseException {
        if (e.gui instanceof GuiMultiplayer) {
            Minecraft.getMinecraft().fontRendererObj.drawString("§fUser: " +  mc.getSession().getUsername() + "  §f|  " + onlineStatus + "  §f|  " + isSessionValid, 5, 10, Color.RED.getRGB());
        }
    }
    @SubscribeEvent
    public void onActionPerformedPre(GuiScreenEvent.ActionPerformedEvent.Pre e) {
        if (e.gui instanceof GuiMultiplayer) {
            if (e.button.id == 2100) {
                Minecraft.getMinecraft().displayGuiScreen(new SessionGUI(e.gui));
            }
            if (e.button.id == 2200) {
                Minecraft.getMinecraft().displayGuiScreen(new ChangerGUI(e.gui));
            }


        }
    }


}
