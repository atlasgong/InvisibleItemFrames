package com.atlasgong.invisibleitemframeslite.itemframe.versions;

import com.atlasgong.invisibleitemframeslite.itemframe.ItemFrameFactory;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ItemFrameFactory_1_20_R5_Plus extends ItemFrameFactory {

    @Override
    protected Material selectFrameMaterial(boolean glow) {
        return glow
                ? Material.GLOW_ITEM_FRAME
                : Material.ITEM_FRAME;
    }

    @Override
    protected void configureMeta(ItemMeta meta, NamespacedKey isInvisibleKey, String name, List<String> lore, boolean enchantmentGlint) {

        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.getPersistentDataContainer()
                .set(isInvisibleKey, PersistentDataType.BYTE, (byte) 1);

        // use new glint API
        meta.setEnchantmentGlintOverride(enchantmentGlint ? true : null);
    }
}