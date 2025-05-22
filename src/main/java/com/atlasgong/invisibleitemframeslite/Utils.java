package com.atlasgong.invisibleitemframeslite;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class Utils {

    private Utils() {
        throw new AssertionError("Utils should not be instantiated.");
    }

    /**
     * Checks if the given ItemStack represents one of this plugin’s invisible item-frame items.
     *
     * @param item           the ItemStack to test; may be null
     * @param isInvisibleKey the NamespacedKey used to mark invisibility in its PersistentDataContainer
     * @return true if the item is non-null, has metadata, and carries the invisibility marker; false otherwise
     */
    public static boolean isInvisibleItemFrame(ItemStack item, NamespacedKey isInvisibleKey) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(isInvisibleKey, PersistentDataType.BYTE);
    }

    /**
     * Checks if the given Entity is an item frame (regular or glowing)
     * that has been marked as invisible by this plugin.
     *
     * @param entity         the Entity to test; may be null or of any type
     * @param isInvisibleKey the NamespacedKey used to mark invisibility in its PersistentDataContainer
     * @return true if the entity is an ItemFrame and carries the invisibility marker; false otherwise
     */
    public static boolean isInvisibleItemFrame(Entity entity, NamespacedKey isInvisibleKey) {
        return entity instanceof ItemFrame && entity.getPersistentDataContainer().has(isInvisibleKey,
                PersistentDataType.BYTE);
    }

    /**
     * Uses reflection to get a post-1.16.1 {@link Material}.
     * <p>
     * If reflection fails unexpectedly, falls back to {@code fallback} as a safeguard,
     * though this scenario should not occur under correct versioned usage.
     *
     * @param material The {@link Material} to attempt to retrieve.
     * @param fallback The fallback {@link Material}, which will be returned on failure.
     * @return The {@link Material} requested on success, or the fallback on failure.
     */
    public static Material getNewMaterial(String material, Material fallback) {
        try {
            Class<?> materialCls = Class.forName("org.bukkit.Material");
            Method valueOf = Enum.class.getDeclaredMethod("valueOf", Class.class, String.class);

            return (Material) valueOf.invoke(null, materialCls, material);

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Material." + material + " not available on this version. Falling " +
                    "back to Material." + fallback.name() + ".", e);
            return fallback;
        }
    }
    
}
