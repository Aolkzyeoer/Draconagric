package net.aolkzyeoer.draconagric.item.tool;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.Vec3;



    public class EmberBluePickhammer extends PickaxeItem {

        public EmberBluePickhammer(Tier tier, Properties properties) {
            // 使用 createAttributes 设置攻击力和攻速
            super(tier, properties.attributes(
                    PickaxeItem.createAttributes(tier, 7, -2.8F)
            ));
        }




    // 强制显示附魔光效
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    // 强制设置附魔能力（如果 Tier 里的附魔能力没生效，这个方法会覆盖）
    @Override
    public int getEnchantmentValue() {
        return 30;
    }

    @Override
    public float getAttackDamageBonus(Entity target, float damage, DamageSource damageSource) {
        Entity attacker = damageSource.getDirectEntity();
        if (!(attacker instanceof LivingEntity livingEntity) || !MaceItem.canSmashAttack(livingEntity)) {
            return 0.0F;
        }

        float fallDistance = livingEntity.fallDistance;
        float smashDamage;
        if (fallDistance <= 3.0F) {
            smashDamage = 4.0F * fallDistance;
        } else if (fallDistance <= 8.0F) {
            smashDamage = 12.0F + 2.0F * (fallDistance - 3.0F);
        } else {
            smashDamage = 22.0F + fallDistance - 8.0F;
        }

        if (livingEntity.level() instanceof ServerLevel serverLevel) {
            smashDamage += EnchantmentHelper.modifyFallBasedDamage(
                    serverLevel,
                    livingEntity.getWeaponItem(),
                    target,
                    damageSource,
                    0.0F
            ) * fallDistance;
        }

        return smashDamage;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);

        if (attacker instanceof ServerPlayer player && MaceItem.canSmashAttack(player)) {
            ServerLevel level = (ServerLevel) player.level();

            if (player.isIgnoringFallDamageFromCurrentImpulse()
                    && player.currentImpulseImpactPos != null
                    && player.currentImpulseImpactPos.y > player.position().y) {
                player.currentImpulseImpactPos = player.position();
            } else {
                player.currentImpulseImpactPos = player.position();
            }

            player.setIgnoreFallDamageFromCurrentImpulse(true);
            player.setDeltaMovement(player.getDeltaMovement().with(Direction.Axis.Y, 0.01D));
            player.connection.send(new ClientboundSetEntityMotionPacket(player));

            SoundEvent sound = target.onGround()
                    ? player.fallDistance > 5.0F ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND
                    : SoundEvents.MACE_SMASH_AIR;

            level.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    sound,
                    player.getSoundSource(),
                    1.0F,
                    1.0F
            );

            knockbackNearby(level, player, target);

            if (player.fallDistance > 10.0F) {
                smashGround(level, player, target, stack, player.fallDistance);
            }
        }

        return result;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHurtEnemy(stack, target, attacker);

        if (MaceItem.canSmashAttack(attacker)) {
            attacker.resetFallDistance();
        }
    }

    private static void knockbackNearby(Level level, LivingEntity attacker, Entity target) {
        Vec3 center = target.position();
        for (LivingEntity entity : level.getEntities(
                EntityTypeTest.forClass(LivingEntity.class),
                target.getBoundingBox().inflate(MaceItem.SMASH_ATTACK_KNOCKBACK_RADIUS),
                entity -> entity != attacker && entity != target && !attacker.isAlliedTo(entity)
        )) {
            Vec3 offset = entity.position().subtract(center);
            double horizontalDistance = offset.horizontalDistance();
            if (horizontalDistance <= 0.0D || horizontalDistance > MaceItem.SMASH_ATTACK_KNOCKBACK_RADIUS) {
                continue;
            }

            double strength = (MaceItem.SMASH_ATTACK_KNOCKBACK_RADIUS - horizontalDistance)
                    / MaceItem.SMASH_ATTACK_KNOCKBACK_RADIUS;
            Vec3 push = offset.normalize().scale(0.7D * strength);
            entity.push(push.x, 0.7D * strength, push.z);
        }
    }

    private static void smashGround(ServerLevel level, ServerPlayer player, Entity target, ItemStack stack, float fallDistance) {
        BlockPos center = target.blockPosition().below();
        int radius = Math.min(5, 2 + (int) ((fallDistance - 10.0F) / 5.0F));
        int broken = 0;

        level.sendParticles(
                ParticleTypes.EXPLOSION,
                target.getX(),
                target.getY(),
                target.getZ(),
                16,
                radius * 0.4D,
                0.2D,
                radius * 0.4D,
                0.05D
        );

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius) {
                    continue;
                }

                BlockPos pos = center.offset(x, 0, z);
                BlockState state = level.getBlockState(pos);
                if (state.isAir() || state.is(Blocks.BEDROCK) || state.getDestroySpeed(level, pos) < 0.0F) {
                    continue;
                }

                level.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, state),
                        pos.getX() + 0.5D,
                        pos.getY() + 1.0D,
                        pos.getZ() + 0.5D,
                        36,
                        0.35D,
                        0.35D,
                        0.35D,
                        0.08D
                );

                Block.dropResources(state, level, pos, level.getBlockEntity(pos), player, stack);
                level.destroyBlock(pos, false);
                broken++;
            }
        }

        if (broken > 0) {
            stack.hurtAndBreak(
                    Math.max(1, broken / 3),
                    player,
                    player.getEquipmentSlotForItem(stack)
            );
        }
    }
}
