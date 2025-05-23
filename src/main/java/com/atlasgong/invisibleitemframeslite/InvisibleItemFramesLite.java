/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package com.atlasgong.invisibleitemframeslite;

import com.atlasgong.invisibleitemframeslite.itemframe.ItemFrameFactory;
import com.atlasgong.invisibleitemframeslite.itemframe.ItemFrameFactoryProvider;
import com.atlasgong.invisibleitemframeslite.itemframe.ItemFrameRegistry;
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
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public final class InvisibleItemFramesLite extends JavaPlugin {

    public static InvisibleItemFramesLite INSTANCE;
    private static boolean firstLoad = true;

    private final NamespacedKey IS_INVISIBLE_KEY;

    private final NamespacedKey RECIPE_KEY;
    private NamespacedKey GLOW_RECIPE_KEY;
    private NamespacedKey GLOW_SHAPELESS_RECIPE_KEY;

    public InvisibleItemFramesLite() {
        INSTANCE = this;
        IS_INVISIBLE_KEY = new NamespacedKey(this, "invisible");
        RECIPE_KEY = new NamespacedKey(this, "invisible_item_frame");
    }

    @Override
    public void onEnable() {
        Version sv = getServerVersion();
        getLogger().log(Level.INFO, "Detected server running on " + sv.minor + "," + sv.patch);

        // declare namespaced keys for glow items
        if (sv.minor >= 17) {
            GLOW_RECIPE_KEY = new NamespacedKey(this, "invisible_glow_item_frame");
            GLOW_SHAPELESS_RECIPE_KEY = new NamespacedKey(this, "invisible_glow_item_frame_shapeless");
        }

        // instantiate registry
        ItemFrameFactory versionSpecificFactory = ItemFrameFactoryProvider.get(sv.minor, sv.patch);
        ItemFrameRegistry.init(versionSpecificFactory);

        // register listeners
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new ItemFramePlaceListener(IS_INVISIBLE_KEY), this);
        pm.registerEvents(new ItemFrameBreakListener(IS_INVISIBLE_KEY), this);
        pm.registerEvents(new ItemFrameInteractionListener(IS_INVISIBLE_KEY), this);
        pm.registerEvents(new ItemFrameCraftListener(IS_INVISIBLE_KEY), this);

        // load config and register recipes
        saveDefaultConfig();
        loadConfig(IS_INVISIBLE_KEY);

        if (sv.minor >= 17) {
            // register shapeless glow item frame recipe
            registerShapelessGlowRecipe(GLOW_SHAPELESS_RECIPE_KEY);
        }

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

    /**
     * Adds a shapeless recipe to allow crafting an invisible item frame
     * with a glow ink sac to make an invisible glow item frame.
     *
     * @param key plugin-scoped identifier for the recipe
     */
    private void registerShapelessGlowRecipe(NamespacedKey key) {
        ItemStack glowIs = ItemFrameRegistry.getInstance().getGlowInvisibleFrame();
        ItemStack regIs = ItemFrameRegistry.getInstance().getRegularInvisibleFrame();

        ShapelessRecipe recipe = new ShapelessRecipe(key, glowIs.clone());
        recipe.addIngredient(Utils.getNewMaterial("GLOW_INK_SAC", Material.INK_SAC));

        //noinspection deprecation, undeprecated in 1.16.5
        recipe.addIngredient(new RecipeChoice.ExactChoice(regIs.clone()));

        Bukkit.addRecipe(recipe);
    }

    public void loadConfig(NamespacedKey IS_INVISIBLE_KEY) {
        final FileConfiguration config = getConfig();

        config.addDefault("items.invisible_item_frame.name", ChatColor.RESET + "Invisible Item Frame");
        config.addDefault("recipes.invisible_item_frame.count", 8);
        config.addDefault("recipes.invisible_item_frame.glint", true);
        config.addDefault("recipes.invisible_item_frame.shape", Arrays.asList("FFF", "FAF", "FFF"));
        config.addDefault("recipes.invisible_item_frame.ingredients.F", "minecraft:item_frame");
        config.addDefault("recipes.invisible_item_frame.ingredients.A", "minecraft:phantom_membrane");

        ConfigurationSection regularItem = config.getConfigurationSection("items.invisible_item_frame");
        assert regularItem != null;
        String rName = regularItem.getString("name");
        List<String> rLore = regularItem.getStringList("lore");
        boolean rEnchantmentGlint = regularItem.getBoolean("enchantment_g" +
                "lint");

        // register frame with registry
        ItemFrameRegistry.getInstance().registerRegularInvisibleItemFrame(IS_INVISIBLE_KEY, rName, rLore, rEnchantmentGlint);

        ConfigurationSection regularRecipe = config.getConfigurationSection("recipes.invisible_item_frame");
        assert regularRecipe != null;
        addRecipeFromConfig(RECIPE_KEY, regularRecipe, ItemFrameRegistry.getInstance().getRegularInvisibleFrame());

        // add glow item frame only if on versions 1.17+
        if (getServerVersion().minor >= 17) {
            config.addDefault("items.invisible_glow_item_frame.name", ChatColor.RESET + "Invisible Glow Item Frame");
            config.addDefault("recipes.invisible_glow_item_frame.count", 8);
            config.addDefault("recipes.invisible_glow_item_frame.glint", true);
            config.addDefault("recipes.invisible_glow_item_frame.shape", Arrays.asList("FFF", "FAF", "FFF"));
            config.addDefault("recipes.invisible_glow_item_frame.ingredients.F", "minecraft:glow_item_frame");
            config.addDefault("recipes.invisible_glow_item_frame.ingredients.A", "minecraft:phantom_membrane");

            ConfigurationSection glowItem = config.getConfigurationSection("items.invisible_glow_item_frame");
            assert glowItem != null;
            String gName = glowItem.getString("name");
            List<String> gLore = glowItem.getStringList("lore");
            boolean gEnchantmentGlint = glowItem.getBoolean("enchantment_glint");

            // register frame with registry
            ItemFrameRegistry.getInstance().registerGlowInvisibleItemFrame(IS_INVISIBLE_KEY, gName, gLore, gEnchantmentGlint);

            ConfigurationSection glowRecipe = config.getConfigurationSection("recipes.invisible_glow_item_frame");
            assert glowRecipe != null;
            addRecipeFromConfig(GLOW_RECIPE_KEY, glowRecipe, ItemFrameRegistry.getInstance().getGlowInvisibleFrame());
        }
    }

    private Version getServerVersion() {
        String[] parts = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        int minor = Integer.parseInt(parts[1]);
        int patch = Integer.parseInt(parts[2]);
        return new Version(minor, patch);
    }

    private static class Version {
        final int minor, patch;

        Version(int minor, int patch) {
            this.minor = minor;
            this.patch = patch;
        }
    }

}
