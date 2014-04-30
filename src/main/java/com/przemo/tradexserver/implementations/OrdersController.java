/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.przemo.tradexserver.implementations;

import com.przemo.tradex.data.Equities;
import com.przemo.tradex.data.OrderTypes;
import com.przemo.tradex.data.Orders;
import com.przemo.tradex.interfaces.IOrdersController;
import com.przemo.tradexserver.helpers.UserSessionsHelper;
import com.przemo.tradexserver.helpers.UsersHelper;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import org.hibernate.Transaction;

/**
 *
 * @author Przemo
 */
public class OrdersController extends DataRequestController implements IOrdersController{

    
    public OrdersController() throws RemoteException{
        super();
    }
    
    /**
     * Places order with given parameters
     * @param equityId
     * @param amount
     * @param dateValid
     * @param orderType
     * @param sessionId
     * @return id of the order placed
     * @throws RemoteException 
     */
    @Override
    public long placeOrder(Equities equityId, double amount, Date dateValid, OrderTypes orderType, String sessionId) throws RemoteException {
        long r = -1;
        updateSessionInfo(sessionId);
        if(getCurrentDBSession() && isSessionOpen(sessionId)){
            Orders newOrder = new Orders();
            newOrder.setEquities(equityId);
            newOrder.setInitialQuantity(amount);
            newOrder.setQuantity(amount);
            newOrder.setOrderTypes(orderType);
            newOrder.setValidThru(dateValid);
            newOrder.setUsers(UsersHelper.findUsersById(session, UserSessionsHelper.findSessionBySessionId(session, sessionId).getUsers().getId()));
            Transaction tx = session.beginTransaction();
            session.saveOrUpdate(newOrder);        
            tx.commit();
            r = newOrder.getId();
            clearSession();          
        }
        return r;
    }

    /**
     * Removes order of the given id number
     * @param orderId
     * @param sessionId
     * @return zero if removed succesfullt, -1 otherwise
     * @throws RemoteException 
     */
    @Override
    public int removeOrder(long orderId, String sessionId) throws RemoteException {
        updateSessionInfo(sessionId);
        if(getCurrentDBSession() && isSessionOpen(sessionId)){
            Orders ord = (Orders) session.get(Orders.class, orderId);
            if(ord!=null){
                Transaction tx = session.beginTransaction();
                session.delete(ord);             
                tx.commit();
                return 0;
            }
            clearSession();
        }
        return -1;
    }
    
}
