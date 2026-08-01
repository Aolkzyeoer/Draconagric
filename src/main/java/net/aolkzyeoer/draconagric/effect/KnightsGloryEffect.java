package net.aolkzyeoer.draconagric.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class KnightsGloryEffect extends MobEffect {

    public KnightsGloryEffect() {
        // 简单注册,0xFFDD700是金色传说,增益(
        super(MobEffectCategory.BENEFICIAL, 0xFFD700);
    }
}
