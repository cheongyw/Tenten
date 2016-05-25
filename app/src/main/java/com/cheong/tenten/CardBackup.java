package com.cheong.tenten;

import android.graphics.Color;
import android.util.SparseArray;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Cheong on 23/5/2016.
 */
public class CardBackup {

    public enum Suit {
        CLUBS    (1, "Clubs"),
        DIAMONDS (2, "Diamonds"),
        HEARTS   (3, "Hearts"),
        SPADES   (4, "Spades");

        private final int s_value;
        private final String s_text;

        Suit(int value, String text) {
            this.s_value = value;
            this.s_text = text;
        }

        public int getValue() {return s_value;}

        public String getText() {return s_text;}

        public static final SparseArray<Suit> SUIT_MAP = new SparseArray<Suit>(4);
        static {
            for (Suit suit: Suit.values()) {
                SUIT_MAP.put(suit.s_value, suit);
            }
        }

    }

    public enum Rank {
        ACE(1, "Ace"),
        TWO(2, "Two"),
        THREE(3, "Three"),
        FOUR(4, "Four"),
        FIVE(5, "Five"),
        SIX(6, "Six"),
        SEVEN(7, "Seven"),
        EIGHT(8, "Eight"),
        NINE(9, "Nine"),
        TEN(10, "Ten"),
        JACK(11, "Jack"),
        QUEEN(12, "Queen"),
        KING(13, "King");

        private final int r_value;
        private final String r_text;

        Rank(int value, String text) {
            this.r_value = value;
            this.r_text = text;
        }

        public int getValue() {return r_value;}

        public String getText() {return r_text;}

        public static final SparseArray<Rank> RANK_MAP = new SparseArray<Rank>(13);
        static {
            for (Rank rank: Rank.values()) {
                RANK_MAP.put(rank.r_value, rank);
            }
        }
    }

    private Suit m_suit;
    private Rank m_rank;
    private boolean faceup;

    public CardBackup(int rank, int suit)
    {
        m_rank = Rank.RANK_MAP.get(rank);
        m_suit = Suit.SUIT_MAP.get(suit);
        faceup = false;
    }

    public void setSuit(int suit)
    {
        m_suit = Suit.SUIT_MAP.get(suit);
    }

    public void setRank(int rank)
    {
        m_rank = Rank.RANK_MAP.get(rank);
    }

    public Suit getSuit()
    {
        return m_suit;
    }

    public Rank getRank()
    {
        return m_rank;
    }

    public void externalize(DataOutputStream out) throws IOException
    {
        out.writeByte(m_suit.getValue());
        out.writeByte(m_rank.getValue());
    }

    public void internalize(DataInputStream in) throws IOException
    {
        m_suit = Suit.SUIT_MAP.get(in.readByte());
        m_rank = Rank.RANK_MAP.get(in.readByte());
    }

    @Override
    public String toString() {
        return m_rank.getText() + " of " + m_suit.getText();
    }


    private final static int width = 50;
    private final static int height = 70;

    public boolean isFaceUp() {
        return faceup;
    }

    public void flip() {
        faceup = !faceup;
    }

}

