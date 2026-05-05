package net.poob22.normaldm.common.server.entity.definition;

import net.poob22.normaldm.common.server.entity.projectile.BaseShotEntity;

public interface IShootingMob {
    /// Make sure any enemy implementing this has its eye level set to when the shot will come out
    BaseShotEntity createProjectile();
}
