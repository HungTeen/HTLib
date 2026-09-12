package hungteen.htlib.client;

import hungteen.htlib.client.gui.widget.codec.RegistryEntriesCache;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 客户端 Codec 相关事件：在标签同步（/reload 或登录）时清空缓存，确保后续请求获取最新数据。
 *
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/12
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientCodecEvents {

    /**
     * 标签同步后清空缓存：/reload 会重新生成所有数据驱动内容（注册表条目、标签等），
     * 客户端缓存需要清空以便下次请求获取最新数据。
     */
    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        RegistryEntriesCache.clear();
//        EntrySchemaCache.clear();
    }
}
