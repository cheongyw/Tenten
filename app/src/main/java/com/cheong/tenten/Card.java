package com.cheong.tenten;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Cheong on 23/5/2016.
 */
public class Card implements Comparable<Card> {

    private int m_value;
    private CharSequence m_ability;

    public Card(int value)
    {
        m_value = value;
        if (value==0||value==13||value==26) {
            m_ability = "Deactivate opponent's cards";
        }
        else if (value==39) {
            m_ability = "Restoration: Highest hand wins";
        }
        else if (value==33||value==46) {
            m_ability = "Opponent discards 2 random cards";
        }
        else if (value==34||value==47) {
            m_ability = "Draw 2 cards";
        }
        else if (value==35||value==48){
            m_ability = "Draw 3 cards";
        }
        else if (value==36||value==49) {
            m_ability = "Opponent discards 2 highest cards";
        }
        else if (value==37||value==50) {
            m_ability = "Opponent discards 3 highest cards";
        }
        else if (value==38) {
            m_ability = "Sudden death: Game ends next round";
    }
        else if (value==51) {
            m_ability = "Sabotage: Lowest hand wins";
        }
        else {
            m_ability = "None";
        }
    }

    // All cards in a deck
    private final static int[] allCards = new int[] {R.drawable.a1,
            R.drawable.a2,
            R.drawable.a3,
            R.drawable.a4,
            R.drawable.a5,
            R.drawable.a6,
            R.drawable.a7,
            R.drawable.a8,
            R.drawable.a9,
            R.drawable.a10,
            R.drawable.a11,
            R.drawable.a12,
            R.drawable.a13,
            R.drawable.b1,
            R.drawable.b2,
            R.drawable.b3,
            R.drawable.b4,
            R.drawable.b5,
            R.drawable.b6,
            R.drawable.b7,
            R.drawable.b8,
            R.drawable.b9,
            R.drawable.b10,
            R.drawable.b11,
            R.drawable.b12,
            R.drawable.b13,
            R.drawable.c1,
            R.drawable.c2,
            R.drawable.c3,
            R.drawable.c4,
            R.drawable.c5,
            R.drawable.c6,
            R.drawable.c7,
            R.drawable.c8,
            R.drawable.c9,
            R.drawable.c10,
            R.drawable.c11,
            R.drawable.c12,
            R.drawable.c13,
            R.drawable.d1,
            R.drawable.d2,
            R.drawable.d3,
            R.drawable.d4,
            R.drawable.d5,
            R.drawable.d6,
            R.drawable.d7,
            R.drawable.d8,
            R.drawable.d9,
            R.drawable.d10,
            R.drawable.d11,
            R.drawable.d12,
            R.drawable.d13
    };

    public int getRank() {
        return m_value;
    }

    public CharSequence getAbility() {
        return m_ability;
    }

    public void deactivate() {m_ability = "None";}

    private String getSuitText()
    {
        if (m_value<13) {
            return "CLUBS";
        }
        else if(m_value<26) {
            return "DIAMONDS";
        }
        else if(m_value<39) {
            return "HEARTS";
        }
        else {
            return "SPADES";
        }
    }

    public int getValue()
    {
        if (m_value<13) {
            return m_value + 1;
        }
        else if (m_value<26) {
            return m_value - 12;
        }
        else if (m_value<39) {
            return m_value - 25;
        }
        else {
            return m_value - 38;
        }
    }

    private String getValueText() {
        int temp;
        if (m_value<13) {
            temp = m_value + 1;
        }
        else if (m_value<26) {
            temp = m_value - 12;
        }
        else if (m_value<39) {
            temp = m_value - 25;
        }
        else {
            temp = m_value - 38;
        }
        String text = null;
        switch (temp) {
            case 1:
                text = "ACE";
                break;
            case 2:
                text = "TWO";
                break;
            case 3:
                text = "THREE";
                break;
            case 4:
                text = "FOUR";
                break;
            case 5:
                text = "FIVE";
                break;
            case 6:
                text = "SIX";
                break;
            case 7:
                text = "SEVEN";
                break;
            case 8:
                text = "EIGHT";
                break;
            case 9:
                text = "NINE";
                break;
            case 10:
                text = "TEN";
                break;
            case 11:
                text = "JACK";
                break;
            case 12:
                text = "QUEEN";
                break;
            case 13:
                text = "KING";
        }
        return text;
    }

    @Override
    public String toString() {
        return getValueText() + " of " + getSuitText();
    }

    public int getImage() {
        return allCards[m_value];
    }

    @Override
    public int compareTo(Card other) {
        if (this.getValue() < other.getValue()) {
            return -1;
        }
        else if (this.getValue() > other.getValue()) {
            return 1;
        }
        // break ties with suit
        else if (this.getRank() < other.getRank()) {
            return -1;
        }
        else if (this.getRank() > other.getRank()) {
            return 1;
        }
        else {
            return 0;
        }
    }
}
