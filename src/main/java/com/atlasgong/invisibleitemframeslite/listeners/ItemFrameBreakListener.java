package com.atlasgong.invisibleitemframeslite.listeners;

import com.atlasgong.invisibleitemframeslite.Utils;
import com.atlasgong.invisibleitemframeslite.itemframe.ItemFrameRegistry;
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
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Listener that restores invisibility to item frame items when they are broken and dropped.
 * Spigot does not link item drops to the hanging entity directly, so this class tracks
 * the tick when an invisible item frame is broken and modifies the matching drop.
 */
public class ItemFrameBreakListener implements Listener {

    /** Key used to tag item frames as invisible via persistent data. */
    private final NamespacedKey isInvisibleKey;

    /** World tick when an invisible item frame was last broken. Used to identify the drop. */
    long hangingBrokenAtTick = -1;

    /**
     * Constructs a new ItemFrameBreakListener.
     *
     * @param isInvisibleKey The {@link NamespacedKey} used to identify invisible item frames.
     */
    public ItemFrameBreakListener(NamespacedKey isInvisibleKey) {
        this.isInvisibleKey = isInvisibleKey;
    }

    /**
     * Records the current tick if an invisible item frame is broken.
     * This allows tracking when the corresponding dropped item appears,
     * since Spigot doesn't provide drop data in the break event.
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
     * Detects when an item is spawned and checks if it’s the result of an invisible item frame breaking.
     * If the tick matches the previously stored value, applies the correct invisible item frame metadata
     * to preserve its invisibility after dropping.
     */
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        final Item entity = event.getEntity();
        final ItemStack stack = entity.getItemStack();
        final long now = entity.getWorld().getFullTime();

        if (now != hangingBrokenAtTick) {
            return;
        }

        ItemMeta regularInvisibleItemFrameMeta = ItemFrameRegistry
                .getInstance()
                .getRegularInvisibleFrame()
                .getItemMeta();

        ItemMeta glowInvisibleItemFrameMeta = ItemFrameRegistry
                .getInstance()
                .getGlowInvisibleFrame()
                .getItemMeta();

        if (stack.getType() == Material.ITEM_FRAME) {
            stack.setItemMeta(regularInvisibleItemFrameMeta);
        } else if (stack.getType().name().equals("GLOW_ITEM_FRAME")) {
            // use name based check to avoid referencing GLOW_ITEM_FRAME directly
            // which doesn't exist in versions before 1.17. this check will always fail
            // on pre-1.17 versions.
            stack.setItemMeta(glowInvisibleItemFrameMeta);
        } else {
            return;
        }
        hangingBrokenAtTick = -1;
        entity.setItemStack(stack);
    }

}
