package hungteen.htlib.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 22:30
 **/
public class HTMultiTextureParticle extends HTTextureParticle {

    protected final SpriteSet sprites;

    public HTMultiTextureParticle(ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(clientLevel, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(sprites);
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
}
