package hungteen.htlib.util;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;

import java.util.List;
import java.util.Objects;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-25 15:56
 **/
public class PlayerUtil {

//    public static void playClientSound(Player player, SoundEvent ev) {
//        if(ev != null) {
//            PVZPacketHandler.sendToClient((ServerPlayer) player, new PlaySoundPacket(ev.getRegistryName().toString()));
//        }
//    }

    public static void sendTitleToPlayer(Player player, Component text) {
        if(player instanceof ServerPlayer) {
            ((ServerPlayer) player).connection.send(new ClientboundSetTitleTextPacket(text));
        }
    }

    public static void sendSubTitleToPlayer(Player player, Component text) {
        if(player instanceof ServerPlayer) {
            sendTitleToPlayer(player, new TextComponent(""));
            ((ServerPlayer) player).connection.send(new ClientboundSetSubtitleTextPacket(text));
        }
    }

    public static void sendMsgToAll(Level world, Component text) {
        getServerPlayers(world).forEach(player -> {
            sendMsgTo(player, text);
        });
    }

    /**
     * send a system chat message to player.
     */
    public static void sendMsgTo(Player player, Component text) {
        player.sendMessage(text, Util.NIL_UUID);
    }

    /**
     * display some tips to player in the middle bar.
     */
    public static void sendTipTo(Player player, Component text) {
        player.displayClientMessage(text, true);
    }

    /**
     * get all players in the server.
     */
    public static List<ServerPlayer> getServerPlayers(Level world){
        return Objects.requireNonNull(world.getServer()).getPlayerList().getPlayers();
    }

    /**
     * player is in survival mode for special judgement.
     */
    public static boolean isPlayerSurvival(Player player) {
        return ! player.isCreative() && ! player.isSpectator();
    }

    /**
     * Avoid crash by fake player.
     */
    public static boolean isValidPlayer(Player player) {
        return player != null && ! (player instanceof FakePlayer);
    }
}
