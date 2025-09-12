package net.minecraft.port.krypton;

/**
 * Maps VarInt byte sizes to a lookup table of 64 entries.
 */
public final class VarIntUtil {
    private static final int[] VARINT_EXACT_BYTE_LENGTHS = new int[33];

    static {
        for (int i = 0; i <= 32; i++) {
            VARINT_EXACT_BYTE_LENGTHS[i] = (int)Math.ceil((31d - (i - 1)) / 7d);
        }
        VARINT_EXACT_BYTE_LENGTHS[32] = 1; // Special case for 0.
    }

    public static int getVarIntLength(int value) {
        return VARINT_EXACT_BYTE_LENGTHS[Integer.numberOfLeadingZeros(value)];
    }

    private VarIntUtil() {
    }
}
