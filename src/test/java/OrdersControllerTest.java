/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.przemo.tradex.data.Equities;
import com.przemo.tradexserver.implementations.InfoController;
import com.przemo.tradexserver.implementations.LoginController;
import com.przemo.tradexserver.implementations.OrdersController;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.Date;
import junit.framework.TestCase;

/**
 *
 * @author Przemo
 */
public class OrdersControllerTest extends TestCase {
    
    OrdersController ic;
    InfoController icc;
    String ssid=null;
    
    public OrdersControllerTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ic = new OrdersController();
        icc = new InfoController();
        ssid = new LoginController().loginRequest("test", "test", InetAddress.getLocalHost().getHostAddress());
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        new LoginController().logoutRequest(ssid);
    }
    
    public void testPlaceAndRemoveOrder() throws RemoteException{
        Equities eq = (Equities) icc.requestAvailableInstruments(ssid).toArray()[0];
        assertNotNull(eq);
        long p =ic.placeOrder(eq, 10, new Date(), icc.requestAvailableOrderTypes().get(0), ssid);
        assertTrue(p>0);
        assertEquals(0, ic.removeOrder(p, ssid));
    }
}
