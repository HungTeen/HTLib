package hungteen.htlib.util.helper;

import hungteen.htlib.common.HTLibProxy;
import hungteen.htlib.common.network.packet.PlaySoundPacket;
import hungteen.htlib.platform.HTLibPlatformAPI;
import hungteen.htlib.util.helper.impl.SoundHelper;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-25 15:56
 **/
public interface PlayerHelper {

    static void setCooldown(Player player, Item item, int coolDown){
        player.getCooldowns().addCooldown(item, coolDown);
    }

    static boolean isOnCooldown(Player player, Item item){
        return player.getCooldowns().isOnCooldown(item);
    }

    static Optional<Player> getClientPlayer(){
        return HTLibProxy.get().getClientPlayer();
    }

    static void playClientSound(Player player, Holder<SoundEvent> ev) {
        if(ev != null) {
            HTLibPlatformAPI.get().sendToClient((ServerPlayer) player, new PlaySoundPacket(ev));
        }
    }
    static void playClientSound(Player player, SoundEvent ev) {
        SoundHelper.get().holder(ev).ifPresent(sound -> {
            HTLibPlatformAPI.get().sendToClient((ServerPlayer)player, new PlaySoundPacket(sound));
        });
    }

    static void sendTitleToPlayer(Player player, Component text) {
        if(player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetTitleTextPacket(text));
        }
    }

    static void sendSubTitleToPlayer(Player player, Component text) {
        if(player instanceof ServerPlayer serverPlayer) {
            sendTitleToPlayer(player, Component.empty());
            serverPlayer.connection.send(new ClientboundSetSubtitleTextPacket(text));
        }
    }

    static void sendMsgToAll(ServerLevel world, Component text) {
        getServerPlayers(world).forEach(player -> {
            sendMsgTo(player, text);
        });
    }

    /**
     * send a system chat message to player.
     */
    static void sendMsgTo(Player player, Component text) {
        player.sendSystemMessage(text);
    }

    /**
     * display some tips to player in the middle bar.
     */
    static void sendTipTo(Player player, Component text) {
        player.displayClientMessage(text, true);
    }

    /**
     * @return  all players in the server of level.
     */
    static List<ServerPlayer> getServerPlayers(ServerLevel world){
        return getServerPlayers(world.getServer());
    }

    /**
     * @return  all players in the server.
     */
    static List<ServerPlayer> getServerPlayers(MinecraftServer server){
        return server.getPlayerList().getPlayers();
    }

    /**
     * player is in survival mode for special judgement.
     */
    static boolean isPlayerSurvival(Player player) {
        return ! player.isCreative() && ! player.isSpectator();
    }

}
