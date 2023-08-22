import com.kingcent.campus.ShopApplication;
import com.kingcent.campus.service.OrderService;
import com.kingcent.campus.wx.service.WxPayService;
import com.kingcent.campus.wx.service.WxRefundService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = ShopApplication.class)
public class MyTest {
    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private WxRefundService refundService;

    @Test
    public void test() {
//        System.out.println(orderService.pay(6L, 14L, 58L, "192.168.1.1"));
    }
}
