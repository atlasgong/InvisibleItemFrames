/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package com.atlasgong.invisibleitemframeslite;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class PluginListener implements Listener {
    private final NamespacedKey isInvisibleKey;

    long hangingBrokenAtTick = -1;

    public PluginListener(NamespacedKey isInvisibleKey) {
        this.isInvisibleKey = isInvisibleKey;
    }

    /**
     * Stores a note of the tick an item frame was broken in for when the item
     * drop spawns. Since there's no drops list on the event.
     */
    @EventHandler
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        final Hanging entity = event.getEntity();

        final boolean isFrame = Utils.isInvisibleItemFrame(entity, isInvisibleKey);
        Entity remover = event.getRemover();
        if (remover == null) {
            return;
        }

        if (isFrame) {
            hangingBrokenAtTick = entity.getWorld().getFullTime();
        }
    }

    /**
     * Since there's no connection between HangingBreakByEntityEvent and the items
     * created, this separate handler checks a saved value for whether to turn the
     * dropped item into an invisible item frame.
     */
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        final Item entity = event.getEntity();
        final ItemStack stack = entity.getItemStack();
        final long now = entity.getWorld().getFullTime();
        if (now != hangingBrokenAtTick) {
            return;
        }
        if (stack.getType() == Material.ITEM_FRAME) {
            stack.setItemMeta(InvisibleItemFrames.INVISIBLE_FRAME.getItemMeta());
        } else if (stack.getType() == Material.GLOW_ITEM_FRAME) {
            stack.setItemMeta(InvisibleItemFrames.INVISIBLE_GLOW_FRAME.getItemMeta());
        } else {
            return;
        }
        hangingBrokenAtTick = -1;
        entity.setItemStack(stack);
    }


}
