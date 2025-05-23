package com.atlasgong.invisibleitemframeslite.itemframe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.List;


/**
 * Singleton registry responsible for creating and storing custom invisible item frames
 * using a version-specific {@link ItemFrameFactory}.
 * <p>
 * Call {@link #init(ItemFrameFactory)} once at plugin startup to initialize the registry.
 * Access later using {@link #getInstance()}.
 */
public class ItemFrameRegistry {
    private static ItemFrameRegistry instance;

    private final ItemFrameFactory factory;
    private ItemStack regInvisibleFrame;
    private ItemStack glowInvisibleFrame;

    /**
     * Private constructor to enforce singleton pattern.
     *
     * @param factory the item frame factory used to generate item frames
     */
    private ItemFrameRegistry(ItemFrameFactory factory) {
        this.factory = factory;
    }

    /**
     * Initializes the singleton instance of the registry.
     * Should be called only once, typically during plugin startup.
     *
     * @param factory the version-specific factory used to create item frames
     * @throws IllegalStateException if the registry has already been initialized
     */
    public static void init(ItemFrameFactory factory) {
        if (instance != null) {
            throw new IllegalStateException("ItemFrameRegistry is already initialized.");
        }
        instance = new ItemFrameRegistry(factory);
    }

    /**
     * Returns the singleton instance of the registry.
     *
     * @return the initialized {@link ItemFrameRegistry}
     * @throws IllegalStateException if the registry has not been initialized yet
     */
    public static ItemFrameRegistry getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ItemFrameRegistry not initialized. Call init() first.");
        }
        return instance;
    }

    /**
     * Registers the regular (non-glowing) invisible item frame using the factory.
     *
     * @param isInvisibleKey   key used to mark the item frame as invisible
     * @param name             display name of the item
     * @param lore             lore lines for the item
     * @param enchantmentGlint whether to apply an enchantment glint effect
     */
    public void registerRegularInvisibleItemFrame(NamespacedKey isInvisibleKey, String name, List<String> lore,
                                                  boolean enchantmentGlint) {
        regInvisibleFrame = factory.create(isInvisibleKey, name, lore, enchantmentGlint, false);
    }

    /**
     * Registers the glowing invisible item frame using the factory.
     *
     * @param isInvisibleKey   key used to mark the item frame as invisible
     * @param name             display name of the item
     * @param lore             lore lines for the item
     * @param enchantmentGlint whether to apply an enchantment glint effect
     */
    public void registerGlowInvisibleItemFrame(NamespacedKey isInvisibleKey, String name, List<String> lore,
                                               boolean enchantmentGlint) {
        glowInvisibleFrame = factory.create(isInvisibleKey, name, lore, enchantmentGlint, true);
    }

    /**
     * Gets a clone of the registered regular invisible item frame.
     *
     * @return cloned {@link ItemStack} of the regular invisible item frame
     */
    public ItemStack getRegularInvisibleFrame() {
        return regInvisibleFrame.clone();
    }

    /**
     * Gets a clone of the registered glowing invisible item frame.
     *
     * @return cloned {@link ItemStack} of the glowing invisible item frame
     */
    public ItemStack getGlowInvisibleFrame() {
        return glowInvisibleFrame.clone();
    }

}
