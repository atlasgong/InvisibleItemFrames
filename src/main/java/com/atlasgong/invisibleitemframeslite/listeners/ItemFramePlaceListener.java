package com.atlasgong.invisibleitemframeslite.listeners;

import com.atlasgong.invisibleitemframeslite.Utils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

/**
 * Listener class responsible for handling events related to placing invisible item frames.
 * When a player places an item frame tagged as "invisible", this class ensures that the
 * resulting entity is also marked as invisible using persistent data.
 */
public class ItemFramePlaceListener implements Listener {

    /** Key used to tag item frames as invisible via persistent data. */
    private final NamespacedKey isInvisibleKey;

    /** The location where an invisible item frame is about to be placed. */
    Location aboutToPlaceLocation = null;

    /** The face of the block where the invisible item frame is about to be placed. */
    BlockFace aboutToPlaceFace = null;

    /**
     * Constructs a new ItemFramePlaceListener.
     *
     * @param isInvisibleKey The {@link NamespacedKey} used to identify invisible item frames.
     */
    public ItemFramePlaceListener(NamespacedKey isInvisibleKey) {
        this.isInvisibleKey = isInvisibleKey;
    }

    /**
     * Handles the event triggered when a hanging entity (such as an item frame) is placed.
     * If the player previously attempted to place an invisible item frame, the newly created
     * item frame entity is tagged as invisible using persistent data.
     */
    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
        if (!(event.getEntity() instanceof ItemFrame)) return;
        final Location loc = event.getBlock().getLocation();
        final BlockFace face = event.getBlockFace();

        if (loc.equals(aboutToPlaceLocation) && face == aboutToPlaceFace) {
            aboutToPlaceLocation = null;
            aboutToPlaceFace = null;
            event.getEntity().getPersistentDataContainer().set(isInvisibleKey,
                    PersistentDataType.BYTE, (byte) 1);
        }
    }

    /**
     * Handles the event triggered when a player interacts (right-clicks) to place an item.
     * This method pre-records the intended location and face for the item frame placement
     * if the item used is tagged as an invisible item frame.
     * <p>
     * Note: The {@link HangingPlaceEvent} does not contain information about which item was used,
     * so this method is used to correlate item metadata with the entity placement.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final ItemStack item = event.getItem();

        if (!Utils.isInvisibleItemFrame(item, isInvisibleKey) || event.useItemInHand() == Event.Result.DENY) {
            return;
        }

        final Block block = event.getClickedBlock();
        if (block != null) {
            aboutToPlaceLocation = block.getRelative(event.getBlockFace()).getLocation();
            aboutToPlaceFace = event.getBlockFace();
        }
    }
}
