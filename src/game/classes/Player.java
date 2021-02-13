package game.classes;

import game.interfaces.IDeckElement;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Player
{
    public final int MAX_HEALTH = 30;
    public final int MAX_DECK_SIZE = 40;
    public final int MAX_MANA_AMOUNT = 10;
    private int health;
    private int mana = 1;
    private int maxMana = 1;

    private final boolean isPlayerOne;

    private final List<IDeckElement> deck = new LinkedList<>();
    private final List<IDeckElement> hand = new LinkedList<>();

    public Player(boolean isPlayerOne)
    {
        this.health = this.MAX_HEALTH;
        this.isPlayerOne = isPlayerOne;
    }

    public void drawCard()
    {
        if(this.hand.size() < 9 && this.deck.size() > 0)
        {
            int i = ThreadLocalRandom.current().nextInt(0, this.deck.size());
            hand.add(deck.get(i));
            deck.remove(i);
        }
    }

    public void drawMultipleCards(int numberOfCards)
    {
        for(int i = 0; i < numberOfCards; i++)
            this.drawCard();
    }

    private Minion generateRandomMinion(boolean inDeckOne, int manaCost)
    {
        int attackPower = manaCost;
        int maxHealth = manaCost;
        int subtrahend = ThreadLocalRandom.current().nextInt(-manaCost + 1, manaCost);
        attackPower -= subtrahend;
        maxHealth += subtrahend;

        boolean[] abilities = new boolean[3];

        for(int i = 0; i < manaCost / 4; i++)
        {
            int index = ThreadLocalRandom.current().nextInt(0, 3);
            while(true)
            {
                if((abilities[0] && index == 2) || (abilities[2] && index == 0))
                {
                    index = ThreadLocalRandom.current().nextInt(0, 3);
                    continue;
                }
                if(!abilities[index])
                {
                    abilities[index] = true;
                    break;
                }
                index = ThreadLocalRandom.current().nextInt(0, 3);
            }
        }
        return new Minion(manaCost, attackPower, maxHealth, abilities[0], abilities[1], abilities[2], inDeckOne);
    }

    private Spell generateRandomSpell(boolean inDeckOne, int manaCost)
    {
        int attackPower = manaCost + 2;
        int amountOfAbilities = ThreadLocalRandom.current().nextInt(0, 4);
        boolean[] abilities = new boolean[3];

        for(int i = 0; i < amountOfAbilities; i++)
        {
            while(true)
            {
                int index = ThreadLocalRandom.current().nextInt(0, 3);
                if (!abilities[index])
                {
                    abilities[index] = true;
                    attackPower -= 2;
                    break;
                }
            }
        }
        return new Spell(manaCost, attackPower, abilities[0], abilities[1], abilities[2], inDeckOne);
    }

    public void generateDeck(boolean inDeckOne)
    {
        for (int i = 0; i < ((MAX_DECK_SIZE * 3) / 4) - 1; i++)
        {
            deck.add(generateRandomMinion(inDeckOne, i % 8 + 1));
        }
        deck.add(generateRandomMinion(inDeckOne, 9));
        deck.add(generateRandomMinion(inDeckOne, 9));
        deck.add(generateRandomMinion(inDeckOne, 10));

        for (int i = 1; i <= 8; i++)
        {
            deck.add(generateRandomSpell(inDeckOne, i));
        }
    }

    public boolean isDead()
    {
        return this.health <= 0;
    }

    public boolean isPlayerOne()
    {
        return this.isPlayerOne;
    }

    public void dealDamage(int subtrahend)
    {
        this.health -= subtrahend;
    }

    public void heal(int component)
    {
        this.health += component;
        if(this.health > this.MAX_HEALTH)
            this.health = this.MAX_HEALTH;
    }

    public void removeCardFromHand(IDeckElement e)
    {
        this.hand.remove(e);
    }

    public List<IDeckElement> getHand()
    {
        return this.hand;
    }

    public int getCurrMana()
    {
        return mana;
    }

    public int getMaxMana()
    {
        return maxMana;
    }

    public String getHealthToString()
    {
        return "<html>" + this.health + "/" + this.MAX_HEALTH + "</html>";
    }

    public void refillMana()
    {
        this.mana = this.maxMana;
    }

    public void addMaxMana()
    {
        if(this.maxMana < MAX_MANA_AMOUNT)
            this.maxMana += 1;
    }

    public void drainMana(int subtrahend)
    {
        this.mana -= subtrahend;
    }
}
