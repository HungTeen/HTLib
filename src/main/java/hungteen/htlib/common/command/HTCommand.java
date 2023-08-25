package hungteen.htlib.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
import hungteen.htlib.util.helper.HTLibHelper;
import hungteen.htlib.util.helper.MathHelper;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
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

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-01 12:51
 **/
public class HTCommand {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed"));
    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(Component.translatable("commands.summon.invalidPosition"));
//    private static final SuggestionProvider<CommandSourceStack> ALL_CUSTOM_RAIDS = SuggestionProviders.register(HTLibHelper.prefix("all_custom_raids"), (commandContext, builder) -> {
//        return SharedSuggestionProvider.suggestResource(HTRaidComponents.getIds(), builder);
//    });
    private static final SuggestionProvider<CommandSourceStack> ALL_DUMMY_ENTITIES = SuggestionProviders.register(HTLibHelper.prefix("all_dummy_entities"), (commandContext, builder) -> {
        return SharedSuggestionProvider.suggestResource(HTDummyEntities.getIds(), builder);
    });

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("htlib").requires((ctx) -> ctx.hasPermission(2));
        builder.then(Commands.literal("create")
                .then(Commands.literal("dummy")
                        .then(Commands.argument("dummy_entity", DummyEntityArgument.id())
                                .suggests(ALL_DUMMY_ENTITIES)
                                .then(Commands.argument("position", Vec3Argument.vec3())
                                        .executes(ctx -> createDummyEntity(ctx.getSource(), DummyEntityArgument.getDummyEntity(ctx, "dummy_entity"), Vec3Argument.getVec3(ctx, "position"), new CompoundTag()))
                                        .then(Commands.argument("nbt", CompoundTagArgument.compoundTag())
                                                .executes(ctx -> createDummyEntity(ctx.getSource(), DummyEntityArgument.getDummyEntity(ctx, "dummy_entity"), Vec3Argument.getVec3(ctx, "position"), CompoundTagArgument.getCompoundTag(ctx, "nbt")))

                                        )
                                )
                        )
                )
//                .then(Commands.literal("raid")
//                        .then(Commands.argument("dummy_entity", DummyEntityArgument.id())
//                                .suggests(ALL_DUMMY_ENTITIES)
//                                .then(Commands.argument("type", ResourceArgument.resource(context, HTRaidComponents.registry().getRegistryKey()))
//                                        .then(Commands.argument("position", Vec3Argument.vec3())
//                                                .executes(ctx -> createRaid(ctx.getSource(), DummyEntityArgument.getDummyEntity(ctx, "dummy_entity"), ResourceArgument.getResource(ctx, "type", HTRaidComponents.registry().getRegistryKey()), Vec3Argument.getVec3(ctx, "position")))
//                                        )
//                                )
//                        )
//                )
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

    public static int createRaid(CommandSourceStack sourceStack, ResourceLocation dummyType, Holder<IRaidComponent> raid, Vec3 position) throws CommandSyntaxException {
        final CompoundTag nbt = new CompoundTag();
        CodecHelper.encodeNbt(HTRaidComponents.getDirectCodec(), raid.get())
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
