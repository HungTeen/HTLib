package hungteen.htlib.common.impl.result;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.interfaces.raid.IRaid;
import hungteen.htlib.api.interfaces.raid.IResultComponent;
import hungteen.htlib.api.interfaces.raid.ResultType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/2 16:06
 **/
public record FunctionResult(List<ResourceLocation> globalFunctions, List<ResourceLocation> defenderFunctions, List<ResourceLocation> raiderFunctions) implements IResultComponent {

    public static final Codec<FunctionResult> CODEC = RecordCodecBuilder.<FunctionResult>mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.listOf().fieldOf("global_functions").forGetter(FunctionResult::globalFunctions),
            ResourceLocation.CODEC.listOf().fieldOf("defender_functions").forGetter(FunctionResult::defenderFunctions),
            ResourceLocation.CODEC.listOf().fieldOf("raider_functions").forGetter(FunctionResult::raiderFunctions)
    ).apply(instance, FunctionResult::new)).codec();

    @Override
    public void apply(IRaid raid, ServerLevel level, int tick) {
        if(tick == 0)
            globalFunctions().forEach((function) -> applyFunction(level, function, null));
    }

    @Override
    public void applyToDefender(IRaid raid, Entity defender, int tick) {
        if(tick == 0)
            defenderFunctions().forEach((function) -> applyFunction(defender.level(), function, defender));
    }

    @Override
    public void applyToRaider(IRaid raid, Entity raider, int tick) {
        if(tick == 0)
            raiderFunctions().forEach((function) -> applyFunction(raider.level(), function, raider));
    }

    private void applyFunction(Level level, ResourceLocation function, Entity entity){
        if(level instanceof ServerLevel serverLevel) {
            serverLevel.getServer().getFunctions().get(function).ifPresent((commandFunction) -> {
                CommandSourceStack sourceStack = serverLevel.getServer()
                        .createCommandSourceStack()
                        .withSuppressedOutput()
                        .withMaximumPermission(2)
                        .withLevel(serverLevel);
                if (entity != null) {
                    sourceStack = sourceStack.withEntity(entity)
                    ;
                }
                serverLevel.getServer().getFunctions().execute(commandFunction, sourceStack);
            });
        }
    }

    @Override
    public ResultType<?> getType() {
        return HTLibResultTypes.FUNCTION;
    }

}
