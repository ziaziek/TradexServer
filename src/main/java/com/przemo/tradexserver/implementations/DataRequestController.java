/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.przemo.tradexserver.implementations;

import com.przemo.tradex.data.UserSessions;
import com.przemo.tradexserver.helpers.UserSessionsHelper;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 *
 * @author Przemo
 */
public class DataRequestController extends UnicastRemoteObject {
    
    protected Session session = null;
    protected final int sessionExpiryTime = 10; // number of minutes of inactivity
    
    public DataRequestController() throws RemoteException{
        super();
        
    }
    
    /**
     * Updates session info whenever a query is made to the database
     * @param sessionId 
     */
    protected void updateSessionInfo(String sessionId) {
        if(session!=null && session.isOpen()){
           UserSessions usList = UserSessionsHelper.findSessionBySessionId(session, sessionId); 
           if(usList!=null ){
               usList.setExpiryDate(produceExpiryDate(sessionExpiryTime));
               //should there be any transaction already open, just save the object, otherwise create a transaction and commit
               if(session.getTransaction()==null){
                   Transaction tx = session.beginTransaction();
                   session.saveOrUpdate(usList);
                   tx.commit();
               } else {
                   session.saveOrUpdate(usList);
               }
           }
        }       
    }
    
    protected boolean getCurrentDBSession(){
        if(session==null || !session.isOpen()){
           session = new AnnotationConfiguration().configure().buildSessionFactory().openSession(); 
        }
        return session!=null && session.isOpen();
    }
    
    /**
     * Closes the currently open session
     */
    public void clearSession(){
        if(session!=null && session.isOpen()){
          session.close();  
        }  
    }
    
    @Override
    public void finalize() throws Throwable{
        clearSession();
        super.finalize();
    }
    
    /**
     * Produces the expiry date of the session
     * @param minutes number of minutes after the current query time to set the session as expired
     * @return 
     */
    protected Date produceExpiryDate(int minutes) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MINUTE, minutes);
        return c.getTime();
    }
    
    public boolean isSessionOpen(String sessionId) throws RemoteException {
        if(getCurrentDBSession()){
            List<UserSessions> uss = session.getNamedQuery("findOpenSessionBySessionId").setParameter("ssid", sessionId)
                    .setParameter("dateNow", new Date()).list();
            updateSessionInfo(sessionId);
            return (uss!=null && !uss.isEmpty() && uss.get(0).getLogoutTime()==null);
        }
        return false;
    }
}
