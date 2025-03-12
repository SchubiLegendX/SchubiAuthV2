package dev.schubilegend.GUI;

import dev.schubilegend.SchubiAuth;
import dev.schubilegend.Utils.APIUtils;
import dev.schubilegend.Utils.SessionChanger;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

public class SessionGUI extends GuiScreen {

    private GuiScreen previousScreen;
    private String status = "Session";
    private GuiTextField sessionField;
    private ScaledResolution sr;

    public SessionGUI(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        sr = new ScaledResolution(mc);

        sessionField = new GuiTextField(1, mc.fontRendererObj, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() / 2, 200, 20);
        sessionField.setMaxStringLength(32767);
        sessionField.setFocused(true);

        buttonList.add(new GuiButton(1400, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() / 2 + 25, 97, 20, "Login"));
        buttonList.add(new GuiButton(1500, sr.getScaledWidth() / 2 + 3, sr.getScaledHeight() / 2 + 25, 97, 20, "Restore"));
        buttonList.add(new GuiButton(1600, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() / 2 + 50, 200, 20, "Back"));

        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);

        super.onGuiClosed();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        mc.fontRendererObj.drawString(status, sr.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(status) / 2, sr.getScaledHeight() / 2 - 30, Color.WHITE.getRGB());
        sessionField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {

        if (button.id == 1400) {
            new Thread (() -> {
                try {
                    String token = sessionField.getText();
                    String[] playerInfo = APIUtils.getProfileInfo(token);
                    SessionChanger.setSession(new Session(playerInfo[0], playerInfo[1], token,"mojang"));
                    status = "§2Logged in as " + playerInfo[0];
                } catch (Exception e) {
                    status = "§4Invalid token";
                    e.printStackTrace();
                }
            }).start();
        }

        if (button.id == 1500) {
            SessionChanger.setSession(SchubiAuth.originalSession);
            status = "§2Restored session";

        }

        if (button.id == 1600) {
            mc.displayGuiScreen(previousScreen);
        }
        super.actionPerformed(button);
    }
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        sessionField.textboxKeyTyped(typedChar, keyCode);

        if (Keyboard.KEY_ESCAPE == keyCode) mc.displayGuiScreen(previousScreen);
        else super.keyTyped(typedChar, keyCode);
    }
}