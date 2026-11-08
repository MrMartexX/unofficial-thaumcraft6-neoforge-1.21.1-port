package thaumcraft.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

/**
 * Key mappings for Thaumcraft (F for caster focus change, G for misc toggle).
 */
public final class TCKeyMappings {
    public static final String KEY_CATEGORY = "key.categories.thaumcraft";

    public static final KeyMapping KEY_CHANGE_FOCUS = new KeyMapping(
            "key.thaumcraft.change_focus",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F,
            KEY_CATEGORY
    );

    public static final KeyMapping KEY_MISC_TOGGLE = new KeyMapping(
            "key.thaumcraft.misc_toggle",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            KEY_CATEGORY
    );

    private TCKeyMappings() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(TCKeyMappings::onRegisterKeyMappings);
    }

    private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KEY_CHANGE_FOCUS);
        event.register(KEY_MISC_TOGGLE);
    }
}
