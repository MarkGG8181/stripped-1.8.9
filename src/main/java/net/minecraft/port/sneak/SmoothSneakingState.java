package net.minecraft.port.sneak;

import org.lwjgl.Sys;

public class SmoothSneakingState {

    private float crouchProgress = 0.0F;
    private float lastFrameTime = 0.0F;

    public float getSneakingHeightOffset(boolean isSneaking) {
        float target = isSneaking ? 1.0f : 0.0f;

        float now = (float)Sys.getTime() / Sys.getTimerResolution();
        float deltaTime = 0.0F;

        if (lastFrameTime > 0.0f) {
            deltaTime = now - lastFrameTime;
        }

        lastFrameTime = now;

        float difference = target - this.crouchProgress;

        if (Math.abs(difference) < 0.0001f) {
            this.crouchProgress = target;
        }
        else {
            this.crouchProgress += difference * deltaTime * 10.0f;
        }

        return this.crouchProgress * -0.35F;
    }
}
