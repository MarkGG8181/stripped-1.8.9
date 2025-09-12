package org.lwjgl.opengl;

public final class GLContext {

    private static final ContextCapabilities contextCapabilities = new ContextCapabilities();

    public static ContextCapabilities getCapabilities() {
        return contextCapabilities;
    }

    private GLContext() {
    }
}
