package game.classes;

import game.interfaces.IDeckElement;

public class Spell implements IDeckElement
{
    private final int manaCost;
    private final int attackPower;
    private final int healPower;

    private final boolean canSilence;
    private final boolean canFreeze;
    private final boolean drawCard;

    private final boolean inDeckOne;

    private final boolean isMinion = false;

    public Spell(int manaCost, int attackPower, boolean canSilence, boolean canFreeze, boolean drawCard, boolean inDeckOne)
    {
        this.manaCost = manaCost;
        this.attackPower = attackPower;
        this.healPower = -attackPower;
        this.canSilence = canSilence;
        this.canFreeze = canFreeze;
        this.drawCard = drawCard;
        this.inDeckOne = inDeckOne;
    }

    public int getManaCost()
    {
        return manaCost;
    }

    public int getAttackPower()
    {
        return attackPower;
    }

    public int getHealPower()
    {
        return healPower;
    }

    public boolean canFreeze()
    {
        return canFreeze;
    }

    public boolean canSilence()
    {
        return canSilence;
    }

    public boolean isDrawCard()
    {
        return drawCard;
    }

    public int getAmountOfAbilities()
    {
        int counter = 0;
        if(this.drawCard)
            counter++;
        if(this.canSilence)
            counter++;
        if(this.canFreeze)
            counter++;
        return counter;
    }

    public boolean isInDeckOne()
    {
        return inDeckOne;
    }

    public boolean isHeal()
    {
        return this.healPower > 0;
    }

    @Override
    public boolean isMinion()
    {
        return isMinion;
    }

    @Override
    public String toString()
    {
        String damageOrHeal;
        int multiplier = 1;
        if(this.attackPower < 0)
        {
            damageOrHeal = " | Heal: ";
            multiplier = -1;
        }
        else
            damageOrHeal = " | Attack power: ";

        return "Mana cost: " + this.manaCost + damageOrHeal + multiplier * this.attackPower + this.abilitiesToString();
    }

    private String abilitiesToString()
    {
        String base = " | ";
        String result = base;

        if(this.canSilence)
        {
            result += "Silence";
            if(this.canFreeze || this.drawCard)
                result += " | ";
        }
        if(this.canFreeze)
        {
            result += "Freeze";
            if(this.drawCard)
                result += " | ";
        }
        if(this.drawCard)
        {
            result += "Draw card";
        }

        if(!result.equals(base))
            return result;
        else
            return "";
    }
}
