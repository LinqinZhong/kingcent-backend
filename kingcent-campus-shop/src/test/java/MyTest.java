import com.kingcent.campus.ShopApplication;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@SpringBootTest(classes = ShopApplication.class)
public class MyTest {
    @Autowired
    private WxPayService wxPayService;

    @Test
    public void test() throws NoSuchAlgorithmException, UnknownHostException {
    }
}
