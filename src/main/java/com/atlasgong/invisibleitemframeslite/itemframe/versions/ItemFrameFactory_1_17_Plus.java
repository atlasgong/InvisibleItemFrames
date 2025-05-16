package com.atlasgong.invisibleitemframeslite.itemframe.versions;

import com.atlasgong.invisibleitemframeslite.itemframe.ItemFrameFactory;
import org.bukkit.Material;

public class ItemFrameFactory_1_17_Plus extends ItemFrameFactory {

    @Override
    protected Material selectFrameMaterial(boolean glow) {
        return glow
                ? Material.GLOW_ITEM_FRAME
                : Material.ITEM_FRAME;
    }
}
