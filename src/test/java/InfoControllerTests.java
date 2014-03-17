/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.przemo.tradex.data.Accounts;
import com.przemo.tradex.data.Equities;
import com.przemo.tradex.data.EquitiesPriceHistory;
import com.przemo.tradex.data.OrderTypes;
import com.przemo.tradex.data.Transactions;
import com.przemo.tradex.interfaces.IInfoController;
import com.przemo.tradexserver.implementations.InfoController;
import com.przemo.tradexserver.implementations.LoginController;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static junit.framework.Assert.assertNotNull;
import junit.framework.TestCase;

/**
 * Each test that retrieves any information from the server needs to check against validity od the session and ssid.
 * Thus, it should check whether the methods tested work with valid ssid, and make sure that they don't work with
 * an invalid or no ssid.
 * @author Przemo
 */
public class InfoControllerTests extends TestCase {
    
    InfoController ic;
    String ssid=null;
    public InfoControllerTests(String testName){
        super(testName);         
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ic = new InfoController();
        ssid = new LoginController().loginRequest("test", "test", InetAddress.getLocalHost().getHostAddress());
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        new LoginController().logoutRequest(ssid);
    }
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}

//    public void testTransactionsRequest() throws RemoteException{
//        //test with ssid
//        List<Transactions> trList = ic.requestTransactions(null, null, ssid);
//        assertNotNull(trList);
//        if(!trList.isEmpty()){ //there are any data for Transactions for the current user, check them
//            Transactions tr = trList.get(0);
//            assertNotNull(tr.getId());
//            assertNotNull(tr.getTransactionDate());
//            assertTrue(tr.getPrice()>0);
//            assertTrue(tr.getQuantity()>0);
//        }
//        trList = ic.requestTransactions(new Date(), new Date(), "");
//        assertNull(trList); //wrong ssid - no data should be given
//    }
//    
//    public void testRequestAvailableInstruments() throws RemoteException{
//        //test with ssid
//        Set<Equities> eqList = ic.requestAvailableInstruments(ssid);
//        assertNotNull(eqList);
//        if(!eqList.isEmpty()){
//            for(Equities eq: eqList){
//                assertNotNull(eq);
//                assertNotNull(eq.getEquitySymbol());
//            }
//        }
//        assertNull(ic.requestAvailableInstruments(""));
//    }
//    
//    public void testRequestEditUserData() throws RemoteException, UnknownHostException {
//        String changedPassword = "tescik";
//        assertTrue(changePassword(changedPassword));
//        //logout
//        new LoginController().logoutRequest(ssid);
//        //log in again with old credentials
//        ssid = new LoginController().loginRequest("test", "test", InetAddress.getLocalHost().getHostAddress());
//        assertNull(ssid);
//        //login with new credentials
//        ssid = new LoginController().loginRequest("test", changedPassword, InetAddress.getLocalHost().getHostAddress());
//        assertNotNull(ssid);
//        //ask for some information
//        List<Accounts> acc = ic.requestAccountInfo(ssid);
//        //change back to original password
//        assertTrue(changePassword("test"));
//        //make sure that the info from the previous session was not null
//        assertNotNull(acc);
//        if(!acc.isEmpty()){
//            assertNotNull(acc.get(0).getAccountNumber());
//            assertNotNull(acc.get(0).getBalance());
//        }
//    }
//    
//    public void testAvailableOrdersTypes() throws RemoteException{
//        List<OrderTypes> oTypes = ic.requestAvailableOrderTypes();
//        assertNotNull(oTypes);
//        assertFalse(oTypes.isEmpty());
//    }
//    
//    public void testTimeRangeData() throws RemoteException{
//        Calendar cf = Calendar.getInstance();
//        Calendar ct = Calendar.getInstance();
//        cf.add(Calendar.MONTH, -12);
//        Equities  eq = ic.requestAvailableInstruments(ssid).iterator().next();
//        assertNotNull(eq);
//        List<EquitiesPriceHistory> eqList = ic.requestTimeRangeData(cf.getTime(), ct.getTime(), eq, ssid);
//        assertNotNull(eqList);
//        assertTrue("Empty history list!", eqList.size()>0);
//        //pick the first item from the list and check that the fields are not null's
//        EquitiesPriceHistory eqItem = eqList.get(0);
//        assertNotNull(eqItem);
//        assertTrue(eqItem.getAskPrice()>0);
//        assertTrue(eqItem.getRecordDate().compareTo(new Date())<=0);
//    }
//    
//    public void testRequestQuotation() throws RemoteException{
//        Calendar ct = Calendar.getInstance();
//        Equities  eq = ic.requestAvailableInstruments(ssid).iterator().next();
//        assertNotNull(eq);
//        Equities quot = ic.requestQuotation(ct.getTime(), eq, ssid);
//        assertNotNull(quot);
//        assertNotNull(quot.getEquitySymbol());
//        assertTrue(quot.getAskPrice()>0);
//        assertTrue(quot.getBidPrice()>0);
//    }
    
    //Condition - nothing changes in the data source when the test runs
    public void testAvailableInstrumentsUnchanged() throws RemoteException{
        Set<Equities> eq = ic.requestAvailableInstruments(ssid);
        assertNotNull(eq);
        for(int i = 0; i<250; i++){
            System.out.println("Iteration "+i);
            Set<Equities> eq1 = ic.requestAvailableInstruments(ssid);
            assertNotNull(eq1);
            assertEquals(eq.size(), eq1.size());
            Iterator<Equities> eqIt = eq.iterator();
            Iterator<Equities> eq1Iter = eq1.iterator();
           while(eqIt.hasNext() && eq1Iter.hasNext()){
               Equities eqItem = eqIt.next();
               Equities eq1Item = eq1Iter.next();
               assertEquals(eqItem.getEquitySymbol(), eq1Item.getEquitySymbol());
           }
        }
    }
            
    private boolean changePassword(String pw) throws RemoteException{
        Map<String,Object> ps = new HashMap<>();
        ps.put(IInfoController.editUserParamsNames[0], pw);
        return ic.requestEditUserData(ssid, ps);
    } 
    
}
