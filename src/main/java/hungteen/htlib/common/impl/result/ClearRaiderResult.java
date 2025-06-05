package hungteen.htlib.common.impl.result;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.interfaces.raid.IRaid;
import hungteen.htlib.api.interfaces.raid.IResultComponent;
import hungteen.htlib.api.interfaces.raid.IResultType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2023-02-12 21:59
 **/
public record ClearRaiderResult(int clearTick) implements IResultComponent {

    public static final Codec<ClearRaiderResult> CODEC = RecordCodecBuilder.<ClearRaiderResult>mapCodec(instance -> instance.group(
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("clear_tick", 0).forGetter(ClearRaiderResult::clearTick)
    ).apply(instance, ClearRaiderResult::new)).codec();

    @Override
    public void apply(IRaid raid, ServerLevel level, int tick) {

    }

    @Override
    public void applyToDefender(IRaid raid, Entity defender, int tick) {

    }

    @Override
    public void applyToRaider(IRaid raid, Entity raider, int tick) {
        if(tick >= clearTick()){
            raid.removeRaider(raider);
            raider.discard();
        }
    }

    @Override
    public IResultType<?> getType() {
        return HTResultTypes.CLEAR_RAIDER;
    }
}
