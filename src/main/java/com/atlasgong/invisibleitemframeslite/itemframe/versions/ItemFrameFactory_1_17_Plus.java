package com.atlasgong.invisibleitemframeslite.itemframe.versions;

import com.atlasgong.invisibleitemframeslite.itemframe.ItemFrameFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

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
     * <p>
     * Uses reflection to access {@code Material.GLOW_ITEM_FRAME} to allow compatibility with older API targets.
     * If reflection fails unexpectedly, falls back to {@code Material.ITEM_FRAME} as a safeguard,
     * though this scenario should not occur under correct versioned usage.
     * </p>
     *
     * @param glow whether to select a glowing item frame
     * @return the corresponding {@link Material} for the item frame
     */
    @Override
    protected Material selectFrameMaterial(boolean glow) {
        try {
            Class<?> materialCls = Class.forName("org.bukkit.Material");
            Method valueOf = Enum.class.getDeclaredMethod("valueOf", Class.class, String.class);
            Material glowItemFrame = (Material) valueOf.invoke(null, materialCls, "GLOW_ITEM_FRAME");

            return glow
                    ? glowItemFrame
                    : Material.ITEM_FRAME;

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Material.GLOW_ITEM_FRAME not available on this version. Falling " +
                    "back to Material.ITEM_FRAME.", e);
            return Material.ITEM_FRAME;
        }
    }
}
