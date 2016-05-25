package com.cheong.tenten;

/**
 * Created by Cheong on 23/5/2016.
 */
public class Card {

    private boolean faceup;
    private int m_value;

    public Card(int value)
    {
        m_value = value;
        faceup = false;
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
            R.drawable.a13
    };

    private String getSuitText(int value)
    {
        if (value<13) {
            return "CLUBS";
        }
        else if(value<26) {
            return "DIAMONDS";
        }
        else if(value<39) {
            return "HEARTS";
        }
        else {
            return "SPADES";
        }
    }

    public int getValue()
    {
        int temp = (m_value + 1) % 14;
        if (temp == 0) {
            temp = 13;
        }
        return temp;
    }

    private String getValueText(int value) {
        int temp = (value + 1) % 14;
        if (temp == 0) {
            temp = 13;
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
        return getValueText(m_value) + " of " + getSuitText(m_value);
    }

    public boolean isFaceUp() {
        return faceup;
    }

    public void flip() {
        faceup = !faceup;
    }

    public int getImage() {
        return allCards[m_value];
    }

}
