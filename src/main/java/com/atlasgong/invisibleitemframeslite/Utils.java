package com.atlasgong.invisibleitemframeslite;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Utils {

    /**
     * Returns whether the given ItemStack is an invisible item frame item.
     *
     * @param item The stack to check.
     * @return Whether the item stack is an invisible item frame item.
     */
    public static boolean isInvisibleItemFrame(ItemStack item, NamespacedKey isInvisibleKey) {
        if (item == null) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().has(isInvisibleKey, PersistentDataType.BYTE);
    }

    /**
     * Returns whether the given entity is an item frame
     * which will become invisible when it has an item.
     *
     * @param entity The entity to check.
     * @return Whether the entity is an invisible item frame.
     */
    public static boolean isInvisibleItemFrame(Entity entity, NamespacedKey isInvisibleKey) {
        if (entity == null) {
            return false;
        }
        final EntityType type = entity.getType();
        if (type != EntityType.ITEM_FRAME && type != EntityType.GLOW_ITEM_FRAME) {
            return false;
        }
        return entity.getPersistentDataContainer().has(isInvisibleKey, PersistentDataType.BYTE);
    }

    /**
     * Creates an invisible item frame.
     *
     * @param isInvisibleKey   The namespaced key to store invisibility status.
     * @param name             The configurable display name for the item.
     * @param lore             The configurable optional lore for the item.
     * @param enchantmentGlint Whether the item should have an enchantment glint.
     * @param glow             Whether to create a glow item frame instead of a regular item frame.
     * @return An invisible item frame.
     */
    public static ItemStack createItem(NamespacedKey isInvisibleKey, String name, List<String> lore,
                                       boolean enchantmentGlint,
                                       boolean glow) {
        ItemStack item = new ItemStack(glow ? Material.GLOW_ITEM_FRAME : Material.ITEM_FRAME, 1);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.setEnchantmentGlintOverride(enchantmentGlint ? true : null);
        meta.getPersistentDataContainer().set(isInvisibleKey, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Creates an invisible regular item frame.
     *
     * @param isInvisibleKey   The namespaced key to store invisibility status.
     * @param name             The configurable display name for the item.
     * @param lore             The configurable optional lore for the item.
     * @param enchantmentGlint Whether the item should have an enchantment glint.
     * @return An invisible regular item frame.
     */
    public static ItemStack createItem(NamespacedKey isInvisibleKey, String name, List<String> lore,
                                       boolean enchantmentGlint) {
        return createItem(isInvisibleKey, name, lore, enchantmentGlint, false);
    }

}
