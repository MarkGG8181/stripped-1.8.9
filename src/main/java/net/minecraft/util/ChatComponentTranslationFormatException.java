package net.minecraft.util;

public class ChatComponentTranslationFormatException extends IllegalArgumentException
{
    public ChatComponentTranslationFormatException(ChatComponentTranslation component, String message)
    {
        super("Error parsing: %s: %s".formatted(new Object[]{component, message}));
    }

    public ChatComponentTranslationFormatException(ChatComponentTranslation component, int index)
    {
        super("Invalid index %d requested for %s".formatted(new Object[]{Integer.valueOf(index), component}));
    }

    public ChatComponentTranslationFormatException(ChatComponentTranslation component, Throwable cause)
    {
        super("Error while parsing: %s".formatted(new Object[]{component}), cause);
    }
}
