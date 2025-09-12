package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL11;

public final class GlStateManager
{
    public static void pushAttrib()
    {
        GL11.glPushAttrib(8256);
    }

    public static void popAttrib()
    {
        GL11.glPopAttrib();
    }

    public static void disableAlpha()
    {
        GL11.glDisable(3008);
    }

    public static void enableAlpha()
    {
        GL11.glEnable(3008);
    }

    public static void alphaFunc(int func, float ref)
    {
        GL11.glAlphaFunc(func, ref);
    }

    public static void enableLighting()
    {
        GL11.glEnable(2896);
    }

    public static void disableLighting()
    {
        GL11.glDisable(2896);
    }

    public static void enableLight(int light)
    {
        GL11.glEnable(16384 + light);
    }

    public static void disableLight(int light)
    {
        GL11.glDisable(16384 + light);
    }

    public static void enableColorMaterial()
    {
        GL11.glEnable(2903);
    }

    public static void disableColorMaterial()
    {
        GL11.glDisable(2903);
    }

    public static void colorMaterial(int face, int mode)
    {
        GL11.glColorMaterial(face, mode);
    }

    public static void disableDepth()
    {
        GL11.glDisable(2929);
    }

    public static void enableDepth()
    {
        GL11.glEnable(2929);
    }

    public static void depthFunc(int depthFunc)
    {
        GL11.glDepthFunc(depthFunc);
    }

    public static void depthMask(boolean flagIn)
    {
        GL11.glDepthMask(flagIn);
    }

    public static void disableBlend()
    {
        GL11.glDisable(3042);
    }

    public static void enableBlend()
    {
        GL11.glEnable(3042);
    }

    public static void blendFunc(int srcFactor, int dstFactor)
    {
        GL11.glBlendFunc(srcFactor, dstFactor);
    }

