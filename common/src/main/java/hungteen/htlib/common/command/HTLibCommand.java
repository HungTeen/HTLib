package hungteen.htlib.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import hungteen.htlib.common.entity.SeatEntity;
import hungteen.htlib.common.world.entity.DummyEntity;
import hungteen.htlib.common.world.entity.DummyEntityManager;
import hungteen.htlib.util.helper.MathHelper;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-01 12:51
 **/
public class HTLibCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("htlib").requires((ctx) -> ctx.hasPermission(2));
        builder.then(Commands.literal("dummy")
                .then(Commands.literal("remove")
                        .then(Commands.literal("nearby")
                                .then(Commands.argument("position", Vec3Argument.vec3())
                                        .executes(context -> removeNearbyDummyEntity(context.getSource(), Vec3Argument.getVec3(context, "position")))
                                ))
                        .then(Commands.literal("all")
                                .executes(context -> removeAllDummyEntity(context.getSource()))
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

    public static int removeNearbyDummyEntity(CommandSourceStack sourceStack, Vec3 position) {
        List<DummyEntity> list = DummyEntityManager.getDummyEntities(sourceStack.getLevel(), position, 1).toList();
        DummyEntityManager.markRemoveEntities(list);
        return list.size();
    }

    public static int removeAllDummyEntity(CommandSourceStack source) {
        List<DummyEntity> list = DummyEntityManager.getDummyEntities(source.getLevel());
        DummyEntityManager.markRemoveEntities(list);
        return list.size();
    }

    public static int seat(CommandSourceStack sourceStack, Entity entity, Vec3 position) {
        if (entity instanceof LivingEntity livingEntity) {
            SeatEntity.seatAt(sourceStack.getLevel(), livingEntity, MathHelper.toBlockPos(position), 0, entity.getYRot(), 120, false);
        }
        return 1;
    }

}
