package hungteen.htlib.util.helper;

import hungteen.htlib.HTLib;
import hungteen.htlib.common.network.NetworkHandler;
import hungteen.htlib.common.network.PlaySoundPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-25 15:56
 **/
public class PlayerHelper {

    public static void setCooldown(Player player, Item item, int coolDown){
        player.getCooldowns().addCooldown(item, coolDown);
    }

    public static boolean isOnCooldown(Player player, Item item){
        return player.getCooldowns().isOnCooldown(item);
    }

    /**
     * Only use on client side !
     */
    public static Optional<Player> getClientPlayer() {
        return Optional.ofNullable(HTLib.PROXY.getPlayer());
    }

    public static void playClientSound(Player player, @Nullable SoundEvent ev) {
        if(ev != null) {
            NetworkHandler.sendToClient((ServerPlayer) player, new PlaySoundPacket(ev.getLocation().toString()));
        }
    }

    public static void sendTitleToPlayer(Player player, Component text) {
        if(player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetTitleTextPacket(text));
        }
    }

    public static void sendSubTitleToPlayer(Player player, Component text) {
        if(player instanceof ServerPlayer serverPlayer) {
            sendTitleToPlayer(player, Component.empty());
            serverPlayer.connection.send(new ClientboundSetSubtitleTextPacket(text));
        }
    }

    public static void sendMsgToAll(ServerLevel world, Component text) {
        getServerPlayers(world).forEach(player -> {
            sendMsgTo(player, text);
        });
    }

    /**
     * send a system chat message to player.
     */
    public static void sendMsgTo(Player player, Component text) {
        player.sendSystemMessage(text);
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
    public static List<ServerPlayer> getServerPlayers(ServerLevel world){
        return world.getServer().getPlayerList().getPlayers();
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
