package com.atlasgong.invisibleitemframeslite.itemframe.versions;

import com.atlasgong.invisibleitemframeslite.Utils;
import com.atlasgong.invisibleitemframeslite.itemframe.ItemFrameFactory;
import org.bukkit.Material;

/**
 * ItemFrameFactory implementation for 1.17 and newer.
 * <p>
 * This class handles the selection of item frame material, supporting the optional use of
 * {@code Material.GLOW_ITEM_FRAME}, which was introduced in 1.17.
 * </p>
 * <p>
 * Uses reflection to safely access the enum constant without causing issues when compiled against older Bukkit APIs.
 * </p>
 */
public class ItemFrameFactory_1_17_Plus extends ItemFrameFactory {

    /**
     * Selects the appropriate item frame material depending on whether the glowing variant is requested.
     *
     * @param glow whether to select a glowing item frame
     * @return the corresponding {@link Material} for the item frame
     */
    @Override
    protected Material selectFrameMaterial(boolean glow) {
        return glow ? Utils.getNewMaterial("GLOW_ITEM_FRAME", Material.ITEM_FRAME) : Material.ITEM_FRAME;
    }
}
