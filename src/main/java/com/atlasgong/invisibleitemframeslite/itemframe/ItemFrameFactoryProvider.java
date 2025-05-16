package com.atlasgong.invisibleitemframeslite.itemframe;

import com.atlasgong.invisibleitemframeslite.itemframe.versions.ItemFrameFactory_1_16;
import com.atlasgong.invisibleitemframeslite.itemframe.versions.ItemFrameFactory_1_17_Plus;
import com.atlasgong.invisibleitemframeslite.itemframe.versions.ItemFrameFactory_1_20_R5_Plus;
import org.bukkit.Bukkit;

public class ItemFrameFactoryProvider {

    private ItemFrameFactoryProvider() {
        throw new AssertionError("ItemFrameFactoryProvider should not be instantiated.");
    }

    public static ItemFrameFactory get() {
        String ver = Bukkit.getServer()
                .getClass()
                .getPackage()
                .getName()
                .split("\\.")[3]; // e.g. "v1_20_R2"
        String[] p = ver.replace("v1_", "").split("_R");
        int minor = Integer.parseInt(p[0]);
        int patch = Integer.parseInt(p[1]);

        if (minor == 16) {
            return new ItemFrameFactory_1_16();
        } else if (minor >= 17 && (minor < 20 || patch < 5)) {
            return new ItemFrameFactory_1_17_Plus();
        } else {
            return new ItemFrameFactory_1_20_R5_Plus();
        }
    }
}
