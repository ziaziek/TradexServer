/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.przemo.tradexserver.implementations;

import com.przemo.tradex.data.UserSessions;
import com.przemo.tradex.data.Users;
import com.przemo.tradex.interfaces.ILoginController;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.hibernate.JDBCException;
import org.hibernate.Query;
import org.hibernate.Transaction;
/**
 *
 * @author Przemo
 */
public class LoginController extends DataRequestController implements ILoginController {

    protected final String stringSet = "ABCDEFGHIJKLMNOPQRSTUWVXYZabcdefghijklmnopqrstuwvxyz0123456789_";
    
    protected int sessionIdLength=128;
   
    
    public LoginController() throws RemoteException{
        super();
        
    }
    
    @Override
    public String loginRequest(String username, String password, String ipaddress) throws RemoteException {
        String sid = null;
        if(getCurrentDBSession()){
            Query qry = session.createQuery("from Users where username=? and password=?");// where username='test' and password='test'");
            qry.setParameter(0, username);
            qry.setParameter(1, password);
            List<Users> us = qry.list();
            if (us != null && !us.isEmpty() && us.size() == 1) {
                try {
                    Transaction tx = session.beginTransaction();
                    String ssid = generateSessionId();
                    UserSessions uss = new UserSessions();
                    uss.setLoginIp(ipaddress);
                    uss.setSessionKey(ssid);
                    uss.setUsers(us.get(0));
                    uss.setLoginTime(new Date());
                    uss.setExpiryDate(produceExpiryDate(sessionExpiryTime));
                    us.get(0).setLastLogin(new Date());
                    us.get(0).setLastIpLogin(ipaddress);
                    session.save(uss);
                    session.saveOrUpdate(us.get(0));
                    tx.commit();
                    sid = ssid;
                    session.close();
                } catch (JDBCException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        
        return sid;
    }

    /**
     * Logout requested. Update UserSessions table to invalidate the sessionid
     * @param sessionId
     * @return true if succeeded, false - otherwise (session was already closed or coudn't be found
     * @throws RemoteException 
     * @author Przemek
     */
    @Override
    public boolean logoutRequest(String sessionId) throws RemoteException {
        if(getCurrentDBSession() && isSessionOpen(sessionId)){            
            List<UserSessions> uss = session.getNamedQuery("findOpenSessionBySessionId").setParameter("ssid", sessionId)
                    .setParameter("dateNow", new Date()).list();
            if(uss!=null && uss.size()==1){
                uss.get(0).setLogoutTime(new Date());
                Transaction tx = session.beginTransaction();
                session.update(uss.get(0));
                tx.commit();
                session.close();
                return true;
            }
            return false;
        }
        return false;
    }
    
    /**
     * Builds a unique session id that will be held as the session id and be passed to the client
     * @return string session id
     */
    protected String generateSessionId(){
        int v = stringSet.length();
        StringBuilder sBuilder = new StringBuilder(sessionIdLength);
        Random r = new Random();
        for(int i=0; i<sessionIdLength; i++){
            sBuilder.append(stringSet.charAt(Math.abs(r.nextInt()%v)));
        }
        return sBuilder.toString();
    }
    
}
