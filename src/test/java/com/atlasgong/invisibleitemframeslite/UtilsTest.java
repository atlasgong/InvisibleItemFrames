package com.atlasgong.invisibleitemframeslite;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.atlasgong.invisibleitemframeslite.itemframe.ItemFrameRegistry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class UtilsTest {

    private ServerMock server;
    private InvisibleItemFramesLite plugin;
    private NamespacedKey isInvisibleKey;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(InvisibleItemFramesLite.class);
        isInvisibleKey = plugin.getInvisibleKey();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();

        // bypass singleton enforcement
        try {
            Field field = ItemFrameRegistry.class.getDeclaredField("instance");
            field.setAccessible(true);
            field.set(null, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testIsInvisibleItemFrameWithItemStack() {
        ItemStack is = ItemFrameRegistry.getInstance().getRegularInvisibleFrame();
        assert Utils.isInvisibleItemFrame(is, isInvisibleKey);
    }

    @Disabled("TODO")
    @Test
    void testIsInvisibleItemFrameWithEntity() {
        World w = server.createWorld(new WorldCreator("world"));
        assert w != null;
        Location loc = new Location(w, 0, 64, 0);
        Block b = w.getBlockAt(loc);
        b.setType(Material.STONE);
        PlayerMock p = server.addPlayer();
        w.spawnEntity(new Location(w, 0, 64, 0), EntityType.ITEM_FRAME);

    }

    @Disabled("TODO")
    @Test
    void getNewMaterial() {

    }
}