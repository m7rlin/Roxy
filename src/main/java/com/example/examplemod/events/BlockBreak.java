package com.example.examplemod.events;

import com.example.examplemod.keybindings.StartStop;
import net.minecraft.client.Minecraft;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;

import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import net.minecraft.util.text.StringTextComponent;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class BlockBreak {

    private Minecraft mc = Minecraft.getInstance();

    KeyBinding forward = mc.gameSettings.keyBindForward;
    KeyBinding back = mc.gameSettings.keyBindBack;
    KeyBinding right = mc.gameSettings.keyBindRight;
    KeyBinding left = mc.gameSettings.keyBindLeft;

    KeyBinding attack = mc.gameSettings.keyBindAttack;
    KeyBinding defense = mc.gameSettings.keyBindUseItem;


    int turnInterval = 200;
    boolean turned = false;

    boolean directionForward = true;

    boolean work = false;

    boolean firstStart = true;

    boolean torchShouldBePlaced = false;

    double posXStart = 0;
    double posZStart = 0;

    double posXStop = 0;
    double posZStop = 0;


    // not working on server!!!!
    /*@SubscribeEvent
    public void onTorchPichup(EntityItemPickupEvent e) {

        PlayerEntity p = e.getEntityPlayer();
        ItemEntity item = e.getItem();
        p.sendMessage(e.getItem().getName());

        if (work) {
            work = false;

            // the item is torch
            if (item.getItem().getItem() == Items.TORCH) {
                this.stoptMining();
                this.moveStop();


                this.setPositionDown();
                KeyBinding.setKeyBindState(defense.getKey(), true);
                torchShouldBePlaced = true;
            }
            //e.getEntityPlayer().sendMessage(new StringTextComponent("TORCH pickedUp"));
        }
    }*/

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent e) {
        //e.getEntityPlayer().sendMessage(new StringTextComponent("Interaction"));
        if (torchShouldBePlaced) {
            PlayerEntity p = e.getEntityPlayer();
            Vec3d pos = p.getPositionVector();

            double x = pos.x;
            double y = pos.y;
            double z = pos.z;

            BlockPos playerBlockDown = new BlockPos(x, y, z);
            if (Item.getIdFromItem(e.getWorld().getBlockState(playerBlockDown).getBlock().asItem()) == Item.getIdFromItem(Items.TORCH)) {
                KeyBinding.setKeyBindState(defense.getKey(), false);
                work = true;
                firstStart = true;
                torchShouldBePlaced = false;
                //e.getEntityPlayer().sendMessage(new StringTextComponent("Enabled"));
            }

            //e.getEntityPlayer().sendMessage(e.getWorld().getBlockState(playerBlockDown).getBlock().asItem().getName());
        }
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent e) {
        PlayerEntity p = mc.player;

        if (StartStop.start.isKeyDown()) {
            work = true;

            p.sendMessage(new StringTextComponent("Roxy has been `enabled`."));
        }

        if (StartStop.stop.isKeyDown()) {
            work = false;

            this.stoptMining();
            this.moveStop();
            firstStart = true;
            torchShouldBePlaced = false;
            p.sendMessage(new StringTextComponent("Roxy has been `disabled`."));
        }

    }

    @SubscribeEvent
    public void onBlockBreak(TickEvent.PlayerTickEvent e) {

        if (e.phase.equals(TickEvent.Phase.START)) {

            PlayerEntity p = e.player;
            Vec3d pos = p.getPositionVector();

            BlockPos blockPos = new BlockPos(pos.x,pos.y,pos.z);

            int lightLevel = p.world.getLight(blockPos);

            //p.sendMessage(new StringTextComponent(Integer.toString(lightLevel)));



            if (firstStart && work) {
                this.setPositionMining();
                this.startMining();
                this.moveForward();
                firstStart = false;
            }


            if (work) {

                // the light level of the spawning block must be 7 or darker (with exception during thunderstorms), and more light increases the chance that the spawn will fail
                if (lightLevel < 8) {
                    work = false;
                    this.stoptMining();
                    this.moveStop();


                    this.setPositionDown();
                    KeyBinding.setKeyBindState(defense.getKey(), true);
                    torchShouldBePlaced = true;
                    return;
                }


                if (pos.x <= 800.5 && pos.x >= 800) {
                    // turn left
                    this.positiveZ();
                    directionForward = false;
                    turned = true;
                } else if (pos.x >= 899.5) {
                    // turn right
                    this.positiveZ();
                    directionForward = true;
                    turned = true;
                }


                if (turned) {
                    double x = pos.x;
                    double y = pos.y;
                    double z = pos.z;

                    double blockFail = 0.5;

                    // blocks when in the front wall
                    BlockPos posBlockDown = new BlockPos(x+1,y,z-blockFail); // left block - down
                    BlockPos posBlockUp = new BlockPos(x+1,y+1,z-blockFail); // left block - up


                    if (directionForward) {
                        // blocks when in the back wall
                        posBlockDown = new BlockPos(x-1,y,z - blockFail); // right block - down
                        posBlockUp = new BlockPos(x-1,y+1,z - blockFail); // right block - up
                    }

                    int leftBlockDown = Item.getIdFromItem(mc.world.getBlockState(posBlockDown).getBlock().asItem());
                    int leftBlockUp = Item.getIdFromItem(mc.world.getBlockState(posBlockUp).getBlock().asItem());

                    System.out.println("Block on left: " + leftBlockDown + " | " + leftBlockUp);

                    // is stone or cobblestone
                    if (leftBlockDown == 12 || leftBlockUp == 12 || leftBlockUp == 1 || leftBlockDown == 1) {
                        if (directionForward) {
                            // move forward
                            this.negativeX();
                        } else {
                            // move back
                            this.positiveX();
                        }

                        turned = false;
                    }

                    this.moveForward();
                    this.startMining();


                }


            }

        }

    }


    private void moveStop() {
        // move forward
        KeyBinding.setKeyBindState(forward.getKey(), false);

        // turn off other directions
        KeyBinding.setKeyBindState(back.getKey(), false);
        KeyBinding.setKeyBindState(right.getKey(), false);
        KeyBinding.setKeyBindState(left.getKey(), false);
    }

    private void moveForward() {
        // move forward
        KeyBinding.setKeyBindState(forward.getKey(), true);

        // turn off other directions
        KeyBinding.setKeyBindState(back.getKey(), false);
        KeyBinding.setKeyBindState(right.getKey(), false);
        KeyBinding.setKeyBindState(left.getKey(), false);
    }

    private void moveBack() {
        // move back
        KeyBinding.setKeyBindState(back.getKey(), true);

        // turn off other directions
        KeyBinding.setKeyBindState(forward.getKey(), false);
        KeyBinding.setKeyBindState(right.getKey(), false);
        KeyBinding.setKeyBindState(left.getKey(), false);
    }

    private void moveRight() {
        // move right
        KeyBinding.setKeyBindState(right.getKey(), true);

        // turn off other directions
        KeyBinding.setKeyBindState(back.getKey(), false);
        KeyBinding.setKeyBindState(forward.getKey(), false);
        KeyBinding.setKeyBindState(left.getKey(), false);
    }

    private void moveLeft() {
        // move left
        KeyBinding.setKeyBindState(left.getKey(), true);

        // turn off other directions
        KeyBinding.setKeyBindState(back.getKey(), false);
        KeyBinding.setKeyBindState(right.getKey(), false);
        KeyBinding.setKeyBindState(forward.getKey(), false);
    }

    private void startMining() {
        KeyBinding.setKeyBindState(attack.getKey(), true);
    }

    private void stoptMining() {
        KeyBinding.setKeyBindState(attack.getKey(), false);
    }

    private void setPositionMining() {
        PlayerEntity p = mc.player;
        // set yaw= 0 pitch= 90
        p.setLocationAndAngles(p.getPositionVector().x,p.getPositionVector().y,p.getPositionVector().z,p.getPitchYaw().y,35);
    }

    private void setPositionDown() {
        PlayerEntity p = mc.player;
        // set yaw= 0 pitch= 90
        p.setLocationAndAngles(p.getPositionVector().x,p.getPositionVector().y,p.getPositionVector().z,p.getPitchYaw().y,90);

    }


    private void turnRight() {
        PlayerEntity p = mc.player;
        float pitch = p.getPitchYaw().y;
        if (pitch > 90f) {
            pitch = -179.9f + (90 - pitch);
        } else {
            pitch += 90;
        }
        p.setLocationAndAngles(p.getPositionVector().x,p.getPositionVector().y,p.getPositionVector().z, pitch, p.getPitchYaw().x);
    }

    private void negativeZ() {
        PlayerEntity p = mc.player;
        float pitch = p.getPitchYaw().x;
        float yaw = -179.9f;
        p.setLocationAndAngles(p.getPositionVector().x,p.getPositionVector().y,p.getPositionVector().z, yaw, pitch);
    }

    private void positiveZ() {
        PlayerEntity p = mc.player;
        float pitch = p.getPitchYaw().x;
        float yaw = 0;
        p.setLocationAndAngles(p.getPositionVector().x,p.getPositionVector().y,p.getPositionVector().z, yaw, pitch);
    }

    private void positiveX() {
        PlayerEntity p = mc.player;
        float pitch = p.getPitchYaw().x;
        float yaw = -90;
        p.setLocationAndAngles(p.getPositionVector().x,p.getPositionVector().y,p.getPositionVector().z, yaw, pitch);
    }

    private void negativeX() {
        PlayerEntity p = mc.player;
        float pitch = p.getPitchYaw().x;
        float yaw = 90;
        p.setLocationAndAngles(p.getPositionVector().x,p.getPositionVector().y,p.getPositionVector().z, yaw, pitch);
    }

    private void turnLeft() {
        PlayerEntity p = mc.player;
        float pitch = p.getPitchYaw().y;
        if (pitch <= -90f) {
            pitch = 180f - (Math.abs(pitch) - 180);
        } else {
            pitch -= 90;
        }
        p.setLocationAndAngles(p.getPositionVector().x,p.getPositionVector().y,p.getPositionVector().z, pitch, p.getPitchYaw().x);
    }



}


