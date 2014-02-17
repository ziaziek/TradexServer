/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.przemo.tradexserver.helpers;

import com.przemo.tradex.data.Users;
import org.hibernate.Session;

/**
 *
 * @author Przemo
 */
public class UsersHelper {
    
    public static Users findUsersById(Session session, int uid){
        if(session!=null && session.isOpen()){
            return (Users) session.get(Users.class, uid);
        } else{
            return null;
        }
    }
    
    
}
