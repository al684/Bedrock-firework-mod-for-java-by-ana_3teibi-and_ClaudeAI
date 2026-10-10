package com.antbasi.bedrockfireworks.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class BedrockFireworksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        KeyBinding openSettings = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.bedrockfireworks.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "key.categories.bedrockfireworks"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openSettings.wasPressed()) {
                client.setScreen(new SettingsScreen(client.currentScreen));
            }
        });
    }
}
