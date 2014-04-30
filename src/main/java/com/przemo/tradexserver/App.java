package com.przemo.tradexserver;

import com.przemo.tradex.interfaces.IInfoController;
import com.przemo.tradex.interfaces.ILoginController;
import com.przemo.tradex.interfaces.IOrdersController;
import com.przemo.tradexserver.implementations.InfoController;
import com.przemo.tradexserver.implementations.LoginController;
import com.przemo.tradexserver.implementations.OrdersController;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try {
            Registry reg = LocateRegistry.createRegistry(6020);
            try {
                reg.bind(ILoginController.loginController_ID, new LoginController());
                reg.bind(IInfoController.infoController_ID, new InfoController());
                reg.bind(IOrdersController.ordersController_ID, new OrdersController());
            } catch (AlreadyBoundException | AccessException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
