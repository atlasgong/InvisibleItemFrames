package com.atlasgong.invisibleitemframeslite.itemframe.versions;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;

/**
 * ItemFrameFactory implementation for 1.20.5 and newer.
 * <p>
 * Adds support for the {@code ItemMeta#setEnchantmentGlintOverride(Boolean)} API
 * introduced in Minecraft 1.20.5.
 * </p>
 * <p>
 * This class uses reflection to invoke the method, since the plugin is compiled against an older API.
 * If the method is not available at runtime, it falls back to applying a hidden enchantment
 * to simulate the glint effect.
 * </p>
 */
public class ItemFrameFactory_1_20_R5_Plus extends ItemFrameFactory_1_17_Plus {

    /**
     * Applies the glint override API introduced in Minecraft 1.20.5.
     * <p>
     * Uses reflection to invoke {@code ItemMeta#setEnchantmentGlintOverride(Boolean)} to remain compatible
     * with older Bukkit API targets. If reflection fails unexpectedly, falls back to a hidden enchantment
     * to simulate the glint effect, though this scenario should not occur under correct versioned usage.
     * </p>
     *
     * @param enchantmentGlint whether to apply a visual glint effect
     */
    @Override
    protected void configureMeta(ItemMeta meta, NamespacedKey isInvisibleKey, String name, List<String> lore, boolean enchantmentGlint) {

        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.getPersistentDataContainer()
                .set(isInvisibleKey, PersistentDataType.BYTE, (byte) 1);

        // use new glint API introduced in 1.20.5
        try {
            Method glint = meta.getClass().getMethod("setEnchantmentGlintOverride", Boolean.class);
            glint.invoke(meta, enchantmentGlint ? Boolean.TRUE : null);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            Bukkit.getLogger().log(Level.SEVERE, "setEnchantmentGlintOverride not available in this runtime. Falling " +
                    "back to unsafe enchantment workaround.", e);
            if (enchantmentGlint) {
                // fallback glint
                meta.addEnchant(Enchantment.MENDING, 0, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }
    }

}