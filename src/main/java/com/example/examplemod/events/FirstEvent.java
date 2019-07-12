package com.example.examplemod.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class FirstEvent {

    private Minecraft mc = Minecraft.getInstance();
    private int interval = 2000;

    @SubscribeEvent
    public void onRenderTick(RenderTickEvent e) {
        FontRenderer fr = mc.fontRenderer;

        // when we are on the world
        if (mc.world != null) {
            int itemsCount = getItemCountInInventory(12);
            int itemStacks = getStacksAmount(itemsCount);
            fr.drawStringWithShadow("Cobble: "+ itemsCount + " (" + itemStacks + ")", 2, 2, -1);

            if (itemStacks >= 9 && this.interval >= 2000) {
                mc.player.sendChatMessage("/cobblex");
                this.interval = 0;
            }
            if (this.interval >= 0 && this.interval != 2000) {
                this.interval++;
            }

        } else {
            fr.drawStringWithShadow("Roxy v1.0.0 by m7rlin ", 2, 2, -1);
        }
    }

    private int getItemCountInInventory(int itemId) {
        int result = 0;

        for (int i = 0; i < Minecraft.getInstance().player.inventory.getSizeInventory(); ++i) {
            Minecraft mc = Minecraft.getInstance();
            PlayerInventory inv = mc.player.inventory;

            if (inv.getStackInSlot(i) != null) {

                ItemStack compareTo = inv.getStackInSlot(i);
                Item compareToItem = compareTo.getItem();
                int compareToId = Item.getIdFromItem(compareToItem);

                /*
                if (compareToId != 0) {
                    System.out.println("Item ID: "+compareToId);
                }
                */

                if (compareToId == itemId) {
                    result += compareTo.getCount();
                }
            }
        }

        return result;
    }

    private int getStacksAmount(int itemCount) {
        int stacks = itemCount / 64;
        return stacks;
    }

}
