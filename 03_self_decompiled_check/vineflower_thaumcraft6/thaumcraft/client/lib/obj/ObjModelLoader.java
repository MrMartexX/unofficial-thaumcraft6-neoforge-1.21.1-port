package thaumcraft.client.lib.obj;

import net.minecraft.util.ResourceLocation;

public class ObjModelLoader implements IModelCustomLoader {
   private static final String[] types = new String[]{"obj"};

   @Override
   public String getType() {
      return "OBJ model";
   }

   @Override
   public String[] getSuffixes() {
      return types;
   }

   @Override
   public IModelCustom loadInstance(ResourceLocation resource) throws WavefrontObject.ModelFormatException {
      return new WavefrontObject(resource);
   }
}
