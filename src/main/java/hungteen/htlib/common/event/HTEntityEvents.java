package hungteen.htlib.common.event;

import hungteen.htlib.HTLib;
import hungteen.htlib.common.capability.raid.RaidCapability;
import hungteen.htlib.common.world.raid.DefaultRaid;
import hungteen.htlib.util.helper.registry.EntityHelper;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-04 11:06
 **/
@Mod.EventBusSubscriber(modid = HTLib.MOD_ID)
public class HTEntityEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void damage(LivingDamageEvent event){
        if(EntityHelper.isServer(event.getEntity()) && event.getSource().getEntity() != null){
            RaidCapability.getRaid(event.getEntity()).ifPresent(capability -> {
                if(capability.getRaid() instanceof DefaultRaid){
                    ((DefaultRaid)capability.getRaid()).addDefender(event.getSource().getEntity());
                }
            });
        }
    }

}
