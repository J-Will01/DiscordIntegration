package de.erdbeerbaerlp.dcintegration.architectury.util;

import net.minecraft.world.item.ItemStack;

public class TooltipUtils {
    public static boolean showsInTooltip(ItemStack stack) {
        // TooltipDisplay API doesn't exist in Minecraft 1.21.1
        // In 1.21.1, items always show in tooltip, so return true
        // This API was added in later 1.21.x versions
        return true;
    }
}