package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.renderer.GlStateManager;

public class ItemCameraTransforms
{
    public static final ItemCameraTransforms DEFAULT = new ItemCameraTransforms();
    public static float offsetTranslateX = 0.0F;
    public static float offsetTranslateY = 0.0F;
    public static float offsetTranslateZ = 0.0F;
    public static float offsetRotationX = 0.0F;
    public static float offsetRotationY = 0.0F;
    public static float offsetRotationZ = 0.0F;
    public static float offsetScaleX = 0.0F;
    public static float offsetScaleY = 0.0F;
    public static float offsetScaleZ = 0.0F;
    public final ItemTransformVec3f thirdPerson;
    public final ItemTransformVec3f firstPerson;
    public final ItemTransformVec3f head;
    public final ItemTransformVec3f gui;
    public final ItemTransformVec3f ground;
    public final ItemTransformVec3f fixed;

    private ItemCameraTransforms()
    {
        this(ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT);
    }

    public ItemCameraTransforms(ItemCameraTransforms transforms)
    {
        this.thirdPerson = transforms.thirdPerson;
        this.firstPerson = transforms.firstPerson;
        this.head = transforms.head;
        this.gui = transforms.gui;
        this.ground = transforms.ground;
        this.fixed = transforms.fixed;
    }

    public ItemCameraTransforms(ItemTransformVec3f thirdPersonIn, ItemTransformVec3f firstPersonIn, ItemTransformVec3f headIn, ItemTransformVec3f guiIn, ItemTransformVec3f groundIn, ItemTransformVec3f fixedIn)
    {
        this.thirdPerson = thirdPersonIn;
        this.firstPerson = firstPersonIn;
        this.head = headIn;
        this.gui = guiIn;
        this.ground = groundIn;
        this.fixed = fixedIn;
    }

    public void applyTransform(ItemCameraTransforms.TransformType type)
    {
        ItemTransformVec3f itemtransformvec3f = this.getTransform(type);

        if (itemtransformvec3f != ItemTransformVec3f.DEFAULT)
        {
            GlStateManager.translate(itemtransformvec3f.translation.x + offsetTranslateX, itemtransformvec3f.translation.y + offsetTranslateY, itemtransformvec3f.translation.z + offsetTranslateZ);
            GlStateManager.rotate(itemtransformvec3f.rotation.y + offsetRotationY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(itemtransformvec3f.rotation.x + offsetRotationX, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(itemtransformvec3f.rotation.z + offsetRotationZ, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(itemtransformvec3f.scale.x + offsetScaleX, itemtransformvec3f.scale.y + offsetScaleY, itemtransformvec3f.scale.z + offsetScaleZ);
        }
    }

    public ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType type)
    {
        switch (type)
        {
            case THIRD_PERSON:
                return this.thirdPerson;

            case FIRST_PERSON:
                return this.firstPerson;

            case HEAD:
                return this.head;

            case GUI:
                return this.gui;

            case GROUND:
                return this.ground;

            case FIXED:
                return this.fixed;

            default:
                return ItemTransformVec3f.DEFAULT;
        }
    }

    public boolean func_181687_c(ItemCameraTransforms.TransformType type)
    {
        return !this.getTransform(type).equals(ItemTransformVec3f.DEFAULT);
    }

    static class Deserializer implements JsonDeserializer<ItemCameraTransforms>
    {
        public ItemCameraTransforms deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
            ItemTransformVec3f itemtransformvec3f = this.func_181683_a(p_deserialize_3_, jsonobject, "thirdperson");
            ItemTransformVec3f itemtransformvec3f1 = this.func_181683_a(p_deserialize_3_, jsonobject, "firstperson");
            ItemTransformVec3f itemtransformvec3f2 = this.func_181683_a(p_deserialize_3_, jsonobject, "head");
            ItemTransformVec3f itemtransformvec3f3 = this.func_181683_a(p_deserialize_3_, jsonobject, "gui");
            ItemTransformVec3f itemtransformvec3f4 = this.func_181683_a(p_deserialize_3_, jsonobject, "ground");
            ItemTransformVec3f itemtransformvec3f5 = this.func_181683_a(p_deserialize_3_, jsonobject, "fixed");
            return new ItemCameraTransforms(itemtransformvec3f, itemtransformvec3f1, itemtransformvec3f2, itemtransformvec3f3, itemtransformvec3f4, itemtransformvec3f5);
        }

        private ItemTransformVec3f func_181683_a(JsonDeserializationContext p_181683_1_, JsonObject p_181683_2_, String p_181683_3_)
        {
            return p_181683_2_.has(p_181683_3_) ? (ItemTransformVec3f)p_181683_1_.deserialize(p_181683_2_.get(p_181683_3_), ItemTransformVec3f.class) : ItemTransformVec3f.DEFAULT;
        }
    }

    public static enum TransformType
    {
        NONE,
        THIRD_PERSON,
        FIRST_PERSON,
        HEAD,
        GUI,
        GROUND,
        FIXED;
    }
}
