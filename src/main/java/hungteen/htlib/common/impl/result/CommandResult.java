package hungteen.htlib.common.impl.result;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.interfaces.raid.IRaid;
import hungteen.htlib.api.interfaces.raid.IResultComponent;
import hungteen.htlib.api.interfaces.raid.IResultType;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-03 22:49
 **/
public record CommandResult(String globalCommand, String defenderCommand, String raiderCommand) implements IResultComponent, CommandSource {

    public static final Codec<CommandResult> CODEC = RecordCodecBuilder.<CommandResult>mapCodec(instance -> instance.group(
            Codec.STRING.optionalFieldOf("global_command", "").forGetter(CommandResult::globalCommand),
            Codec.STRING.optionalFieldOf("defender_command", "").forGetter(CommandResult::defenderCommand),
            Codec.STRING.optionalFieldOf("raider_command", "").forGetter(CommandResult::raiderCommand)
    ).apply(instance, CommandResult::new)).codec();

    @Override
    public void apply(IRaid raid, ServerLevel level, int tick) {
        executeCommandTo(level, raid, null, globalCommand());
    }

    @Override
    public void applyToDefender(IRaid raid, Entity defender, int tick) {
        if(raid.getLevel() instanceof ServerLevel level){
            executeCommandTo(level, raid, defender, defenderCommand());
        }
    }

    @Override
    public void applyToRaider(IRaid raid, Entity raider, int tick) {
        if(raid.getLevel() instanceof ServerLevel level){
            executeCommandTo(level, raid, raider, raiderCommand());
        }
    }

    private void executeCommandTo(ServerLevel level, IRaid raid, Entity target, String command){
        MinecraftServer server = level.getServer();
        if (server.isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(command)) {
            try {
                CommandSourceStack commandStack;
                if(target != null){
                    commandStack = target.createCommandSourceStack().withSuppressedOutput().withPermission(2);
                } else {
                    commandStack = new CommandSourceStack(this, raid.getPosition(), Vec2.ZERO, level, 2, "Raid", raid.getTitle(), server, null);
                }
                server.getCommands().performPrefixedCommand(commandStack, command);
            } catch (Throwable throwable) {
                CrashReport $$4 = CrashReport.forThrowable(throwable, "Executing htlib command result");
                CrashReportCategory $$5 = $$4.addCategory("Command result to be executed");
                $$5.setDetail("Command", () -> command);
                throw new ReportedException($$4);
            }
        }
    }

    @Override
    public IResultType<?> getType() {
        return HTResultTypes.COMMAND;
    }

    @Override
    public void sendSystemMessage(Component component) {

    }

    @Override
    public boolean acceptsSuccess() {
        return false;
    }

    @Override
    public boolean acceptsFailure() {
        return false;
    }

    @Override
    public boolean shouldInformAdmins() {
        return false;
    }
}
