package com.evandev.reliable_backpacks.client.integration;

import com.evandev.reliable_backpacks.config.ModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClothConfigIntegration {

    public static Screen createScreen(Screen parent) {
        ModConfig config = ModConfig.get();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.reliable_backpacks.title"));

        builder.setSavingRunnable(ModConfig::save);

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.reliable_backpacks.category.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.reliable_backpacks.enable_accessories_integration"), config.enableAccessoriesIntegration)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.enableAccessoriesIntegration = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.reliable_backpacks.enable_curios_integration"), config.enableCuriosIntegration)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.enableCuriosIntegration = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.reliable_backpacks.enable_trinkets_integration"), config.enableTrinketsIntegration)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.enableTrinketsIntegration = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.reliable_backpacks.enable_entity_stealing"), config.enableEntityStealing)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.enableEntityStealing = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.reliable_backpacks.enable_chest_slot"), config.enableChestSlot)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.enableChestSlot = newValue)
                .build());

        general.addEntry(entryBuilder.startIntSlider(Component.translatable("config.reliable_backpacks.backpack_rows"), config.backpackRows, 1, 6)
                .setDefaultValue(3)
                .setSaveConsumer(newValue -> config.backpackRows = newValue)
                .build());

        return builder.build();
    }
}