package hungteen.htlib.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import hungteen.htlib.api.interfaces.raid.IRaidComponent;
import hungteen.htlib.common.entity.SeatEntity;
import hungteen.htlib.common.impl.raid.HTRaidComponents;
import hungteen.htlib.common.world.entity.DummyEntity;
import hungteen.htlib.common.world.entity.DummyEntityManager;
import hungteen.htlib.common.world.entity.HTDummyEntities;
import hungteen.htlib.common.world.raid.AbstractRaid;
import hungteen.htlib.util.helper.CodecHelper;
import hungteen.htlib.util.helper.CommandHelper;
import hungteen.htlib.util.helper.HTLibHelper;
import hungteen.htlib.util.helper.MathHelper;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-01 12:51
 **/
public class HTCommand {

    private static final DynamicCommandExceptionType ERROR_INVALID_FEATURE = new DynamicCommandExceptionType((msg) -> {
        return Component.translatable("commands.place.feature.invalid", msg);
    });
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed"));
    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(Component.translatable("commands.summon.invalidPosition"));
    private static final SuggestionProvider<CommandSourceStack> ALL_CUSTOM_RAIDS = SuggestionProviders.register(HTLibHelper.prefix("all_custom_raids"), (commandContext, builder) -> {
//        Optional<HolderLookup.RegistryLookup<IRaidComponent>> raids = commandContext.getSource().registryAccess().lookup(HTRaidComponents.registry().getRegistryKey());
//        if(raids.isPresent()){
//            return SharedSuggestionProvider.suggestResource(raids.get().listElementIds().map(ResourceKey::location), builder);
//        }
        return SharedSuggestionProvider.suggestResource(HTRaidComponents.registry().getCachedKeys(), builder);
    });
    private static final SuggestionProvider<CommandSourceStack> ALL_DUMMY_ENTITIES = SuggestionProviders.register(HTLibHelper.prefix("all_dummy_entities"), (commandContext, builder) -> {
        return SharedSuggestionProvider.suggestResource(HTDummyEntities.getIds(), builder);
    });

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("htlib").requires((ctx) -> ctx.hasPermission(2));
        builder.then(Commands.literal("dummy")
                .then(Commands.literal("create")
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
                .then(Commands.literal("remove")
                        .then(Commands.argument("dummy_entity", DummyEntityArgument.id())
                                .suggests(ALL_DUMMY_ENTITIES)
                                .then(Commands.literal("nearby")
                                        .then(Commands.argument("position", Vec3Argument.vec3())
                                                .executes(context -> removeNearbyDummyEntity(context.getSource(), DummyEntityArgument.getDummyEntity(context, "dummy_entity"), Vec3Argument.getVec3(context, "position")))
                                        ))
                                .then(Commands.literal("all")
                                        .executes(context -> removeAllDummyEntity(context.getSource(), DummyEntityArgument.getDummyEntity(context, "dummy_entity")))
                                )
                        )
                )
        );
        builder.then(Commands.literal("raid")
                .then(Commands.literal("create")
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
        builder.then(Commands.literal("seat")
                .then(Commands.argument("target", EntityArgument.entity())
                        .then(Commands.argument("position", Vec3Argument.vec3())
                                .executes(ctx -> seat(ctx.getSource(), EntityArgument.getEntity(ctx, "target"), Vec3Argument.getVec3(ctx, "position")))
                        )
                )
        );
        dispatcher.register(builder);
    }

    private static Holder<IRaidComponent> getRaid(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        return CommandHelper.getHolder(context, HTRaidComponents.registry().getRegistryKey(), name, ERROR_INVALID_FEATURE);
    }

    public static int createDummyEntity(CommandSourceStack sourceStack, ResourceLocation location, Vec3 position, CompoundTag tag) throws CommandSyntaxException {
        final BlockPos blockpos = MathHelper.toBlockPos(position);
        if (!Level.isInSpawnableBounds(blockpos)) {
            throw INVALID_POSITION.create();
        }
        final DummyEntity dummyEntity = DummyEntityManager.createDummyEntity(sourceStack.getLevel(), location, position, tag);
        if (dummyEntity != null) {
            sourceStack.sendSuccess(() -> Component.translatable("commands.summon.success", dummyEntity.getEntityType().getRegistryName()), true);
            return 1;
        }
        throw ERROR_FAILED.create();
    }

    public static int removeNearbyDummyEntity(CommandSourceStack sourceStack, ResourceLocation dummyType, Vec3 position) throws CommandSyntaxException {
        List<DummyEntity> list = DummyEntityManager.getDummyEntities(sourceStack.getLevel(), dummyType, position, 1).toList();
        DummyEntityManager.markRemoveEntities(list);
        return list.size();
    }

    public static int removeAllDummyEntity(CommandSourceStack source, ResourceLocation dummyType) {
        List<DummyEntity> list = DummyEntityManager.getDummyEntities(source.getLevel(), dummyType).toList();
        DummyEntityManager.markRemoveEntities(list);
        return list.size();
    }

    public static int createRaid(CommandSourceStack sourceStack, ResourceLocation dummyType, ResourceLocation raidId, Vec3 position) throws CommandSyntaxException {
        final CompoundTag nbt = new CompoundTag();
        IRaidComponent raidComponent = HTRaidComponents.registry().getValue(sourceStack.getLevel(), HTRaidComponents.registry().createKey(raidId));
        CodecHelper.encodeNbt(HTRaidComponents.getDirectCodec(), raidComponent)
                .result().ifPresent(tag -> {
                    nbt.put(AbstractRaid.RAID_TAG, tag);
                });
        return createDummyEntity(sourceStack, dummyType, position, nbt);
    }

    public static int seat(CommandSourceStack sourceStack, Entity entity, Vec3 position) {
        if(entity instanceof LivingEntity livingEntity){
            SeatEntity.seatAt(sourceStack.getLevel(), livingEntity, MathHelper.toBlockPos(position), 0, entity.getYRot(), 120, false);
        }
        return 1;
    }

}
