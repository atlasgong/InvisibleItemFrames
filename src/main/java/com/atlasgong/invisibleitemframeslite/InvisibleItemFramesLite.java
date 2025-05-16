/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package com.atlasgong.invisibleitemframeslite;

import com.atlasgong.invisibleitemframeslite.itemframe.ItemFrameFactory;
import com.atlasgong.invisibleitemframeslite.itemframe.ItemFrameFactoryProvider;
import com.atlasgong.invisibleitemframeslite.listeners.ItemFrameBreakListener;
import com.atlasgong.invisibleitemframeslite.listeners.ItemFrameCraftListener;
import com.atlasgong.invisibleitemframeslite.listeners.ItemFrameInteractionListener;
import com.atlasgong.invisibleitemframeslite.listeners.ItemFramePlaceListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class InvisibleItemFramesLite extends JavaPlugin {
    public static InvisibleItemFramesLite INSTANCE;
    public static NamespacedKey RECIPE_KEY;
    public static NamespacedKey GLOW_RECIPE_KEY;
    public static ItemStack INVISIBLE_FRAME;
    public static ItemStack INVISIBLE_GLOW_FRAME;
    private static boolean firstLoad = true;

    private ItemFrameFactory frameFactory;

    @Override
    public void onEnable() {
        INSTANCE = this;

        // declare namespaced keys
        NamespacedKey isInvisibleKey = new NamespacedKey(this, "invisible");
        RECIPE_KEY = new NamespacedKey(this, "invisible_item_frame");
        GLOW_RECIPE_KEY = new NamespacedKey(this, "invisible_glow_item_frame");

        // get version specific item frame factory
        frameFactory = ItemFrameFactoryProvider.get();

        // register listeners
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new ItemFramePlaceListener(isInvisibleKey), this);
        pm.registerEvents(new ItemFrameBreakListener(isInvisibleKey), this);
        pm.registerEvents(new ItemFrameInteractionListener(isInvisibleKey), this);
        pm.registerEvents(new ItemFrameCraftListener(isInvisibleKey), this);

        // load config
        saveDefaultConfig();
        loadConfig(isInvisibleKey);

        firstLoad = false;

        // incl metrics for bStats
        int pluginId = 25837;
        @SuppressWarnings("unused") Metrics metrics = new Metrics(this, pluginId);
    }

    private void addRecipeFromConfig(NamespacedKey key, ConfigurationSection config, ItemStack item) {
        item = item.clone();
        item.setAmount(config.getInt("count"));
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        List<String> shape = config.getStringList("shape");
        recipe.shape(shape.toArray(new String[0]));

        ConfigurationSection ingredients = config.getConfigurationSection("ingredients");
        // If this is null, then the defaults above are incorrect.
        assert ingredients != null;
        for (Map.Entry<String, Object> entry : ingredients.getValues(false).entrySet()) {
            Material material = Material.matchMaterial(entry.getValue().toString());
            if (material == null) {
                getLogger()
                        .severe("Failed to find material " + entry.getValue().toString() + ", recipe might not work.");
                continue;
            }
            recipe.setIngredient(entry.getKey().charAt(0), material);
        }

        try {
            Bukkit.addRecipe(recipe);
        } catch (IllegalStateException ignored) {
            if (firstLoad) {
                getLogger().severe("Failed to add recipe " + config.getName() + ". This is likely an issue in the config");
            } else {
                getLogger().warning("Failed to add recipe " + config.getName() + ", because Spigot doesn't support reloading recipes.");
            }
        }
    }

    public void loadConfig(NamespacedKey isInvisibleKey) {
        final FileConfiguration config = getConfig();

        config.addDefault("items.invisible_item_frame.name", ChatColor.RESET + "Invisible Item Frame");

        config.addDefault("items.invisible_glow_item_frame.name", ChatColor.RESET + "Invisible Glow Item Frame");

        config.addDefault("recipes.invisible_item_frame.count", 8);
        config.addDefault("recipes.invisible_item_frame.glint", true);
        config.addDefault("recipes.invisible_item_frame.shape", Arrays.asList("FFF", "FAF", "FFF"));
        config.addDefault("recipes.invisible_item_frame.ingredients.F", "minecraft:item_frame");
        config.addDefault("recipes.invisible_glow_item_frame.ingredients.A", "minecraft:phantom_membrane");

        config.addDefault("recipes.invisible_glow_item_frame.count", 8);
        config.addDefault("recipes.invisible_item_frame.glint", true);
        config.addDefault("recipes.invisible_glow_item_frame.shape", Arrays.asList("FFF", "FAF", "FFF"));
        config.addDefault("recipes.invisible_glow_item_frame.ingredients.F", "minecraft:glow_item_frame");
        config.addDefault("recipes.invisible_glow_item_frame.ingredients.A", "minecraft:phantom_membrane");


        ConfigurationSection regularItem = config.getConfigurationSection("items.invisible_item_frame");
        assert regularItem != null;
        String rName = regularItem.getString("name");
        List<String> rLore = regularItem.getStringList("lore");
        boolean rEnchantmentGlint = regularItem.getBoolean("enchantment_glint");
        INVISIBLE_FRAME = frameFactory.create(isInvisibleKey, rName, rLore, rEnchantmentGlint, false);

        ConfigurationSection glowItem = config.getConfigurationSection("items.invisible_glow_item_frame");
        assert glowItem != null;
        String gName = glowItem.getString("name");
        List<String> gLore = glowItem.getStringList("lore");
        boolean gEnchantmentGlint = glowItem.getBoolean("enchantment_glint");
        INVISIBLE_GLOW_FRAME = frameFactory.create(isInvisibleKey, gName, gLore, gEnchantmentGlint, true);

        ConfigurationSection regularRecipe = config.getConfigurationSection("recipes.invisible_item_frame");
        assert regularRecipe != null;
        addRecipeFromConfig(RECIPE_KEY, regularRecipe, INVISIBLE_FRAME);

        ConfigurationSection glowRecipe = config.getConfigurationSection("recipes.invisible_glow_item_frame");
        assert glowRecipe != null;
        addRecipeFromConfig(GLOW_RECIPE_KEY, glowRecipe, INVISIBLE_GLOW_FRAME);
    }
}
