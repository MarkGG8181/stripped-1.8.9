package net.minecraft.controller;

import com.studiohartman.jamepad.ControllerAxis;

public record ControllerAxisEvent(ControllerAxis axis, float value) { }