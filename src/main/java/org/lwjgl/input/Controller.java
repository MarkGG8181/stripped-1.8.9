package org.lwjgl.input;

import com.studiohartman.jamepad.ControllerButton;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Lightweight static wrapper for Jamepad controller input.
 * Emulates LWJGL-style polling and events for compatibility with Minecraft's input system.
 */
public class Controller {
    private static final Logger logger = LogManager.getLogger("Jamepad");

    private static ControllerManager manager;
    private static ControllerState prevState;
    private static ControllerState currState;

    private static boolean created = false;
    private static boolean connected = false;

    private static final int CONTROLLER_INDEX = 0; // Always use first controller
    private static final float TRIGGER_THRESHOLD = 0.5f;

    private static final Queue<ButtonEvent> eventQueue = new LinkedList<>();

    private static int nextButton = -1;
    private static boolean nextPressed = false;

    public static final float STICK_DEADZONE = 0.2f;

    // === Lifecycle ===

    public static void create() {
        if (created) return;

        try {
            manager = new ControllerManager();
            manager.initSDLGamepad();

            currState = manager.getState(CONTROLLER_INDEX);
            prevState = currState;
            connected = currState.isConnected;

            if (connected) {
                logger.info("Controller connected: {}", currState.controllerType);
            } else {
                logger.info("No controller connected.");
            }

            created = true;
        } catch (Throwable t) {
            logger.error("Failed to initialize controller system", t);
        }
    }

    public static void destroy() {
        if (!created) return;
        if (manager != null) {
            manager.quitSDLGamepad();
            manager = null;
        }
        created = false;
        connected = false;
        eventQueue.clear();
    }

    public static boolean isCreated() {
        return created;
    }

    public static boolean isConnected() {
        if (!created) return false;
        currState = manager.getState(CONTROLLER_INDEX);
        connected = currState.isConnected;
        return connected;
    }

    public static void update() {
        if (!created) return;

        manager.update();
        ControllerState newState = manager.getState(CONTROLLER_INDEX);

        if (!newState.isConnected) {
            if (connected) logger.info("Controller disconnected.");
            connected = false;
            currState = newState;
            return;
        }

        if (!connected && newState.isConnected) {
            logger.info("Controller connected: {}", newState.controllerType);
        }

        connected = true;
        prevState = currState;
        currState = newState;
    }

    public static void poll() {
        if (!created || !connected) return;

        eventQueue.clear();

        for (ControllerButton button : ControllerButton.values()) {
            boolean prevPressed = isButtonPressed(prevState, button);
            boolean currPressed = isButtonPressed(currState, button);

            if (prevPressed != currPressed) {
                eventQueue.add(new ButtonEvent(button.ordinal(), currPressed));
            }
        }

        // Triggers (as pseudo-buttons)
        detectTriggerChange(prevState.leftTrigger, currState.leftTrigger, 1000);
        detectTriggerChange(prevState.rightTrigger, currState.rightTrigger, 1001);
    }

    public static boolean next() {
        if (!created || !connected) return false;
        if (eventQueue.isEmpty()) return false;

        ButtonEvent e = eventQueue.poll();
        nextButton = e.button;
        nextPressed = e.pressed;
        return true;
    }

    public static int getEventButton() {
        return nextButton;
    }

    public static boolean getEventButtonState() {
        return nextPressed;
    }

    public static boolean isButtonDown(int index) {
        if (!created || !connected) return false;
        ControllerButton[] buttons = ControllerButton.values();
        if (index < 0 || index >= buttons.length) return false;
        return isButtonPressed(currState, buttons[index]);
    }

    public static float getAxis(int axis) {
        if (!created || !connected) return 0f;
        return switch (axis) {
            case 0 -> currState.leftStickX;
            case 1 -> currState.leftStickY;
            case 2 -> currState.rightStickX;
            case 3 -> currState.rightStickY;
            case 4 -> currState.leftTrigger;
            case 5 -> currState.rightTrigger;
            default -> 0f;
        };
    }

    private static boolean isButtonPressed(ControllerState state, ControllerButton button) {
        if (state == null) return false;
        return switch (button) {
            case A -> state.a;
            case B -> state.b;
            case X -> state.x;
            case Y -> state.y;
            case START -> state.start;
            case BACK -> state.back;
            case GUIDE -> state.guide;
            case DPAD_UP -> state.dpadUp;
            case DPAD_DOWN -> state.dpadDown;
            case DPAD_LEFT -> state.dpadLeft;
            case DPAD_RIGHT -> state.dpadRight;
            case LEFTSTICK -> state.leftStickClick;
            case RIGHTSTICK -> state.rightStickClick;
            case LEFTBUMPER -> state.lb;
            case RIGHTBUMPER -> state.rb;
        };
    }

    private static void detectTriggerChange(float prev, float curr, int id) {
        boolean prevPressed = prev > TRIGGER_THRESHOLD;
        boolean currPressed = curr > TRIGGER_THRESHOLD;
        if (prevPressed != currPressed) {
            eventQueue.add(new ButtonEvent(id, currPressed));
        }
    }

    private record ButtonEvent(int button, boolean pressed) {}
}
