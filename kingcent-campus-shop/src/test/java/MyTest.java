import com.kingcent.campus.ShopApplication;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.service.WxPayService;
import com.kingcent.campus.service.impl.AppWxRefundService;
import com.kingcent.campus.shop.entity.OrderGoodsEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest(classes = ShopApplication.class)
public class MyTest {
    @Autowired
    private WxPayService wxPayService;


    @Autowired
    private AppWxRefundService refundService;

    @Test
    public void test() throws NoSuchAlgorithmException, IOException, UnrecoverableKeyException, CertificateException, KeyStoreException, KeyManagementException {
        System.out.println(refundService.requestToRefund(
                "4200001904202308195897056447",
                "123456789test",
                10,
                10,
                List.of(new OrderGoodsEntity(
                        26L,
                        26L,
                        6L,
                        1L,
                        "宿舍",
                        "向阳花",
                        "",
                        10,
                        new BigDecimal(1),
                        new BigDecimal(10),
                        new BigDecimal(0)
                )),
                "不用了"
        ));
    }
}
