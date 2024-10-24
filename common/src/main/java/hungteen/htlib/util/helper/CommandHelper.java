package hungteen.htlib.util.helper;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2023-08-25 21:13
 **/
public interface CommandHelper {

    public static <T> Holder.Reference<T> getHolder(CommandContext<CommandSourceStack> context, ResourceKey<Registry<T>> key, String name, DynamicCommandExceptionType type) throws CommandSyntaxException {
        return ResourceKeyArgument.resolveKey(context, name, key, type);
    }

}
