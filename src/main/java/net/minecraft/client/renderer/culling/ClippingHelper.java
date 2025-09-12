package net.minecraft.client.renderer.culling;

public class ClippingHelper {
    public double[][] frustum = new double[6][4];
    public float[] projectionMatrix = new float[16];
    public float[] modelviewMatrix = new float[16];
    public float[] clippingMatrix = new float[16];

    /**
     * Returns true if the box is inside all 6 clipping planes, otherwise returns false.
     */
    public boolean isBoxInFrustum(double minX, double minY, double minZ,
                                  double maxX, double maxY, double maxZ) {
        for (int i = 0; i < 6; i++) {
            double[] plane = this.frustum[i];

            // Pick the vertex most in the direction of the normal
            double x = plane[0] >= 0 ? maxX : minX;
            double y = plane[1] >= 0 ? maxY : minY;
            double z = plane[2] >= 0 ? maxZ : minZ;

            if (plane[0] * x + plane[1] * y + plane[2] * z + plane[3] <= 0.0D) {
                return false;
            }
        }
        return true;
    }
}
