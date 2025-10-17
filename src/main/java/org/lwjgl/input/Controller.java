package org.lwjgl.input;

import com.studiohartman.jamepad.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Lightweight static wrapper for Jamepad controller input.
 * Emulates LWJGL-style polling and events for compatibility with Minecraft's input system.
 */
public class Controller {
    private static final Logger logger = LogManager.getLogger("Jamepad");

    private static final ControllerManager controllers = new ControllerManager();
    private static ControllerIndex controller;

    public static final float DEADZONE = 0.2f;

    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        controllers.initSDLGamepad();
        controller = controllers.getControllerIndex(0);
        initialized = true;
    }

    public static void update() {
        if (!initialized) return;
        controllers.update();
    }

    public static void destroy() {
        if (!initialized) return;
        controllers.quitSDLGamepad();
    }

    public static boolean isConnected() {
        return controller != null && controller.isConnected();
    }

    public static boolean isButtonDown(ControllerButton button) {
        try {
            return controller.isButtonPressed(button);
        } catch (ControllerUnpluggedException e) {
            return false;
        }
    }

    public static float getAxis(ControllerAxis axis) {
        try {
            return controller.getAxisState(axis);
        } catch (ControllerUnpluggedException e) {
            return 0f;
        }
    }

    public static void shutdown() {
        controllers.quitSDLGamepad();
    }
}
