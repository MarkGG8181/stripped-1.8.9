package net.minecraft.controller;

import com.studiohartman.jamepad.ControllerButton;

public record ControllerButtonEvent(ControllerButton button, boolean pressed) {
}
