/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package com.atlasgong.invisibleitemframeslite;

import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
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


    /**
     * Spigot does not respect the doLimitedCrafting gamerule or allow permission
     * nodes for recipes, this re-adds that functionality.
     */
    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        // event.getRecipe() is not the same object as the Recipe you pass to addRecipe.
        // So this ugly hack is required of checking the craft result for whether it's
        // the item to be crafted.
        if (!Utils.isInvisibleItemFrame(event.getInventory().getResult(), isInvisibleKey)) {
            return;
        }

        // disallow using invisible item frames to craft themselves
        for (ItemStack is : event.getInventory().getMatrix()) {
            if (Utils.isInvisibleItemFrame(is, isInvisibleKey)) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
                return;
            }
        }

        final HumanEntity entity = event.getView().getPlayer();
        final Boolean limitedCrafting = entity.getWorld().getGameRuleValue(GameRule.DO_LIMITED_CRAFTING);
        final boolean entityHasRecipe = entity.hasDiscoveredRecipe(InvisibleItemFrames.RECIPE_KEY);
        if (Boolean.TRUE.equals(limitedCrafting) && !entityHasRecipe) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }
}
