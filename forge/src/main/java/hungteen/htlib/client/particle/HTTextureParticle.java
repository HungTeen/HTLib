package hungteen.htlib.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 09:08
 **/
public abstract class HTTextureParticle extends TextureSheetParticle {

    protected HTTextureParticle(ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(clientLevel, x, y, z, xSpeed, ySpeed, zSpeed);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
    }
}
