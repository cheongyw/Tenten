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
    public List<HashMap<String, Object>> players;

    public Room(){}

    public Room(String name, int nUsers, String creatorName){
        gameStarted = false;
        gameEnded = false;
        nPlayers = nUsers;
        roomName = name;

        players = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> creator = new HashMap<String, Object>();
        creator.put("username", creatorName);
        creator.put("score", 0);
        creator.put("cards", new ArrayList<Card>());
        players.add(creator);

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

}
