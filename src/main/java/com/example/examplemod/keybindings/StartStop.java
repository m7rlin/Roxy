package com.example.examplemod.keybindings;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class StartStop {

    public final static KeyBinding start = new KeyBinding("key.startbot", 332, "roxy.bot"); // *(Numpad)
    public final static KeyBinding stop = new KeyBinding("key.stopbot", 333, "roxy.bot"); // -(Numpad)

    public static void register() {
        ClientRegistry.registerKeyBinding(start);
        ClientRegistry.registerKeyBinding(stop);
    }
}
