package net.minecraft.util;

import com.google.common.collect.ForwardingSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.HashSet;
import java.util.Set;

public class JsonSerializableSet extends ForwardingSet<String> implements IJsonSerializable
{
    private final Set<String> underlyingSet = new HashSet<>();

    public void fromJson(JsonElement json)
    {
        if (json.isJsonArray())
        {
            for (JsonElement jsonelement : json.getAsJsonArray())
            {
                this.add(jsonelement.getAsString());
            }
        }
    }

    /**
     * Gets the JsonElement that can be serialized.
     */
    public JsonElement getSerializableElement()
    {
        JsonArray jsonarray = new JsonArray();

        for (String s : this)
        {
            jsonarray.add(new JsonPrimitive(s));
        }

        return jsonarray;
    }

    protected Set<String> delegate()
    {
        return this.underlyingSet;
    }
}
