import com.kingcent.campus.admin.AdminApplication;
import com.kingcent.campus.admin.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

/**
 * @author rainkyzhong
 * @date 2023/8/24 12:50
 */
@SpringBootTest(classes = AdminApplication.class)
public class OrderTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void testDelivery(){
//        System.out.println(orderService.startDelivery(1L, 1L, 10, LocalDate.now()));
    }
}
