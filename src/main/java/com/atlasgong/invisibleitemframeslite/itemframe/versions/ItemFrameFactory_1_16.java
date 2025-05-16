package com.atlasgong.invisibleitemframeslite.itemframe.versions;

import com.atlasgong.invisibleitemframeslite.itemframe.ItemFrameFactory;
import org.bukkit.Material;

public class ItemFrameFactory_1_16 extends ItemFrameFactory {

    @Override
    protected Material selectFrameMaterial(boolean glow) {
        return Material.ITEM_FRAME;
    }

}
