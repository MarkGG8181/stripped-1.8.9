package net.minecraft.client.util;

import com.google.common.collect.Lists;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class JsonException extends IOException {
    private final List<JsonException.Entry> entries = Lists.<JsonException.Entry>newArrayList();
    private final String exceptionMessage;

    public JsonException(String message) {
        this.entries.add(new JsonException.Entry());
        this.exceptionMessage = message;
    }

    public JsonException(String message, Throwable cause) {
        super(cause);
        this.entries.add(new JsonException.Entry());
        this.exceptionMessage = message;
    }

    public void func_151380_a(String p_151380_1_) {
        ((JsonException.Entry) this.entries.get(0)).func_151373_a(p_151380_1_);
    }

    public void func_151381_b(String p_151381_1_) {
        ((JsonException.Entry) this.entries.get(0)).filename = p_151381_1_;
        this.entries.add(0, new JsonException.Entry());
    }

    public String getMessage() {
        return "Invalid " + ((JsonException.Entry) this.entries.get(this.entries.size() - 1)).toString() + ": " + this.exceptionMessage;
    }

    public static JsonException func_151379_a(Exception p_151379_0_) {
        if (p_151379_0_ instanceof JsonException) {
            return (JsonException) p_151379_0_;
        } else {
            String s = p_151379_0_.getMessage();

            if (p_151379_0_ instanceof FileNotFoundException) {
                s = "File not found";
            }

            return new JsonException(s, p_151379_0_);
        }
    }

    public static class Entry {
        private String filename;
        private final List<String> jsonKeys;

        private Entry() {
            this.filename = null;
            this.jsonKeys = Lists.<String>newArrayList();
        }

        private void func_151373_a(String p_151373_1_) {
            this.jsonKeys.add(0, p_151373_1_);
        }

        public String func_151372_b() {
            return StringUtils.join((Iterable) this.jsonKeys, "->");
        }

        public String toString() {
            return this.filename != null ? (!this.jsonKeys.isEmpty() ? this.filename + " " + this.func_151372_b() : this.filename) : (!this.jsonKeys.isEmpty() ? "(Unknown file) " + this.func_151372_b() : "(Unknown file)");
        }
    }
}
