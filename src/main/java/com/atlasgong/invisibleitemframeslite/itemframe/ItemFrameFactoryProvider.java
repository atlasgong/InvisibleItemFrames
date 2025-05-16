package com.atlasgong.invisibleitemframeslite.itemframe;

import com.atlasgong.invisibleitemframeslite.itemframe.versions.ItemFrameFactory_1_16;
import com.atlasgong.invisibleitemframeslite.itemframe.versions.ItemFrameFactory_1_17_Plus;
import com.atlasgong.invisibleitemframeslite.itemframe.versions.ItemFrameFactory_1_20_R5_Plus;

public class ItemFrameFactoryProvider {

    private ItemFrameFactoryProvider() {
        throw new AssertionError("ItemFrameFactoryProvider should not be instantiated.");
    }

    public static ItemFrameFactory get(int minor, int patch) {
        if (minor <= 16) {
            return new ItemFrameFactory_1_16();
        } else if (minor < 20 || (minor == 20 && patch < 5)) {
            return new ItemFrameFactory_1_17_Plus();
        } else {
            return new ItemFrameFactory_1_20_R5_Plus();
        }
    }
}
