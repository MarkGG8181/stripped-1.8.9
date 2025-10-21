package net.minecraft.controller.bind;

import com.studiohartman.jamepad.ControllerAxis;
import com.studiohartman.jamepad.ControllerButton;
import net.minecraft.client.Minecraft;
import net.minecraft.controller.Controller;

public class ControllerInputBinding {
    private final String description;
    private final String category;

    private ControllerButton button;
    private ControllerAxis axis;

    private final ControllerButton defaultButton;
    private final ControllerAxis defaultAxis;

    private boolean isAxis;
    private boolean inverted;
    private boolean pressed;
    private float value;

    public ControllerInputBinding(String description, ControllerButton defaultButton, String category) {
        this.description = description;
        this.category = category;
        this.defaultButton = defaultButton;
        this.defaultAxis = null;
        this.button = defaultButton;
        this.isAxis = false;
    }

    public ControllerInputBinding(String description, ControllerAxis defaultAxis, String category) {
        this.description = description;
        this.category = category;
        this.defaultAxis = defaultAxis;
        this.defaultButton = null;
        this.axis = defaultAxis;
        this.isAxis = true;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public boolean isAxis() {
        return isAxis;
    }

    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public String getName() {
        if (getButton() != null)
            return getButton().name();

        return getAxis().name();
    }

    public ControllerButton getButton() {
        return button;
    }

    public ControllerAxis getAxis() {
        return axis;
    }

    public ControllerButton getDefaultButton() {
        return defaultButton;
    }

    public ControllerAxis getDefaultAxis() {
        return defaultAxis;
    }

    public void setButton(ControllerButton newButton) {
        this.button = newButton;
        this.isAxis = false;
    }

    public void setAxis(ControllerAxis newAxis) {
        this.axis = newAxis;
        this.isAxis = true;
    }

    public boolean isPressed() {
        return isAxis ? getValue() > Minecraft.getMinecraft().gameSettings.controllerDeadzone : pressed;
    }

    public float getValue() {
        float result = isAxis ? value : (pressed ? 1.0F : 0.0F);
        return inverted ? -result : result;
    }

    public void update() {
        if (!Controller.isConnected()) {
            pressed = false;
            value = 0f;
            return;
        }

        try {
            if (isAxis) {
                float raw = Controller.getAxis(axis);
                float deadzone = Minecraft.getMinecraft().gameSettings.controllerDeadzone;
                value = (Math.abs(raw) < deadzone) ? 0f : raw;
                pressed = Math.abs(value) > deadzone;
            } else {
                pressed = Controller.isButtonDown(button);
                value = pressed ? 1.0F : 0.0F;
            }
        } catch (Exception e) {
            pressed = false;
            value = 0f;
        }
    }

    public void reset() {
        pressed = false;
        value = 0f;
    }
}