package net.minecraft.controller.event;

import com.studiohartman.jamepad.ControllerAxis;

public record ControllerAxisEvent(ControllerAxis axis, float value) { }