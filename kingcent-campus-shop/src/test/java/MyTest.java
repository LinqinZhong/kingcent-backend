import com.kingcent.campus.ShopApplication;
import com.kingcent.campus.wx.service.WxPayService;
import com.kingcent.campus.wx.service.WxRefundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = ShopApplication.class)
public class MyTest {
    @Autowired
    private WxPayService wxPayService;


    @Autowired
    private WxRefundService refundService;
//
//    @Test
//    public void test() throws NoSuchAlgorithmException, IOException, UnrecoverableKeyException, CertificateException, KeyStoreException, KeyManagementException {
//        System.out.println(refundService.requestToRefund(
//                "4200001906202308202390219233",
//                "123456790test",
//                1,
//                1,
//                List.of(new WxOrderGoodsEntity(
//                        "1",
//                        "哈哈",
//                        1L,
//                        1L,
//                        1
//
//                )),
//                "不用了"
//        ));
//    }
}
