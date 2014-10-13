/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.entity.projectile;

import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.EntityProperties;
import net.tridentsdk.api.entity.living.ProjectileSource;
import net.tridentsdk.api.entity.projectile.Egg;
import net.tridentsdk.entity.TridentProjectile;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents an egg after being thrown
 *
 * @author The TridentSDK Team
 */
public class TridentEgg extends TridentProjectile implements Egg {
    /**
     * Inherits constructor from {@link net.tridentsdk.entity.TridentProjectile}
     */
    public TridentEgg(UUID uniqueId, Location spawnLocation, ProjectileSource source) {
        super(uniqueId, spawnLocation, source, false);
    }

    @Override
    public void applyProperties(EntityProperties properties) {
    }

    @Override
    protected void hit() {
        int chance = ThreadLocalRandom.current().nextInt(1, 256);

        if (chance <= 8) {
            if (chance == 1) {
                // TODO: Spawn 4 Baby Chicken
            } else {
                // TODO: Spawn 1 Baby Chicken
            }
        }
    }

    /* (non-Javadoc)
     * @see net.tridentsdk.api.entity.Projectile#getShooter()
     */
    @Override
    public ProjectileSource getShooter() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.tridentsdk.api.entity.Projectile#setShooter(net.tridentsdk.api.entity.living.ProjectileSource)
     */
    @Override
    public void setShooter(ProjectileSource shooter) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see net.tridentsdk.api.entity.Projectile#doesBounce()
     */
    @Override
    public boolean doesBounce() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.tridentsdk.api.entity.Projectile#setBounce(boolean)
     */
    @Override
    public void setBounce(boolean bouncy) {
        // TODO Auto-generated method stub

    }
}