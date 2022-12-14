package hungteen.htlib.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.world.entity.DummyEntity;
import hungteen.htlib.common.world.entity.DummyEntityManager;
import hungteen.htlib.common.world.entity.DummyEntityType;
import hungteen.htlib.common.world.entity.HTDummyEntities;
import hungteen.htlib.impl.raid.HTRaidComponents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-01 12:51
 **/
public class HTCommand {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed"));
    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(Component.translatable("commands.summon.invalidPosition"));
    private static final SuggestionProvider<CommandSourceStack> ALL_CUSTOM_RAIDS = SuggestionProviders.register(HTLib.prefix("all_custom_raids"), (commandContext, builder) -> {
        return SharedSuggestionProvider.suggestResource(HTRaidComponents.getIds(), builder);
    });
    private static final SuggestionProvider<CommandSourceStack> ALL_DUMMY_ENTITIES = SuggestionProviders.register(HTLib.prefix("all_dummy_entities"), (commandContext, builder) -> {
        return SharedSuggestionProvider.suggestResource(HTDummyEntities.getIds(), builder);
    });

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("htlib").requires((ctx) -> ctx.hasPermission(2));
        builder.then(Commands.literal("create")
                        .then(Commands.literal("dummy")
                                .then(Commands.argument("dummy_entity", DummyEntityArgument.id())
                                        .suggests(ALL_DUMMY_ENTITIES)
                                        .then(Commands.argument("position", Vec3Argument.vec3())
                                                .executes(context -> createDummyEntity(context.getSource(), DummyEntityArgument.getDummyEntity(context, "dummy_entity"), Vec3Argument.getVec3(context, "position"), new CompoundTag()))
                                                .then(Commands.argument("nbt", CompoundTagArgument.compoundTag())
                                                        .executes(context -> createDummyEntity(context.getSource(), DummyEntityArgument.getDummyEntity(context, "dummy_entity"), Vec3Argument.getVec3(context, "position"), CompoundTagArgument.getCompoundTag(context, "nbt")))
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("raid")
                                .then(Commands.argument("dummy_entity", DummyEntityArgument.id())
                                        .suggests(ALL_DUMMY_ENTITIES)
                                        .then(Commands.argument("type", ResourceLocationArgument.id())
                                                .suggests(ALL_CUSTOM_RAIDS)
                                                .then(Commands.argument("position", Vec3Argument.vec3())
                                                        .executes(context -> createRaid(context.getSource(), DummyEntityArgument.getDummyEntity(context, "dummy_entity"), ResourceLocationArgument.getId(context, "type"), Vec3Argument.getVec3(context, "position")))
                                                )
                                        )
                                )
                        )
        );
        dispatcher.register(builder);
    }

    public static int createDummyEntity(CommandSourceStack sourceStack, ResourceLocation location, Vec3 position, CompoundTag tag) throws CommandSyntaxException {
        BlockPos blockpos = new BlockPos(position);
        if (!Level.isInSpawnableBounds(blockpos)) {
            throw INVALID_POSITION.create();
        }
        if(HTDummyEntities.getEntityType(location).isPresent()){
            final DummyEntityType<?> dummyEntityType = HTDummyEntities.getEntityType(location).get();
            CompoundTag compoundtag = tag.copy();
            compoundtag.putInt("DummyEntityID", DummyEntityManager.get(sourceStack.getLevel()).getUniqueId());
            Vec3.CODEC.encodeStart(NbtOps.INSTANCE, position)
                    .result().ifPresent(nbt -> compoundtag.put("Position", nbt));

            DummyEntity dummyEntity = dummyEntityType.create(sourceStack.getLevel(), compoundtag);

            if (dummyEntity != null) {
                DummyEntityManager.get(sourceStack.getLevel()).add(dummyEntity);
                sourceStack.sendSuccess(Component.translatable("commands.summon.success", dummyEntityType.getRegistryName()), true);
                return 1;
            }
        }
        throw ERROR_FAILED.create();
    }

    public static int createRaid(CommandSourceStack sourceStack, ResourceLocation dummyType, ResourceLocation location, Vec3 position) throws CommandSyntaxException {
        final CompoundTag tag = new CompoundTag();
        tag.putString("RaidLocation", location.toString());
        return createDummyEntity(sourceStack, dummyType, position, tag);
    }

}
