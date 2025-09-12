package net.minecraft.client.renderer.culling;

import java.nio.FloatBuffer;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;

public class ClippingHelperImpl extends ClippingHelper {
    private static final ClippingHelperImpl instance = new ClippingHelperImpl();
    private final FloatBuffer projectionMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer modelviewMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);

    public static ClippingHelper getInstance() {
        instance.init();
        return instance;
    }

    private void normalize(double[] plane) {
        double invLen = 1.0 / Math.sqrt(plane[0] * plane[0] + plane[1] * plane[1] + plane[2] * plane[2]);
        plane[0] *= invLen;
        plane[1] *= invLen;
        plane[2] *= invLen;
        plane[3] *= invLen;
    }

    public void init() {
        projectionMatrixBuffer.clear();
        modelviewMatrixBuffer.clear();
        GlStateManager.getFloat(2983, projectionMatrixBuffer);
        GlStateManager.getFloat(2982, modelviewMatrixBuffer);

        for (int i = 0; i < 16; i++) {
            projectionMatrix[i] = projectionMatrixBuffer.get(i);
            modelviewMatrix[i] = modelviewMatrixBuffer.get(i);
        }

        // multiply modelview * projection into clippingMatrix
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                clippingMatrix[row * 4 + col] =
                    modelviewMatrix[row * 4 + 0] * projectionMatrix[col + 0] +
                        modelviewMatrix[row * 4 + 1] * projectionMatrix[col + 4] +
                        modelviewMatrix[row * 4 + 2] * projectionMatrix[col + 8] +
                        modelviewMatrix[row * 4 + 3] * projectionMatrix[col + 12];
            }
        }

        extractPlane(frustum[0], clippingMatrix[3] - clippingMatrix[0],
            clippingMatrix[7] - clippingMatrix[4],
            clippingMatrix[11] - clippingMatrix[8],
            clippingMatrix[15] - clippingMatrix[12]);

        extractPlane(frustum[1], clippingMatrix[3] + clippingMatrix[0],
            clippingMatrix[7] + clippingMatrix[4],
            clippingMatrix[11] + clippingMatrix[8],
            clippingMatrix[15] + clippingMatrix[12]);

        extractPlane(frustum[2], clippingMatrix[3] + clippingMatrix[1],
            clippingMatrix[7] + clippingMatrix[5],
            clippingMatrix[11] + clippingMatrix[9],
            clippingMatrix[15] + clippingMatrix[13]);

        extractPlane(frustum[3], clippingMatrix[3] - clippingMatrix[1],
            clippingMatrix[7] - clippingMatrix[5],
            clippingMatrix[11] - clippingMatrix[9],
            clippingMatrix[15] - clippingMatrix[13]);

        extractPlane(frustum[4], clippingMatrix[3] - clippingMatrix[2],
            clippingMatrix[7] - clippingMatrix[6],
            clippingMatrix[11] - clippingMatrix[10],
            clippingMatrix[15] - clippingMatrix[14]);

        extractPlane(frustum[5], clippingMatrix[3] + clippingMatrix[2],
            clippingMatrix[7] + clippingMatrix[6],
            clippingMatrix[11] + clippingMatrix[10],
            clippingMatrix[15] + clippingMatrix[13]);
    }

    private void extractPlane(double[] plane, double a, double b, double c, double d) {
        plane[0] = a;
        plane[1] = b;
        plane[2] = c;
        plane[3] = d;
        normalize(plane);
    }
}
