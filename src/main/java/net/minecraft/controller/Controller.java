package net.minecraft.controller;

import com.studiohartman.jamepad.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Controller {
    private static final Logger logger = LogManager.getLogger("Jamepad");

    private static final ControllerManager controllers = new ControllerManager();
    private static ControllerIndex controller;

    public static final float DEADZONE = 0.2f;

    private static boolean initialized = false;

    private static final Map<ControllerButton, Boolean> lastButtonStates = new EnumMap<>(ControllerButton.class);
    private static final Queue<ControllerButtonEvent> eventQueue = new ArrayDeque<>();
    private static ControllerButtonEvent currentEvent = null;

    private static final Map<ControllerAxis, Float> lastAxisStates = new EnumMap<>(ControllerAxis.class);
    private static final Queue<ControllerAxisEvent> axisEventQueue = new ArrayDeque<>();
    private static ControllerAxisEvent currentAxisEvent = null;

    private static ControllerBinding[] bindings = new ControllerBinding[0];
    private static ControllerAxisBinding[] axisBindings = new ControllerAxisBinding[0];

    public static void init() {
        if (initialized) return;
        controllers.initSDLGamepad();
        controller = controllers.getControllerIndex(0);
        initialized = true;

        for (ControllerButton button : ControllerButton.values()) {
            lastButtonStates.put(button, false);
        }

        for (ControllerAxis axis : ControllerAxis.values()) {
            lastAxisStates.put(axis, 0f);
        }
    }

    public static void poll() {
        if (!initialized) return;

        controllers.update();

        if (controller != null && controller.isConnected()) {
            for (ControllerButton button : ControllerButton.values()) {
                boolean wasDown = lastButtonStates.getOrDefault(button, false);
                boolean isDown;

                try {
                    isDown = controller.isButtonPressed(button);
                } catch (ControllerUnpluggedException e) {
                    isDown = false;
                }

                if (isDown != wasDown) {
                    eventQueue.add(new ControllerButtonEvent(button, isDown));
                    lastButtonStates.put(button, isDown);
                }
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

            boolean leftDeadzone = Math.abs(currentValue) > DEADZONE;
            boolean wasOutsideDeadzone = Math.abs(lastValue) > DEADZONE;

            if (leftDeadzone || wasOutsideDeadzone) {
                if (Math.abs(currentValue - lastValue) >= 0.05f) {
                    axisEventQueue.add(new ControllerAxisEvent(axis, currentValue));
                }
            }

            lastAxisStates.put(axis, currentValue);
        }

        updateBindings();
        updateAxisBindings();
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

    public static void registerBindings(ControllerBinding[] newBindings) {
        bindings = newBindings;
    }

    public static void updateBindings() {
        if (bindings == null || bindings.length == 0) return;
        for (ControllerBinding b : bindings) {
            b.update();
        }
    }

    public static void registerAxisBindings(ControllerAxisBinding[] newBindings) {
        axisBindings = newBindings;
    }

    public static void updateAxisBindings() {
        if (axisBindings == null || axisBindings.length == 0) return;
        for (ControllerAxisBinding a : axisBindings) {
            a.update();
        }
    }
}
