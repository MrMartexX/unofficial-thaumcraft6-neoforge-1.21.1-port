package thaumcraft.api.golems.parts;

import net.minecraft.resources.ResourceLocation;
import thaumcraft.api.golems.IGolemAPI;

public class PartModel {
    public enum EnumAttachPoint {
        HEAD,
        BODY,
        ARMS,
        LEGS
    }

    public enum EnumLimbSide {
        LEFT,
        RIGHT,
        MIDDLE
    }

    private final ResourceLocation objModel;
    private final ResourceLocation texture;
    private final EnumAttachPoint attachPoint;

    public PartModel(ResourceLocation objModel, ResourceLocation objTexture, EnumAttachPoint attachPoint) {
        this.objModel = objModel;
        this.texture = objTexture;
        this.attachPoint = attachPoint;
    }

    public ResourceLocation getObjModel() {
        return objModel;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public EnumAttachPoint getAttachPoint() {
        return attachPoint;
    }

    public boolean useMaterialTextureForObjectPart(String partName) {
        return partName != null && partName.startsWith("bm");
    }

    public void preRenderObjectPart(String partName, IGolemAPI golem, float partialTicks, EnumLimbSide side) {
    }

    public void postRenderObjectPart(String partName, IGolemAPI golem, float partialTicks, EnumLimbSide side) {
    }

    public float preRenderArmRotationX(IGolemAPI golem, float partialTicks, EnumLimbSide side, float inputRot) {
        return inputRot;
    }

    public float preRenderArmRotationY(IGolemAPI golem, float partialTicks, EnumLimbSide side, float inputRot) {
        return inputRot;
    }

    public float preRenderArmRotationZ(IGolemAPI golem, float partialTicks, EnumLimbSide side, float inputRot) {
        return inputRot;
    }
}
