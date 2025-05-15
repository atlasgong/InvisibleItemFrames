/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package com.atlasgong.invisibleitemframeslite;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

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

}
