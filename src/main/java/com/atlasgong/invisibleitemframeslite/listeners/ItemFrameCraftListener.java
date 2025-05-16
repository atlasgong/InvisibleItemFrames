package com.atlasgong.invisibleitemframeslite.listeners;

import com.atlasgong.invisibleitemframeslite.InvisibleItemFramesLite;
import com.atlasgong.invisibleitemframeslite.Utils;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener that controls crafting behavior for invisible item frames.
 * Enforces restrictions based on the "doLimitedCrafting" gamerule and prevents
 * recursive crafting using invisible item frames themselves.
 */
public class ItemFrameCraftListener implements Listener {

    /** Key used to tag item frames as invisible via persistent data. */
    private final NamespacedKey isInvisibleKey;

    /**
     * Constructs a new ItemFrameCraftListener.
     *
     * @param isInvisibleKey The {@link NamespacedKey} used to identify invisible item frames.
     */
    public ItemFrameCraftListener(NamespacedKey isInvisibleKey) {
        this.isInvisibleKey = isInvisibleKey;
    }

    /**
     * Intercepts crafting attempts to enforce custom behavior:
     * - Blocks crafting invisible item frames using existing invisible item frames.
     * - Enforces the "doLimitedCrafting" gamerule for the custom invisible frame recipe,
     * since Spigot doesn't enforce it natively for custom recipes.
     */
    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        // check if crafted result is our invisible item frame
        if (!Utils.isInvisibleItemFrame(event.getInventory().getResult(), isInvisibleKey)) {
            return;
        }

        // disallow recursive crafting with invisible item frames
        for (ItemStack is : event.getInventory().getMatrix()) {
            if (Utils.isInvisibleItemFrame(is, isInvisibleKey)) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
                return;
            }
        }

        // enforce doLimitedCrafting for players who haven’t unlocked the custom recipe.
        final HumanEntity entity = event.getView().getPlayer();
        final Boolean limitedCrafting = entity.getWorld().getGameRuleValue(GameRule.DO_LIMITED_CRAFTING);
        final boolean entityHasRecipe = entity.hasDiscoveredRecipe(InvisibleItemFramesLite.RECIPE_KEY);
        if (Boolean.TRUE.equals(limitedCrafting) && !entityHasRecipe) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }
}
