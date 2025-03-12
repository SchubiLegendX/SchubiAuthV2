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
import scala.Int;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ChangerGUI extends GuiScreen {

    private final GuiScreen previousScreen;
    private String status = "";
    private GuiTextField nameField;
    private GuiTextField skinField;
    private ScaledResolution sr;
    private ArrayList<GuiTextField> textFields = new ArrayList<>();

    public ChangerGUI(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        sr = new ScaledResolution(mc);
        nameField = new GuiTextField(1, mc.fontRendererObj, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() / 2, 97, 20);
        nameField.setMaxStringLength(16);
        nameField.setFocused(true);
        skinField = new GuiTextField(2, mc.fontRendererObj, sr.getScaledWidth() / 2 + 3, sr.getScaledHeight() / 2, 97, 20);
        skinField.setMaxStringLength(32767);
        textFields.add(nameField);
        textFields.add(skinField);
        buttonList.add(new GuiButton(3100, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() / 2 + 25, 97, 20, "Change Name"));
        buttonList.add(new GuiButton(3200, sr.getScaledWidth() / 2 + 3, sr.getScaledHeight() / 2 + 25, 97, 20, "Change Skin"));
        buttonList.add(new GuiButton(3300, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() / 2 + 50, 200, 20, "Back"));
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
        mc.fontRendererObj.drawString(status, sr.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(status) / 2, sr.getScaledHeight() / 2 - 40, Color.WHITE.getRGB());
        mc.fontRendererObj.drawString("Name:", sr.getScaledWidth() / 2 - 99, sr.getScaledHeight() / 2 - 15, Color.WHITE.getRGB());
        mc.fontRendererObj.drawString("Skin:", sr.getScaledWidth() / 2 + 4, sr.getScaledHeight() / 2 - 15, Color.WHITE.getRGB());
        nameField.drawTextBox();
        skinField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 3100) {
            String newName = nameField.getText();
            if (Objects.equals(SchubiAuth.originalSession.getToken(), mc.getSession().getToken())) {
                status = "§4Prevented you from changing the name of your main account!";
            } else {
                new Thread(() -> {
                    try {
                        int statusCode = APIUtils.changeName(newName, mc.getSession().getToken());
                        if (statusCode == 200) {
                            status = "§2Successfully changed name!";
                            SessionChanger.setSession(new Session(newName, mc.getSession().getPlayerID(), mc.getSession().getToken(), "mojang"));
                        } else if (statusCode == 429) {
                            status = "§4Error: Too many requests!";
                        } else if (statusCode == 400) {
                            status = "§4Error: Invalid name!";
                        } else if (statusCode == 401) {
                            status = "§4Error: Invalid token!";
                        } else if (statusCode == 403) {
                            status = "§4Error: Name is unavailable/Player already changed name in the last 35 days";
                        } else {
                            status = "§4An unknown error occurred!";
                        }
                    }
                    catch (Exception e) {
                        status = "§4An unknown error occurred!";
                        e.printStackTrace();
                    }
                }).start();
            }
        }
        if (button.id == 3200){
            String newSkin = skinField.getText();
            new Thread(() -> {
                try {
                    int statusCode = APIUtils.changeSkin(newSkin, mc.getSession().getToken());
                    if (statusCode == 200) {
                        status = "§2Successfully changed skin!";
                    } else if (statusCode == 429) {
                        status = "§4Error: Too many requests!";
                    } else if (statusCode == 401) {
                        status = "§4Error: Invalid token!";
                    } else {
                        status = "§4Error: Invalid Skin";
                    }
                }
                catch (Exception e) {
                    status = "§4An unknown error occurred!";
                    e.printStackTrace();
                }
            }).start();
        }
        if (button.id == 3300) {
            mc.displayGuiScreen(previousScreen);
        }
        super.actionPerformed(button);
    }
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        nameField.textboxKeyTyped(typedChar, keyCode);
        skinField.textboxKeyTyped(typedChar, keyCode);

        if (Keyboard.KEY_ESCAPE == keyCode) mc.displayGuiScreen(previousScreen);
        else super.keyTyped(typedChar, keyCode);
    }
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        boolean prevFocused = false;
        boolean postFocused = false;
        for (GuiTextField text : textFields) {
            prevFocused = text.isFocused() || prevFocused;
            text.mouseClicked(mouseX, mouseY, mouseButton);
            postFocused = text.isFocused() || postFocused;
        }
    }
}