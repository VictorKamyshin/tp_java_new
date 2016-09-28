package ru.mail.park.services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * ДЗ: реализовать класс, который по соответствующему id сессии будет отдавать пользователей(т.е. реализовать авторизацию по сессии)
 *
 */
@Service
public class SessionService {
    private Map<String, String> SessionIdTologin = new HashMap<>();

    public String addSession(String sessionId, String login) {
        return SessionIdTologin.put(sessionId, login);
    }

    public String getLogin(String sessionId) {
        return SessionIdTologin.get(sessionId);
    }

    public String removeLogin(String sessionId) { return SessionIdTologin.remove(sessionId); }
    //To be done
}
