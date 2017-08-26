package org.darkstorm.minecraft.darkbot.util;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import org.darkstorm.minecraft.darkbot.world.item.BasicItemStack;

public class Bridge {
    public static BasicItemStack GetOldItemStack(ItemStack itemStack) {
        if(itemStack == null)
            return null;

        BasicItemStack basicItemStack = new BasicItemStack(itemStack.getId(),itemStack.getAmount(),itemStack.getData());
        basicItemStack.setStackTagCompound(itemStack.getNBT());

        return basicItemStack;
    }

    public static ItemStack GetNewItemStack(org.darkstorm.minecraft.darkbot.world.item.ItemStack itemStack) {
        if(itemStack == null)
            return null;

        ItemStack newItemStack = new ItemStack(itemStack.getId(), itemStack.getStackSize(), itemStack.getDamage(), itemStack.getStackTagCompound());

        return newItemStack;
    }
}
