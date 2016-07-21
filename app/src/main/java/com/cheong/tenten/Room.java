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
    public String[] turnArray;
    public int turnNo;
    public ArrayList<Integer> drawnCards;
    public boolean suddendeathMode;
    public int suddendeathCount;
    public int winCondition;
    public ArrayList<Boolean> readytoStart;


    public Room(){}

    public Room(String name, int nUsers, String creatorName){
        gameStarted = false;
        gameEnded = false;
        nPlayers = nUsers;
        roomName = name;
        turnArray = new String[nUsers];
        turnNo = 0;
        drawnCards = new ArrayList<Integer>();
        suddendeathMode = false;
        suddendeathCount = 2;
        winCondition = 0;
        readytoStart = new ArrayList<Boolean>();
        readytoStart.add(false); //dummy so it will not disappear in database

        players = new HashMap<String, HashMap<String, Object>>();
        HashMap<String, Object> creator = new HashMap<String, Object>();
        creator.put("score", 0);
        creator.put("cards", new ArrayList<Integer>());
        players.put(creatorName, creator);

    }
    public Map<String, Object> toMap(){
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("roomName", roomName);
        result.put("gameStarted", gameStarted);
        result.put("gameEnded", gameEnded);
        result.put("nPlayers", nPlayers);
        result.put("players", players);
        result.put("turnNo", turnNo);

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
    public String[] turnArray() {return turnArray;}
    public int turnNo() {return turnNo;}
    public ArrayList<Integer> drawnCards() {return drawnCards;}
    public boolean suddendeathMode() {return suddendeathMode;}
    public int suddendeathCount() {return suddendeathCount;}
    public int winCondition() {return winCondition;}
    public ArrayList<Boolean> readytoStart() {return readytoStart;}


    //setters
    public void setGameStarted(boolean bool) {gameStarted = bool;}
    public void setGameEnded(boolean bool) {gameEnded = bool;}
    public void setPlayers(HashMap<String, HashMap<String, Object>> newPlayers) {
        players = newPlayers;
    }
    public void setTurnArray(String[] newArray) {turnArray = newArray;}
    public void nextTurn() {
        if (nPlayers == 2) {
            if (turnNo == 0) {turnNo = 1;}
            else {turnNo = 0;}
        }
        else if (nPlayers == 4) {
            if (turnNo == 3) {turnNo = 0;}
            else {turnNo += 1;}
        }
    }
    public void setDrawnCards(ArrayList<Integer> allDrawn) {drawnCards = allDrawn;}
    public void setSuddenDeathMode(boolean bool) {suddendeathMode = bool;}
    public void minusSuddenDeathCount(){suddendeathCount -= 1;}
    public void setWinCondition(int i){winCondition = i;}
    public void playerReady() {readytoStart.add(true);}

}
