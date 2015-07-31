package singleton;


import client.model.PrivateSession;

import java.util.HashMap;

/**
 * @class PrivateSessionManager
 * @desc Manages all private sessions on this server
 * there are one of these on each server
 */

public class PrivateSessionManager {
    private static PrivateSessionManager instance = null;
    private HashMap<Integer, PrivateSession> mSessions = new HashMap<>();
    private int mCurrentSessionID = -1;

    private PrivateSessionManager(){}

    public static PrivateSessionManager getInstance(){
        if(instance == null){
            instance = new PrivateSessionManager();
        }
        return instance;
    }

    public void addSession(PrivateSession s){
        this.mSessions.put(s.getSessionID(), s);
    }

    public void setNextSessionID(int sessionID){
        mCurrentSessionID = sessionID;
    }

    public int requestSessionID(){
        mCurrentSessionID += 1;
        ChannelManager.getInstance().writeToAllServers(new Object[]{"SessionIDSynchronisation", mCurrentSessionID});
        return mCurrentSessionID;
    }
}
