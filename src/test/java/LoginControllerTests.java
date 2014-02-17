/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.przemo.tradex.data.UserSessions;
import com.przemo.tradexserver.implementations.LoginController;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import junit.framework.TestCase;
import org.hibernate.Transaction;

/**
 *
 * @author Przemo
 */
public class LoginControllerTests extends TestCase {
    
    public LoginControllerTests(String testName) {
        super(testName);
    }
    
     private static final String username = "test";
    private static final String password="test";//parameters for these tests, they are predefined in the testing database
    
    LoginControllerStub lgC;
    String ssid=null; //session id used for logout testing after login is done
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        lgC = new LoginControllerStub();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        //lgC.cleanTestSession(ssid);
        lgC = null;        
    }
    

    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}
    

    public void testSessionIdGeneration(){
        assertEquals(lgC.getIdLength(), lgC.getSessId().length());
        assertFalse(lgC.getSessId().equals(lgC.getSessId()));
        boolean left=false, right=false;
        int maxIterations=1024, i=0;
        while(!left && !right && i<maxIterations){//very little probability that there will be not boundary character that we are looking for
            String s = lgC.getSessId();
            //do the check only if left is still false, so the sequences don't contain the first char
            if(!left){
                left=s.contains(lgC.getStringSet().subSequence(0, 0));
            }
            //likewise right
            if(!right){
               right=s.contains(lgC.getStringSet().subSequence(lgC.getStringSet().length()-1, lgC.getStringSet().length()-1)); 
            }       
            i++;
        }
        assertTrue(left);
        assertTrue(right);
        System.out.println("SessionId test done.");
    }
    
    public void testLogin() throws RemoteException{
        try {
            ssid=lgC.loginRequest(username, password, InetAddress.getLocalHost().getHostAddress());
            assertNotNull(ssid);
            assertEquals(lgC.getIdLength(), ssid.length());
            assertTrue(lgC.isSessionOpen(ssid));
        } catch (UnknownHostException ex) {
            Logger.getLogger(LoginControllerTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void testLogout() throws UnknownHostException{
        try {
            ssid=lgC.loginRequest(username, password, InetAddress.getLocalHost().getHostAddress());
            assertNotNull(ssid);
            assertTrue(lgC.logoutRequest(ssid));
        } catch (RemoteException ex) {
            Logger.getLogger(LoginControllerTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected class LoginControllerStub extends LoginController{
        
        public LoginControllerStub() throws RemoteException{
            super();
        }
        //we publisize the protected method
        public String getSessId(){
            return super.generateSessionId();
        }
        
        public int getIdLength(){
            return super.sessionIdLength;
        }
        
        public String getStringSet(){
            return super.stringSet;
        }
        
        public void cleanTestSession(String sessionId){
            if(session!=null && session.isOpen()){
                List<UserSessions> iter = session.createQuery("from UserSessions where sessionKey=?").setParameter(0, sessionId).list();
                Transaction tx = session.beginTransaction();
                    session.delete(iter.get(0));
                tx.commit();
            }
            session.close();
        }
    }
}
