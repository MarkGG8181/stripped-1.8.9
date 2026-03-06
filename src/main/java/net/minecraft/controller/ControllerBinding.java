package net.minecraft.controller;

import com.studiohartman.jamepad.ControllerButton;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.IntHashMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a rebindable controller button similar to Minecraft's KeyBinding.
 */
public class ControllerBinding {
    private static final List<ControllerBinding> controllerBindArray = new ArrayList<>();
    private static final Set<String> keybindSet = new HashSet<>();

    private final String description;
    private final String category;
    private ControllerButton button;
    private final ControllerButton defaultButton;
    private boolean pressed;

    public ControllerBinding(String description, ControllerButton defaultButton, String category) {
        this.description = description;
        this.button = defaultButton;
        this.defaultButton = defaultButton;
        this.category = category;
        this.pressed = false;

        controllerBindArray.add(this);
        keybindSet.add(category);
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public ControllerButton getButton() {
        return button;
    }

    public ControllerButton getDefaultButton() {
        return defaultButton;
    }

    public void setButton(ControllerButton newButton) {
        this.button = newButton;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public void update() {
        if (!Controller.isConnected()) {
            pressed = false;
            return;
        }
        try {
            pressed = Controller.isButtonDown(button);
        } catch (Exception e) {
            pressed = false;
        }
    }

    public static Set<String> getControllerBinds() {
        return keybindSet;
    }

    public static void resetAll(ControllerBinding[] bindings) {
        for (ControllerBinding b : bindings) {
            b.setPressed(false);
        }
    }
}