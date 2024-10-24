package hungteen.htlib.common.impl.result;

import net.minecraft.resources.ResourceLocation;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2023-02-12 22:24
 **/
public record EventResult(ResourceLocation id, boolean forLevel, boolean forDefender, boolean forRaider) {
//        implements IResultComponent {
//
//    public static final MapCodec<EventResult> CODEC = RecordCodecBuilder.<EventResult>mapCodec(instance -> instance.group(
//            ResourceLocation.CODEC.fieldOf("id").forGetter(EventResult::id),
//            Codec.BOOL.optionalFieldOf("for_level", false).forGetter(EventResult::forLevel),
//            Codec.BOOL.optionalFieldOf("for_defender", false).forGetter(EventResult::forDefender),
//            Codec.BOOL.optionalFieldOf("for_raider", false).forGetter(EventResult::forRaider)
//    ).apply(instance, EventResult::new));
//
//    @Override
//    public void apply(IRaid raid, ServerLevel level, int tick) {
//        if(forLevel() && raid instanceof AbstractRaid){
//            MinecraftForge.EVENT_BUS.post(new RaidEvent.RaidResultLevelEvent(level, (AbstractRaid) raid, id(), tick));
//        }
//    }
//
//    @Override
//    public void applyToDefender(IRaid raid, Entity defender, int tick) {
//        if(forDefender() && raid instanceof AbstractRaid){
//            MinecraftForge.EVENT_BUS.post(new RaidEvent.RaidResultDefenderEvent(defender.level(), (AbstractRaid) raid, id(), defender, tick));
//        }
//    }
//
//    @Override
//    public void applyToRaider(IRaid raid, Entity raider, int tick) {
//        if(forRaider() && raid instanceof AbstractRaid){
//            MinecraftForge.EVENT_BUS.post(new RaidEvent.RaidResultRaiderEvent(raider.level(), (AbstractRaid) raid, id(), raider, tick));
//        }
//    }
//
//    @Override
//    public ResultType<?> getType() {
//        return HTLibResultTypes.EVENT;
//    }
}
