package hungteen.htlib;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.common.impl.HTLibAPIImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/26 16:19
 **/
public class HTLibAPITest {

    @Test
    public void testPlatformAPI(){
        Assertions.assertEquals(HTLibAPI.get().getClass(), HTLibAPIImpl.class);
    }
}
