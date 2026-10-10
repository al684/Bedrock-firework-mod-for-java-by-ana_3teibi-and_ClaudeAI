package com.antbasi.bedrockfireworks.client;

import com.antbasi.bedrockfireworks.BedrockFireworks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public class SettingsScreen extends Screen {

    private final Screen parent;

    public SettingsScreen(Screen parent) {
        super(Text.translatable("bedrockfireworks.screen.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int w = 260;
        int x = this.width / 2 - w / 2;
        int y = this.height / 6 + 10;
        int step = 26;

        addDrawableChild(toggle(x, y, w, "bedrockfireworks.enabled",
                () -> BedrockFireworks.enabled, v -> BedrockFireworks.enabled = v));

        addDrawableChild(toggle(x, y + step, w, "bedrockfireworks.snap",
                () -> BedrockFireworks.snapDirection, v -> BedrockFireworks.snapDirection = v));

        addDrawableChild(new ValueSlider(x, y + step * 2, w, "bedrockfireworks.boostSpeed",
                0.5, 3.0, BedrockFireworks.boostSpeed, v -> BedrockFireworks.boostSpeed = v));

        addDrawableChild(new ValueSlider(x, y + step * 3, w, "bedrockfireworks.horizontal",
                1.0, 1.5, BedrockFireworks.horizontalMultiplier, v -> BedrockFireworks.horizontalMultiplier = v));

        addDrawableChild(new ValueSlider(x, y + step * 4, w, "bedrockfireworks.vertical",
                0.0, 0.1, BedrockFireworks.verticalAcceleration, v -> BedrockFireworks.verticalAcceleration = v));

        addDrawableChild(ButtonWidget.builder(Text.translatable("bedrockfireworks.reset"), b -> {
            BedrockFireworks.resetToDefaults();
            // أعد بناء الشاشة عشان تتحدث القيم المعروضة
            this.client.setScreen(new SettingsScreen(this.parent));
        }).dimensions(x, y + step * 5 + 10, w / 2 - 2, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.translatable("bedrockfireworks.done"), b -> close())
                .dimensions(x + w / 2 + 2, y + step * 5 + 10, w / 2 - 2, 20).build());
    }

    private ButtonWidget toggle(int x, int y, int w, String key, BooleanSupplier get, Consumer<Boolean> set) {
        return ButtonWidget.builder(toggleLabel(key, get.getAsBoolean()), b -> {
            set.accept(!get.getAsBoolean());
            b.setMessage(toggleLabel(key, get.getAsBoolean()));
        }).dimensions(x, y, w, 20).build();
    }

    private Text toggleLabel(String key, boolean on) {
        return Text.translatable(key, Text.translatable(on ? "bedrockfireworks.on" : "bedrockfireworks.off"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 6 - 14, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        BedrockFireworks.save();
        this.client.setScreen(this.parent);
    }

    /** شريط تمرير يعرض القيمة الحقيقية ويحفظها مقرّبة لـ 3 خانات. */
    private static class ValueSlider extends SliderWidget {
        private final String key;
        private final double min;
        private final double max;
        private final DoubleConsumer setter;

        ValueSlider(int x, int y, int w, String key, double min, double max, double current, DoubleConsumer setter) {
            super(x, y, w, 20, Text.empty(), (current - min) / (max - min));
            this.key = key;
            this.min = min;
            this.max = max;
            this.setter = setter;
            updateMessage();
        }

        private double real() {
            return Math.round((min + this.value * (max - min)) * 1000.0) / 1000.0;
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.translatable(key, String.format(Locale.ROOT, "%.3f", real())));
        }

        @Override
        protected void applyValue() {
            setter.accept(real());
        }
    }
}
