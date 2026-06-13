package net.poob22.normaldm.common.server.combat;

public class AttackDefinition {
    AttackType type;
    int setCooldown;
    float attackRange;

    public AttackDefinition(AttackType type, int setCooldown, float attackRange) {
        this.type = type;
        this.setCooldown = setCooldown;
        this.attackRange = attackRange;
    }

    public double getDamage() {
        return switch(this.getType()) {
            case LIGHT_ATTACK -> 1.0D;
            case HEAVY_ATTACK -> 1.5D;
        };
    }

    public AttackType getType() {
        return type;
    }

    public int getCooldown() {
        return setCooldown;
    }

    public float getAttackRange() {
        return attackRange;
    }
}
