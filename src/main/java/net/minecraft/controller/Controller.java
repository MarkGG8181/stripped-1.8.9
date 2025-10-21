package net.minecraft.controller;

import com.studiohartman.jamepad.*;
import net.minecraft.client.Minecraft;
import net.minecraft.controller.bind.ControllerInputBinding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Controller {
    private static final Logger logger = LogManager.getLogger("Jamepad");

    private static final ControllerManager controllers = new ControllerManager();
    private static ControllerIndex controller;

    private static boolean initialized = false;

    private static final Map<ControllerButton, Boolean> lastButtonStates = new EnumMap<>(ControllerButton.class);
    private static final Queue<ControllerButtonEvent> eventQueue = new ArrayDeque<>();
    private static ControllerButtonEvent currentEvent = null;

    private static final Map<ControllerAxis, Float> lastAxisStates = new EnumMap<>(ControllerAxis.class);
    private static final Queue<ControllerAxisEvent> axisEventQueue = new ArrayDeque<>();
    private static ControllerAxisEvent currentAxisEvent = null;

    private static ControllerInputBinding[] bindings = new ControllerInputBinding[0];

    private static long lastReconnectAttempt = 0;
    private static final long RECONNECT_INTERVAL_MS = 1000;

    public static void init() {
        if (initialized) return;

        try {
            controllers.initSDLGamepad();
            initialized = true;
            logger.info("Jamepad initialized (waiting for controller connection)");
        } catch (Exception e) {
            logger.error("Failed to init Jamepad", e);
            initialized = false;
        }
    }

    public static void poll() {
        if (!initialized) return;

        controllers.update();

        if (controller == null || !controller.isConnected()) {
            long now = System.currentTimeMillis();
            if (now - lastReconnectAttempt > RECONNECT_INTERVAL_MS) {
                try {
                    tryReconnect();
                } catch (ControllerUnpluggedException e) {
                    logger.warn(e);
                }
                lastReconnectAttempt = now;
            }
            return;
        }

        for (ControllerButton button : ControllerButton.values()) {
            boolean wasDown = lastButtonStates.getOrDefault(button, false);
            boolean isDown;

            try {
                isDown = controller.isButtonPressed(button);
            } catch (ControllerUnpluggedException e) {
                logger.warn("Controller unplugged mid-frame, will retry...");
                controller = null;
                return;
            }

            if (isDown != wasDown) {
                eventQueue.add(new ControllerButtonEvent(button, isDown));
                lastButtonStates.put(button, isDown);
            }
        }

        for (ControllerAxis axis : ControllerAxis.values()) {
            float lastValue = lastAxisStates.getOrDefault(axis, 0f);
            float currentValue;

            try {
                currentValue = controller.getAxisState(axis);
            } catch (ControllerUnpluggedException e) {
                currentValue = 0f;
            }

            boolean leftDeadzone = Math.abs(currentValue) > Minecraft.getMinecraft().gameSettings.controllerDeadzone;
            boolean wasOutsideDeadzone = Math.abs(lastValue) > Minecraft.getMinecraft().gameSettings.controllerDeadzone;

            if (leftDeadzone || wasOutsideDeadzone) {
                if (Math.abs(currentValue - lastValue) >= 0.05f) {
                    axisEventQueue.add(new ControllerAxisEvent(axis, currentValue));
                }
            }

            lastAxisStates.put(axis, currentValue);
        }

        updateBindings();
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
        } catch (Exception e) {
            return false;
        }
    }

    public static float getAxis(ControllerAxis axis) {
        try {
            return controller.getAxisState(axis);
        } catch (Exception e) {
            return 0f;
        }
    }

    /**
     * Polls the next controller event, similar to LWJGL's Keyboard.next().
     * @return true if an event was fetched, false if none left this frame.
     */
    public static boolean next() {
        currentEvent = eventQueue.poll();
        return currentEvent != null;
    }

    /**
     * Polls the next controller axis event, similar to LWJGL's Mouse.next().
     * @return true if an axis event was fetched, false if none left this frame.
     */
    public static boolean nextAxis() {
        currentAxisEvent = axisEventQueue.poll();
        return currentAxisEvent != null;
    }

    /**
     * Returns the button associated with the current event.
     */
    public static ControllerButton getEventButton() {
        return currentEvent != null ? currentEvent.button() : null;
    }

    /**
     * Returns true if the event was a button press, false if released.
     */
    public static boolean getEventButtonState() {
        return currentEvent != null && currentEvent.pressed();
    }

    /**
     * Returns the axis associated with the current event.
     */
    public static ControllerAxis getEventAxis() {
        return currentAxisEvent != null ? currentAxisEvent.axis() : null;
    }

    /**
     * Returns the axis value associated with the current event.
     * @return a float between -1.0 and 1.0
     */
    public static float getEventAxisValue() {
        return currentAxisEvent != null ? currentAxisEvent.value() : 0f;
    }

    public static void registerBindings(ControllerInputBinding[] newBindings) {
        bindings = newBindings;
    }

    public static void resetBindings() {
        if (bindings == null || bindings.length == 0) return;
        for (ControllerInputBinding b : bindings) {
            b.reset();
        }
    }

    public static void updateBindings() {
        if (bindings == null || bindings.length == 0) return;
        for (ControllerInputBinding b : bindings) {
            b.update();
        }
    }

    private static void tryReconnect() throws ControllerUnpluggedException {
        if (!initialized) return;

        controllers.update();
        int count = controllers.getNumControllers();

        if (count == 0) {
            if (controller != null) {
                logger.info("Controller disconnected.");
                controller = null;
                resetBindings();
            }
            return;
        }

        for (int i = 0; i < count; i++) {
            ControllerIndex idx = controllers.getControllerIndex(i);
            if (idx != null && idx.isConnected()) {
                controller = idx;
                logger.info("Controller connected: {}::{}", controller.getName(), controller.getIndex());
                try {
                    controller.doVibration(0.5f, 0.5f, 300);
                } catch (Exception ignored) {}
                return;
            }
        }
    }
}
