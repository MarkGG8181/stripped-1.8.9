package net.minecraft.port.melod.buffer;

import net.minecraft.client.renderer.GLAllocation;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BufferPool {
    private static final ConcurrentLinkedQueue<ByteBuffer> POOL_2MB = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<ByteBuffer> POOL_256KB = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<ByteBuffer> POOL_128KB = new ConcurrentLinkedQueue<>();

    private static ByteBuffer acquireBuffer(int capacity, ConcurrentLinkedQueue<ByteBuffer> pool) {
        ByteBuffer buffer = pool.poll();
        if (buffer == null) {
            buffer = GLAllocation.createDirectByteBuffer(capacity);
        }
        return buffer;
    }

    private static void releaseBuffer(ByteBuffer buffer) {
        buffer.clear();
        ConcurrentLinkedQueue<ByteBuffer> pool;
        if (buffer.capacity() == 2097152 * 4) {
            pool = POOL_2MB;
        } else if (buffer.capacity() == 262144 * 4) {
            pool = POOL_256KB;
        } else {
            pool = POOL_128KB;
        }
        pool.offer(buffer);
    }

    public static ByteBuffer acquire_2MB() { return acquireBuffer(2097152 * 4, POOL_2MB); }
    public static ByteBuffer acquire_256KB() { return acquireBuffer(262144 * 4, POOL_256KB); }
    public static ByteBuffer acquire_128KB() { return acquireBuffer(131072 * 4, POOL_128KB); }
    public static void release(ByteBuffer buffer) { releaseBuffer(buffer); }
}