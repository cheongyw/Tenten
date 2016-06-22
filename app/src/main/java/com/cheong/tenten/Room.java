package com.cheong.tenten;

/**
 * Created by Sherry on 17/06/2016.
 */
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

public class Room {
    public boolean gameStarted;
    public boolean gameEnded;
    public int nPlayers;
    public String roomName;
    public HashMap<String, HashMap<String, Object>> players;

    public Room(){}

    public Room(String name, int nUsers, String creatorName){
        gameStarted = false;
        gameEnded = false;
        nPlayers = nUsers;
        roomName = name;

        players = new HashMap<String, HashMap<String, Object>>();
        HashMap<String, Object> creator = new HashMap<String, Object>();
        creator.put("score", 0);
        creator.put("cards", new ArrayList<Card>());
        players.put(creatorName, creator);

    }
    public Map<String, Object> toMap(){
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("roomName", roomName);
        result.put("gameStarted", gameStarted);
        result.put("gameEnded", gameEnded);
        result.put("nPlayers", nPlayers);
        result.put("players", players);

        return result;
    }

    //getters
    public boolean gameStarted(){
        return gameStarted;
    }
    public boolean gameEnded(){
        return gameEnded;
    }
    public int nPlayers(){
        return nPlayers;
    }
    public String roomName(){
        return roomName;
    }
    public HashMap<String, HashMap<String, Object>> players(){
        return players;
    }

}
