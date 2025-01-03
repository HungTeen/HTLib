package hungteen.htlib.common.capability;

/**
 * 玩家能力系统管理器，帮助自动监听各种事件。
 * @author PangTeen
 * @program HTLib
 * @data 2023/2/23 12:16
 */
public class PlayerCapabilityManager {

//    public static Set<Capability<? extends IHTPlayerCapability<? extends HTPlayerData>>> PLAYER_CAPABILITIES = new HashSet<>();

//    /**
//     * {@link HTLibForgeInitializer}
//     */
//    public static void tick(TickEvent.PlayerTickEvent event){
//        // Server side only.
//        if(event.phase == TickEvent.Phase.END && event.side == LogicalSide.SERVER){
//            getOptPlayerManagers(event.player).forEach(l -> l.ifPresent(HTPlayerData::tick));
//        }
//    }
//
//    /**
//     * Server Side call to sync data to client side.
//     * {@link hungteen.htlib.common.event.HTPlayerEvents#login(PlayerEvent.PlayerLoggedInEvent)}
//     */
//    public static void syncToClient(Player player) {
//        getOptPlayerManagers(player).forEach(l -> l.ifPresent(HTPlayerData::syncToClient));
//    }
//
//    /**
//     * {@link hungteen.htlib.common.event.HTPlayerEvents#onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone)}
//     */
//    public static void cloneData(Player oldPlayer, Player newPlayer, boolean died) {
//        getPlayerCapabilities().forEach(cap -> {
//            getOptManager(oldPlayer, cap).ifPresent(oldL -> {
//                getOptManager(newPlayer, cap).ifPresent(newL -> newL.cloneFromExistingPlayerData(oldL, died));
//            });
//        });
//    }
//
//    /**
//     * Recommend to use when common set up.
//     */
//    public static <T extends HTPlayerData, K extends IHTPlayerCapability<T>> void initialize(Capability<K> capability){
//        if(PLAYER_CAPABILITIES.contains(capability)){
//            HTLibAPI.logger().warn("Registering duplicated capability : {}", capability.getName());
//        } else {
//            PLAYER_CAPABILITIES.add(capability);
//        }
//    }
//
//    public static List<? extends Optional<? extends HTPlayerData>> getOptPlayerManagers(Player player){
//        return getPlayerCapabilities().stream().map(l -> getOptManager(player, l)).toList();
//    }
//
//    public static Collection<Capability<? extends IHTPlayerCapability<? extends HTPlayerData>>> getPlayerCapabilities(){
//        return Collections.unmodifiableSet(PLAYER_CAPABILITIES);
//    }
//
//    public static Optional<? extends HTPlayerData> getOptManager(Player player, Capability<? extends IHTPlayerCapability<? extends HTPlayerData>> capability) {
//        return Optional.ofNullable(getManager(player, capability));
//    }
//
//    @Nullable
//    public static <T extends HTPlayerData, K extends IHTPlayerCapability<? extends T>> T getManager(Player player, Capability<K> capability) {
//        if(PlayerHelper.isValidPlayer(player)) {
//            final Optional<K> optional = getPlayerCapability(player, capability).resolve();
//            return optional.map(IHTPlayerCapability::get).orElse(null);
//        }
//        return null;
//    }
//
//    public static <U, T extends HTPlayerData, K extends IHTPlayerCapability<T>> U getManagerResult(Player player, Capability<K> capability, Function<T, U> function, U defaultValue) {
//        final T manager = getManager(player, capability);
//        return manager != null ? function.apply(manager) : defaultValue;
//    }
//
//    public static <T extends HTPlayerData, K extends IHTPlayerCapability<? extends T>> LazyOptional<K> getPlayerCapability(Player player, Capability<K> capability){
//        return player.getCapability(capability);
//    }
}
