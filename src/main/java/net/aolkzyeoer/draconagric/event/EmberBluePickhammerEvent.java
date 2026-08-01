package net.aolkzyeoer.draconagric.event;


import net.aolkzyeoer.draconagric.item.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;



@EventBusSubscriber(modid = "draconagric")
public class EmberBluePickhammerEvent {



    /**
     * 已蓄力玩家
     */
    private static final Set<UUID> READY_PLAYERS =
            new HashSet<>();



    /**
     * 正在执行范围挖掘玩家
     */
    private static final Set<UUID> MINING_PLAYERS =
            new HashSet<>();





    /**
     * 蓄力检测
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event){


        Player player = event.getEntity();


        if(player.level().isClientSide)
            return;



        UUID uuid = player.getUUID();


        ItemStack stack =
                player.getMainHandItem();



        /*
         * 松开潜行或者换工具
         * 直接取消蓄力
         */
        if(!player.isShiftKeyDown()
                || !stack.is(ModItems.EMBERBLUE_PICKHAMMER.get())){


            READY_PLAYERS.remove(uuid);

            return;
        }




        /*
         * 第一次蓄力
         */
        if(!READY_PLAYERS.contains(uuid)){


            READY_PLAYERS.add(uuid);



            player.level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.RESPAWN_ANCHOR_CHARGE,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );

        }

    }





    /**
     * 方块破坏
     */
    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event){


        Player player =
                event.getPlayer();



        if(player.level().isClientSide)
            return;



        UUID uuid =
                player.getUUID();




        // 防止递归
        if(MINING_PLAYERS.contains(uuid))
            return;




        ItemStack stack =
                player.getMainHandItem();




        if(!stack.is(ModItems.EMBERBLUE_PICKHAMMER.get()))
            return;



        /*
         * 没蓄力
         */
        if(!READY_PLAYERS.contains(uuid))
            return;




        READY_PLAYERS.remove(uuid);



        Level level =
                player.level();



        BlockPos center =
                event.getPos();




        Direction direction =
                getLookingDirection(player);




        BlockPos[] blocks =
                get3x3Blocks(center,direction);




        event.setCanceled(true);



        MINING_PLAYERS.add(uuid);



        int damage = 0;



        try{


            for(BlockPos pos : blocks){


                BlockState state =
                        level.getBlockState(pos);



                // 空气
                if(state.isAir())
                    continue;



                // 基岩保护
                if(state.is(Blocks.BEDROCK))
                    continue;



                // 不可挖
                if(!state.canHarvestBlock(level,pos,player))
                    continue;



                Block.dropResources(
                        state,
                        level,
                        pos,
                        level.getBlockEntity(pos),
                        player,
                        stack
                );



                level.destroyBlock(
                        pos,
                        false
                );


                damage++;


            }



            /*
             * 一次技能扣耐久
             */
            if(damage > 0){


                stack.hurtAndBreak(
                        damage,
                        player,
                        player.getEquipmentSlotForItem(stack)
                );

            }



        }
        finally{


            MINING_PLAYERS.remove(uuid);

        }


    }







    /**
     * 判断玩家视角方向
     */
    private static Direction getLookingDirection(Player player){


        float pitch =
                player.getXRot();



        if(pitch < -45F)
            return Direction.UP;



        if(pitch > 45F)
            return Direction.DOWN;



        return player.getDirection();

    }







    /**
     * 获取3×3区域
     */
    private static BlockPos[] get3x3Blocks(
            BlockPos center,
            Direction direction){


        Set<BlockPos> result =
                new HashSet<>();



        switch(direction){


            case NORTH:
            case SOUTH:


                for(int x=-1;x<=1;x++){

                    for(int y=-1;y<=1;y++){


                        result.add(
                                center.offset(x,y,0)
                        );

                    }
                }

                break;




            case EAST:
            case WEST:


                for(int z=-1;z<=1;z++){

                    for(int y=-1;y<=1;y++){


                        result.add(
                                center.offset(0,y,z)
                        );

                    }
                }

                break;





            case UP:
            case DOWN:


                for(int x=-1;x<=1;x++){

                    for(int z=-1;z<=1;z++){


                        result.add(
                                center.offset(x,0,z)
                        );

                    }
                }

                break;

        }



        return result.toArray(new BlockPos[0]);

    }


}