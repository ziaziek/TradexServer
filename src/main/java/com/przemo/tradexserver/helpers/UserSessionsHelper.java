/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.przemo.tradexserver.helpers;

import com.przemo.tradex.data.UserSessions;
import org.hibernate.Session;

/**
 *
 * @author Przemo
 */
public class UserSessionsHelper {
    
    public static UserSessions findSessionBySessionId(Session s, String ssid){
        if(s!=null && s.isOpen()){
            return (UserSessions) s.createQuery("from UserSessions where sessionKey=:ssid").setParameter("ssid", ssid).uniqueResult();
        } else {
            return null;
        }
    }
}
