package com.antbasi.bedrockfireworks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class BedrockFireworks implements ModInitializer {

    // القيم الافتراضية (نفس قيم الجافا الأصلية للصاروخ، والدفع الفوري مفعّل)
    public static final boolean DEF_ENABLED = true;
    public static final boolean DEF_SNAP = true;
    public static final double DEF_BOOST_SPEED = 1.7;
    public static final double DEF_HORIZONTAL = 1.15;
    public static final double DEF_VERTICAL = 0.04;

    public static volatile boolean enabled = DEF_ENABLED;
    /** true = velocity snaps to look direction instantly (no slide, no ramp-up). */
    public static volatile boolean snapDirection = DEF_SNAP;
    /** Constant boost speed in blocks/tick. 1.7 is about vanilla's steady-state top speed (~34 b/s). */
    public static volatile double boostSpeed = DEF_BOOST_SPEED;
    /** Rocket entity's own motion. Vanilla Java = 1.15 */
    public static volatile double horizontalMultiplier = DEF_HORIZONTAL;
    /** Rocket entity's own motion. Vanilla Java = 0.04 */
    public static volatile double verticalAcceleration = DEF_VERTICAL;

    private static Path configFile() {
        return FabricLoader.getInstance().getConfigDir().resolve("bedrockfireworks.properties");
    }

    public static void resetToDefaults() {
        enabled = DEF_ENABLED;
        snapDirection = DEF_SNAP;
        boostSpeed = DEF_BOOST_SPEED;
        horizontalMultiplier = DEF_HORIZONTAL;
        verticalAcceleration = DEF_VERTICAL;
    }

    public static void load() {
        Path file = configFile();
        if (!Files.exists(file)) {
            save();
            return;
        }
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(file)) {
            p.load(in);
            enabled = Boolean.parseBoolean(p.getProperty("enabled", String.valueOf(DEF_ENABLED)));
            snapDirection = Boolean.parseBoolean(p.getProperty("snapDirection", String.valueOf(DEF_SNAP)));
            boostSpeed = Double.parseDouble(p.getProperty("boostSpeed", String.valueOf(DEF_BOOST_SPEED)));
            horizontalMultiplier = Double.parseDouble(p.getProperty("horizontalMultiplier", String.valueOf(DEF_HORIZONTAL)));
            verticalAcceleration = Double.parseDouble(p.getProperty("verticalAcceleration", String.valueOf(DEF_VERTICAL)));
        } catch (IOException | NumberFormatException e) {
            System.err.println("[BedrockFireworks] config problem, using defaults: " + e);
            resetToDefaults();
        }
    }

    public static void save() {
        Properties p = new Properties();
        p.setProperty("enabled", String.valueOf(enabled));
        p.setProperty("snapDirection", String.valueOf(snapDirection));
        p.setProperty("boostSpeed", String.valueOf(boostSpeed));
        p.setProperty("horizontalMultiplier", String.valueOf(horizontalMultiplier));
        p.setProperty("verticalAcceleration", String.valueOf(verticalAcceleration));
        try (OutputStream out = Files.newOutputStream(configFile())) {
            p.store(out, "Bedrock Fireworks");
        } catch (IOException e) {
            System.err.println("[BedrockFireworks] could not save config: " + e);
        }
    }

    @Override
    public void onInitialize() {
        load();
    }
}
