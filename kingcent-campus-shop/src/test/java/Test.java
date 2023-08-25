import com.kingcent.campus.ShopApplication;
import com.kingcent.campus.service.GoodsSkuService;
import com.kingcent.campus.service.OrderService;
import com.kingcent.campus.wx.service.WxOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author rainkyzhong
 * @date 2023/8/25 7:53
 */
@SpringBootTest(classes = ShopApplication.class)
public class Test {

    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsSkuService skuService;

    @Autowired
    private WxOrderService wxOrderService;

    @org.junit.jupiter.api.Test
    public void test() throws InterruptedException {

    }
}
