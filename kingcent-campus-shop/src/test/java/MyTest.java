import com.kingcent.campus.ShopApplication;
import com.kingcent.campus.service.OrderService;
import com.kingcent.campus.shop.constant.PayType;
import com.kingcent.campus.shop.constant.RefundReasons;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseConfirmGoodsVo;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseConfirmStoreVo;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseConfirmVo;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseStoreVo;
import com.kingcent.campus.wx.service.WxOrderService;
import com.kingcent.campus.wx.service.WxPayService;
import com.kingcent.campus.wx.service.WxRefundService;
import com.kingcent.campus.wx.service.WxShippingService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest(classes = ShopApplication.class)
public class MyTest {
//    @Autowired
//    private WxPayService wxPayService;
//    @Autowired
//    private WxOrderService wxOrderService;
//    @Autowired
//    private OrderService orderService;
//
//    @Autowired
//    private WxRefundService refundService;
//
//    @Autowired
//    private WxShippingService shippingService;
//
//    /**
//     * 测试商品下单
//     */
//    @Test
//    public void testCreateOrder(){
//        PurchaseConfirmVo purchase = new PurchaseConfirmVo();
//        purchase.setAddressId(45L);
//        List<PurchaseConfirmStoreVo> stores = new ArrayList<>();
//        PurchaseConfirmStoreVo store = new PurchaseConfirmStoreVo();
//        store.setDeliveryTime(LocalDateTime.of(2023,8,25,22,0));
//        List<PurchaseConfirmGoodsVo> goodsList = new ArrayList<>();
//        PurchaseConfirmGoodsVo goods = new PurchaseConfirmGoodsVo();
//        goods.setId(11L);
//        goods.setSku("[[1,1]]");
//        goods.setCount(1);
//        goodsList.add(goods);
//        store.setGoodsList(goodsList);
//        store.setId(1L);
//        store.setRemark("备注");
//        stores.add(store);
//        purchase.setStoreList(stores);
//        System.out.println(orderService.createOrders(
//                6L,
//                1L,
//                purchase,
//                "192.168.1.1",
//                PayType.WX_PAY
//        ));
//    }
//
//    /**
//     * 测试微信支付成功回调
//     */
//    @Test
//    public void testOnWxPayed(){
//        System.out.println(wxOrderService.onWxPayed(
//                6L,
//                "169282201309200060001",
//                "11",
//                2,
//                LocalDateTime.now()
//        ));
//    }
//
//    /**
//     * 测试退款
//     */
//    @Test
//    public void testRefund(){
//        orderService.requireRefund(
//                6L,
//                1L,
//                69L,
//                RefundReasons.list.get(0).getId(),
//                "不用了"
//        );
//    }
//
//    /**
//     * 测试退款成功回调
//     */
//    @Test
//    public void testOnRefunded(){
//        System.out.println(wxOrderService.onWxRefundSuccess(
//                "169282337985400060001",
//                "1",
//                LocalDateTime.now(),
//                BigDecimal.valueOf(0.02),
//                BigDecimal.valueOf(0.02)
//        ));
//    }
//
//
//
//    @Test
//    public void test() {
//        //System.out.println(orderService.pay(6L, 14L, 58L, "192.168.1.1"));
//        //System.out.println(shippingService.upload("o4kR165031cJjoSVfm5LKxv7S7Bs","169280968879300060009",true,"好吃"));
//    }
}
