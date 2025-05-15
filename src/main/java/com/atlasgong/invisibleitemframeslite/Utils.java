/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package com.atlasgong.invisibleitemframeslite;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

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

}
