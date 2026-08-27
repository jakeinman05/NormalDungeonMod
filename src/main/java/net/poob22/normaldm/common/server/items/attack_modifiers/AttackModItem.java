package net.poob22.normaldm.common.server.items.attack_modifiers;

import net.minecraft.world.entity.Entity;
import net.poob22.normaldm.common.server.items.stat_modifiers.StatItem;

public abstract class AttackModItem extends StatItem implements IAttackModifier {
    public AttackModItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public abstract void doEffectOn(Entity entity);
}
