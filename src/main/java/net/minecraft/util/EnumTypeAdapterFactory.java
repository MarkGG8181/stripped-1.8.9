package net.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EnumTypeAdapterFactory implements TypeAdapterFactory
{
    public <T> TypeAdapter<T> create(Gson p_create_1_, TypeToken<T> p_create_2_)
    {
        Class<T> oclass = (Class<T>)p_create_2_.getRawType();

        if (!oclass.isEnum())
        {
            return null;
        }
        else
        {
            final Map<String, T> map = new HashMap<>();

            for (T t : oclass.getEnumConstants())
            {
                map.put(this.func151232A(t), t);
            }

            return new TypeAdapter<>()
            {
                public void write(JsonWriter p_write_1_, T p_write_2_) throws IOException
                {
                    if (p_write_2_ == null)
                    {
                        p_write_1_.nullValue();
                    }
                    else
                    {
                        p_write_1_.value(EnumTypeAdapterFactory.this.func151232A(p_write_2_));
                    }
                }

                public T read(JsonReader p_read_1_) throws IOException
                {
                    if (p_read_1_.peek() == JsonToken.NULL)
                    {
                        p_read_1_.nextNull();
                        return (T)null;
                    }
                    else
                    {
                        return (T)map.get(p_read_1_.nextString());
                    }
                }
            };
        }
    }

    private String func151232A(Object p_151232_1_)
    {
        return p_151232_1_ instanceof Enum e ? e.name().toLowerCase(Locale.US) : p_151232_1_.toString().toLowerCase(Locale.US);
    }
}
