package com.port.gpuTape;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.client.shader.Framebuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Tracks all created Framebuffer objects.
 * If a framebuffer is not used for a certain amount of time, it's considered leaked and is deleted.
 */
public class GpuTape {
    private static final Logger logger = LogManager.getLogger("VideoTape");

    // A thread-safe list to hold all active framebuffers.
    public static final List<Framebuffer> TRACKED_FRAMEBUFFERS = new CopyOnWriteArrayList<>();

    public static long frameCounter = 0;
    private static final long LEAK_THRESHOLD_FRAMES = 600L;

    public static void onTick() {
        frameCounter++;

        // Iterate through all tracked framebuffers to find leaks.
        for (Framebuffer framebuffer : TRACKED_FRAMEBUFFERS) {
            // Check if the framebuffer has exceeded the time limit since its last use.
            if (frameCounter - framebuffer.getLastUseFrame() > LEAK_THRESHOLD_FRAMES) {
                // This framebuffer is likely leaked. Delete it.
                logger.info("Detected and deleted a leaked framebuffer");
                framebuffer.deleteFramebuffer();
            }
        }
    }
}