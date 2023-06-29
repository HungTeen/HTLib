package hungteen.htlib.common.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import hungteen.htlib.common.world.entity.HTDummyEntities;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-01 18:40
 **/
public class DummyEntityArgument implements ArgumentType<ResourceLocation> {

    private static final Collection<String> EXAMPLES = Arrays.asList(HTDummyEntities.DEFAULT_RAID.getRegistryName());
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_ENTITY = new DynamicCommandExceptionType((p_93342_) -> {
        return Component.translatable("command.htlib.dummy_entity.not_found", p_93342_);
    });

    public static DummyEntityArgument id() {
        return new DummyEntityArgument();
    }

    public static ResourceLocation getDummyEntity(CommandContext<CommandSourceStack> commandContext, String type) throws CommandSyntaxException {
        return verify(commandContext.getArgument(type, ResourceLocation.class));
    }

    private static ResourceLocation verify(ResourceLocation location) throws CommandSyntaxException {
        HTDummyEntities.getEntityType(location).orElseThrow(() -> {
            return ERROR_UNKNOWN_ENTITY.create(location);
        });
        return location;
    }

    @Override
    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
        return verify(ResourceLocation.read(reader));
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
