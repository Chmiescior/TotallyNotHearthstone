package game.classes;

import game.interfaces.IDeckElement;

public class Minion implements IDeckElement
{
    private final int maxHealth;
    private int currentHealth;
    private final int manaCost;
    private final int attackPower;

    private boolean isTaunt;
    private boolean isCharge;
    private boolean isInStealth;

    private boolean canAttack;
    private final boolean inDeckOne;
    private boolean isFrozen;
    private boolean unfreeze;

    private final boolean isMinion = true;

    public Minion(int manaCost, int attackPower, int maxHealth, boolean isTaunt, boolean isCharge, boolean isInStealth, boolean inDeckOne)
    {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.manaCost = manaCost;
        this.attackPower = attackPower;
        this.isTaunt = isTaunt;
        this.isCharge = isCharge;
        this.isInStealth = isInStealth;
        this.inDeckOne = inDeckOne;
        this.isFrozen = false;
        this.unfreeze = false;

        this.canAttack = this.isCharge;
    }

    public boolean canAttack()
    {
        return canAttack;
    }

    public boolean isInDeckOne()
    {
        return inDeckOne;
    }

    public boolean isInStealth()
    {
        return isInStealth;
    }

    public void freeze()
    {
        isFrozen = true;
        unfreeze = false;
        makeExhausted();
    }

    public void unfreeze()
    {
        if(this.unfreeze)
        {
            isFrozen = false;
            this.letAttack();
        }
    }

    public void letAttack()
    {
        this.canAttack = true;
    }

    public void silence()
    {
        this.isCharge = false;
        this.isTaunt = false;
        this.isInStealth = false;
        this.unfreeze();
    }

    public void makeExhausted()
    {
        this.canAttack = false;
    }

    public int getMaxHealth()
    {
        return maxHealth;
    }

    public int getAttackPower()
    {
        return attackPower;
    }

    public int getManaCost()
    {
        return manaCost;
    }

    public int getCurrentHealth()
    {
        return currentHealth;
    }

    @Override
    public boolean isMinion()
    {
        return isMinion;
    }

    public boolean isTaunt()
    {
        return this.isTaunt;
    }

    public boolean isCharge()
    {
        return this.isCharge;
    }

    public boolean isFrozen()
    {
        return isFrozen;
    }

    public boolean isToBeUnfrozen()
    {
        return unfreeze;
    }

    public void unfreezeNextRound()
    {
        this.unfreeze = true;
    }

    public void dealDamage(int subtrahend)
    {
        this.currentHealth -= subtrahend;
    }

    public void heal(int component)
    {
        this.currentHealth += component;
        if(this.currentHealth > this.maxHealth)
            this.currentHealth = maxHealth;
    }

    public void unStealth()
    {
        this.isInStealth = false;
    }

    public int getAmountOfAbilities()
    {
        int counter = 0;
        if(this.isTaunt())
            counter++;
        else if(this.isInStealth())
            counter++;
        if(this.isCharge())
            counter++;
        return counter;
    }

    public boolean isDead()
    {
        return this.currentHealth <= 0;
    }


    public String abilitiesToString()
    {
        String base = " | ";
        String result = base;

        if(this.isTaunt)
        {
            result += "Taunt";
            if(this.isCharge)
                result += " | ";
        }
        if(this.isCharge)
        {
            result += "Charge";
            if(this.isInStealth)
                result += " | ";
        }
        if(this.isInStealth)
            result += "Stealth";

        if(!result.equals(base))
            return result;
        else
            return "";
    }

    @Override
    public String toString()
    {
        return "Mana cost: " + this.manaCost + " | Attack: " + this.attackPower + " | Health: " + this.maxHealth
                + this.abilitiesToString();
    }
}