    public static void tryBlendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha)
    {
        OpenGlHelper.glBlendFunc(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
    }

    public static void enableFog()
    {
        GL11.glEnable(2912);
    }

    public static void disableFog()
    {
        GL11.glDisable(2912);
    }

    public static void setFog(int param)
    {
        GL11.glFogi(GL11.GL_FOG_MODE, param);
    }

    public static void setFogDensity(float param)
    {
        GL11.glFogf(GL11.GL_FOG_DENSITY, param);
    }

    public static void setFogStart(float param)
    {
        GL11.glFogf(GL11.GL_FOG_START, param);
    }

    public static void setFogEnd(float param)
    {
        GL11.glFogf(GL11.GL_FOG_END, param);
    }

    public static void enableCull()
    {
        GL11.glEnable(2884);
    }

    public static void disableCull()
    {
        GL11.glDisable(2884);
    }

    public static void cullFace(int mode)
    {
        GL11.glCullFace(mode);
    }

    public static void enablePolygonOffset()
    {
        GL11.glEnable(32823);
    }

    public static void disablePolygonOffset()
    {
        GL11.glDisable(32823);
    }

    public static void doPolygonOffset(float factor, float units)
    {
        GL11.glPolygonOffset(factor, units);
    }

    public static void enableColorLogic()
    {
        GL11.glEnable(3058);
    }

    public static void disableColorLogic()
    {
        GL11.glDisable(3058);
    }

    public static void colorLogicOp(int opcode)
    {
        GL11.glLogicOp(opcode);
    }

    public static void enableTexGenCoord(GlStateManager.TexGen texGen)
    {
        int cap;
        switch (texGen)
        {
            case S:
                cap = 3168;
                break;
            case T:
                cap = 3169;
                break;
            case R:
                cap = 3170;
                break;
            case Q:
                cap = 3171;
                break;
            default:
                return;
        }
        GL11.glEnable(cap);
    }

    public static void disableTexGenCoord(GlStateManager.TexGen texGen)
    {
        int cap;
        switch (texGen)
        {
            case S:
                cap = 3168;
                break;
            case T:
                cap = 3169;
                break;
            case R:
                cap = 3170;
                break;
            case Q:
                cap = 3171;
                break;
            default:
                return;
        }
        GL11.glDisable(cap);
    }

    public static void texGen(GlStateManager.TexGen texGen, int param)
    {
        int coord;
        switch (texGen)
        {
            case S:
                coord = 8192;
                break;
            case T:
                coord = 8193;
                break;
            case R:
                coord = 8194;
                break;
            case Q:
                coord = 8195;
                break;
            default:
                return;
        }
        GL11.glTexGeni(coord, GL11.GL_TEXTURE_GEN_MODE, param);
    }

    public static void texGen(GlStateManager.TexGen texGen, int pname, FloatBuffer params)
    {
        int coord;
        switch (texGen)
        {
            case S:
                coord = 8192;
                break;
            case T:
                coord = 8193;
                break;
            case R:
                coord = 8194;
                break;
            case Q:
                coord = 8195;
                break;
            default:
                return;
        }
        GL11.glTexGen(coord, pname, params);
    }

    public static void setActiveTexture(int texture)
    {
        OpenGlHelper.setActiveTexture(texture);
    }

    public static void enableTexture2D()
    {
        GL11.glEnable(3553);
    }

    public static void disableTexture2D()
    {
        GL11.glDisable(3553);
    }

    public static int generateTexture()
    {
        return GL11.glGenTextures();
    }

    public static void deleteTexture(int texture)
    {
        GL11.glDeleteTextures(texture);
    }

    public static void bindTexture(int texture)
    {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
    }

    public static void enableNormalize()
    {
        GL11.glEnable(2977);
    }

    public static void disableNormalize()
    {
        GL11.glDisable(2977);
    }

    public static void shadeModel(int mode)
    {
        GL11.glShadeModel(mode);
    }

    public static void enableRescaleNormal()
    {
        GL11.glEnable(32826);
    }

    public static void disableRescaleNormal()
    {
        GL11.glDisable(32826);
    }

    public static void viewport(int x, int y, int width, int height)
    {
        GL11.glViewport(x, y, width, height);
    }

    public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha)
    {
        GL11.glColorMask(red, green, blue, alpha);
    }

    public static void clearDepth(double depth)
    {
        GL11.glClearDepth(depth);
    }

    public static void clearColor(float red, float green, float blue, float alpha)
    {
        GL11.glClearColor(red, green, blue, alpha);
    }

    public static void clear(int mask)
    {
        GL11.glClear(mask);
    }

    public static void matrixMode(int mode)
    {
        GL11.glMatrixMode(mode);
    }

    public static void loadIdentity()
    {
        GL11.glLoadIdentity();
    }

    public static void pushMatrix()
    {
        GL11.glPushMatrix();
    }

    public static void popMatrix()
    {
        GL11.glPopMatrix();
    }

    public static void getFloat(int pname, FloatBuffer params)
    {
        GL11.glGetFloat(pname, params);
    }

    public static void ortho(double left, double right, double bottom, double top, double zNear, double zFar)
    {
        GL11.glOrtho(left, right, bottom, top, zNear, zFar);
    }

    public static void rotate(float angle, float x, float y, float z)
    {
        GL11.glRotatef(angle, x, y, z);
    }

    public static void scale(float x, float y, float z)
    {
        GL11.glScalef(x, y, z);
    }

    public static void scale(double x, double y, double z)
    {
        GL11.glScaled(x, y, z);
    }

    public static void translate(float x, float y, float z)
    {
        GL11.glTranslatef(x, y, z);
    }

    public static void translate(double x, double y, double z)
    {
        GL11.glTranslated(x, y, z);
    }

    public static void multMatrix(FloatBuffer matrix)
    {
        GL11.glMultMatrix(matrix);
    }

    public static void color(float colorRed, float colorGreen, float colorBlue, float colorAlpha)
    {
        GL11.glColor4f(colorRed, colorGreen, colorBlue, colorAlpha);
    }

    public static void color(float colorRed, float colorGreen, float colorBlue)
    {
        GL11.glColor4f(colorRed, colorGreen, colorBlue, 1.0F);
    }

    public static void resetColor()
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void callList(int list)
    {
        GL11.glCallList(list);
    }

    public enum TexGen
    {
        S,
        T,
        R,
        Q
    }

    private GlStateManager() {
    }
}
