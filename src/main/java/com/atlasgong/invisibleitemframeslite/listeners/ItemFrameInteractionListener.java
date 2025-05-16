package com.atlasgong.invisibleitemframeslite.listeners;

import com.atlasgong.invisibleitemframeslite.InvisibleItemFramesLite;
import com.atlasgong.invisibleitemframeslite.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Listener that handles interactions and damage events involving invisible item frames.
 * Ensures that the visibility of item frames is updated appropriately based on whether
 * they contain an item.
 */
public class ItemFrameInteractionListener implements Listener {

    /** Key used to tag item frames as invisible via persistent data. */
    private final NamespacedKey isInvisibleKey;


    /**
     * Constructs a new ItemFrameInteractionListener.
     *
     * @param isInvisibleKey The {@link NamespacedKey} used to identify invisible item frames.
     */
    public ItemFrameInteractionListener(NamespacedKey isInvisibleKey) {
        this.isInvisibleKey = isInvisibleKey;
    }


    /**
     * Triggered when a player right-clicks on an entity.
     * If the entity is an invisible item frame, schedules a visibility update
     * to reflect any item changes caused by the interaction (e.g., placing or removing an item).
     */
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        final Entity entity = event.getRightClicked();
        if (!Utils.isInvisibleItemFrame(entity, isInvisibleKey)) {
            return;
        }

        final ItemFrame frame = (ItemFrame) entity;
        new ItemFrameUpdateRunnable(frame).runTask(InvisibleItemFramesLite.INSTANCE);
    }


    /**
     * If the damaged entity is an invisible item frame, schedules a visibility update
     * in case its item was removed (e.g. by a player punching it).
     */
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if (!Utils.isInvisibleItemFrame(entity, isInvisibleKey)) {
            return;
        }

        final ItemFrame frame = (ItemFrame) entity;
        new ItemFrameUpdateRunnable(frame).runTask(InvisibleItemFramesLite.INSTANCE);
    }


    /**
     * A task that updates an invisible item frame’s visibility one tick later,
     * ensuring that any changes to its contents (like items being added or removed)
     * have already taken effect. Invisible item frames become visible when empty,
     * and invisible again when holding an item.
     */
    private static class ItemFrameUpdateRunnable extends BukkitRunnable {
        ItemFrame itemFrame;

        ItemFrameUpdateRunnable(ItemFrame itemFrame) {
            this.itemFrame = itemFrame;
        }

        @Override
        public void run() {
            final ItemStack item = itemFrame.getItem();
            final boolean hasItem = item.getType() != Material.AIR;
            itemFrame.setVisible(!hasItem);
        }
    }

}
