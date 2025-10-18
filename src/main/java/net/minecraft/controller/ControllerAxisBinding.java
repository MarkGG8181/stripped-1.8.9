package net.minecraft.controller;

import com.studiohartman.jamepad.ControllerAxis;

/**
 * Represents a rebindable controller axis for analog input.
 * Can be used for movement, camera, or trigger control.
 */
public class ControllerAxisBinding {

    private final String description;
    private final String category;
    private ControllerAxis axis;
    private final ControllerAxis defaultAxis;
    private float value;
    private boolean inverted;
    private final float deadzone = Controller.DEADZONE;

    public ControllerAxisBinding(String description, ControllerAxis defaultAxis, String category) {
        this.description = description;
        this.axis = defaultAxis;
        this.defaultAxis = defaultAxis;
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public ControllerAxis getAxis() {
        return axis;
    }

    public ControllerAxis getDefaultAxis() {
        return defaultAxis;
    }

    public void setAxis(ControllerAxis axis) {
        this.axis = axis;
    }

    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public float getValue() {
        return inverted ? -value : value;
    }

    public boolean isDown() {
        return getValue() > deadzone;
    }

    public static void resetAll(ControllerAxisBinding[] bindings) {
        for (ControllerAxisBinding b : bindings) {
            b.value = 0f;
        }
    }

    public void update() {
        if (!Controller.isConnected()) {
            value = 0f;
            return;
        }
        try {
            float raw = Controller.getAxis(axis);
            value = (Math.abs(raw) < deadzone) ? 0f : raw;
        } catch (Exception e) {
            value = 0f;
        }
    }
}