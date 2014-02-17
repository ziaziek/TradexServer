/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.przemo.tradexserver.helpers;

import com.przemo.tradex.data.Accounts;
import com.przemo.tradex.data.Users;
import com.przemo.tradexserver.implementations.DataRequestController;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author Przemo
 */
public class AccountsHelper {
    
     public static List<Accounts> findAccountsByUsers(Session s, Users uid){
        if(s!=null && s.isOpen()){
            return  s.createQuery("from Accounts where users.id=:ui").setParameter("ui", uid.getId()).list();
        } else{
            return null;
        }
    }
     
     public static List<Accounts> findAccountsBySessionId(Session s, String ssid){
         if(s!=null && s.isOpen()){
             Users u = UserSessionsHelper.findSessionBySessionId(s, ssid).getUsers();
             if(u!=null){
                 return findAccountsByUsers(s, u);
             }
         }
         return null;
     }
}
