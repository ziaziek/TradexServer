/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.przemo.tradexserver.implementations;

import com.przemo.tradex.data.Accounts;
import com.przemo.tradex.data.Equities;
import com.przemo.tradex.data.EquitiesTypes;
import com.przemo.tradex.data.OrderTypes;
import com.przemo.tradex.data.Transactions;
import com.przemo.tradex.data.UserSessions;
import com.przemo.tradex.data.Users;
import com.przemo.tradex.interfaces.IInfoController;
import com.przemo.tradexserver.helpers.AccountsHelper;
import com.przemo.tradexserver.helpers.UserSessionsHelper;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.Transaction;

/**
 *
 * @author Przemo
 */
public class InfoController extends DataRequestController implements IInfoController{

    public InfoController() throws RemoteException{
        super();
    }
    
    @Override
    public List<Transactions> requestTransactions(Date dateFrom, Date dateTo, String sessionId) throws RemoteException {
        if (getCurrentDBSession() && isSessionOpen(sessionId)) {
            UserSessions us = UserSessionsHelper.findSessionBySessionId(session, sessionId);
                if (us != null) {
                    Users u = (Users) session.get(Users.class, us.getUsers().getId());
                    if(u!=null){
                        //if dateFrom is null, take the zero date, if dateTo is null, take Now  
                        Date ctempFrom = new Date();
                        Date ctempTo = new Date();
                        if(dateFrom!=null){               
                            ctempFrom=dateFrom;
                        }
                        if(dateTo!=null){
                            ctempTo=dateTo;
                        } 
                        List<Transactions> trList = session.createQuery("from Transactions t where t.transactionDate>=:df and t.transactionDate<:dt "
                            + " and t.ordersByBuyerOrderId.users=:uid").setParameter("df", ctempFrom).setParameter("dt", ctempTo)
                            .setParameter("uid", u).list();
                        return trList;
                    }                  
                }
        } 
        return null;
    }

    @Override
    public Set<Equities> requestAvailableInstruments(String sessionId) throws RemoteException {
        if(getCurrentDBSession() && isSessionOpen(sessionId)){
            return new HashSet<>( session.createQuery("from Equities").list());           
        } else {
            return null;
        }
    }

    @Override
    public Equities requestQuotation(Date date, Object instrument, String sessionId) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<UserSessions> requestActivity(Date dateFrom, Date dateTo, String SessionId) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Equities> requestTimeRangeData(Date dateFrom, Date dateTo, int equityId, String sessionId) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Accounts> requestAccountInfo(String sessionId) throws RemoteException {
        if(getCurrentDBSession() && isSessionOpen(sessionId)){
            return AccountsHelper.findAccountsBySessionId(session, sessionId);
        }
        return null;
    }

    /**
     * This request can be sent by any client, even the one that is not logged in
     * @return
     * @throws RemoteException 
     */
    @Override
    public List<EquitiesTypes> requestCurrentEquitiesTypes() throws RemoteException {
        if(getCurrentDBSession()){
            return session.createQuery("from EquitiesTypes").list();
        } else {
            return null;
        }
    }

    /**
     * User data to be edited. Parameters for edition are formed as a map of parameter name and value.
     * The parameters available are defined in the interface
     * @param sessionId
     * @param userData
     * @return
     * @throws RemoteException 
     */
    @Override
    public boolean requestEditUserData(String sessionId, Map<String, Object> userData) throws RemoteException {
        boolean ret = false;
        if (getCurrentDBSession() && isSessionOpen(sessionId) && userData != null && !userData.isEmpty()) {
            Users u = UserSessionsHelper.findSessionBySessionId(session, sessionId).getUsers();
            if (u != null) {
                for (String sp : editUserParamsNames) {
                    if (userData.containsKey(sp)) {
                        if(sp.equals("password")){ //not very elegant, but reflection won't work here, as userData elements are objects
                            //and password is a private field
                            u.setPassword((String) userData.get(sp));
                        }
                    }
                }
                Transaction tx = session.beginTransaction();
                session.saveOrUpdate(u);
                updateSessionInfo(sessionId);
                tx.commit();
                ret=true;
            } else {
                ret = false;
            }
        }
        return ret;
    }

    @Override
    public List<OrderTypes> requestAvailableOrderTypes() throws RemoteException {
        if(getCurrentDBSession()){
            return session.createQuery("from OrderTypes").list();
        } else {
            return null;
        }
    }
    
}
