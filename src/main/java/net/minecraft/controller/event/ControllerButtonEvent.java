package net.minecraft.controller.event;

import com.studiohartman.jamepad.ControllerButton;

public record ControllerButtonEvent(ControllerButton button, boolean pressed) {
}
