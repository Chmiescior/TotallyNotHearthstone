package game.classes;

import game.interfaces.IDeckElement;

import java.util.LinkedList;
import java.util.List;

public class Engine
{
    private final Player player1 = new Player(true);
    private final Player player2 = new Player(false);

    private final List<Minion> battlefield1 = new LinkedList<>();
    private final List<Minion> battlefield2 = new LinkedList<>();

    private final List<IDeckElement> discarded1 = new LinkedList<>();
    private final List<IDeckElement> discarded2 = new LinkedList<>();

    private int roundCounter;
    private boolean playerOneTurn;
    private final int numberOfStartingCards;

    public Engine()
    {
        this.player1.generateDeck(true);
        this.player2.generateDeck(false);
        this.roundCounter = 0;
        this.playerOneTurn = true;
        this.numberOfStartingCards = 3;
        this.nextTurn();
    }

    public void nextTurn()
    {
        this.roundCounter++;
        this.playerOneTurn = !this.playerOneTurn;
        unfreezeMinions();

        if(roundCounter % 2 == 1)
        {
            player1.refillMana();
            if(roundCounter == 1)
                player1.drawMultipleCards(numberOfStartingCards);
            else
                player1.drawCard();
            player1.addMaxMana();
        }
        else
        {
            player2.refillMana();
            if(roundCounter == 2)
                player2.drawMultipleCards(numberOfStartingCards);
            else
                player2.drawCard();
            player2.addMaxMana();
        }
    }

    private void unfreezeMinions()
    {
        if(this.playerOneTurn)
            for(Minion minion : battlefield1)
            {
                if(minion.isFrozen())
                {
                    if (minion.isToBeUnfrozen())
                        minion.unfreeze();
                    else
                        minion.unfreezeNextRound();
                }
                else
                    minion.letAttack();
            }
        else
            for(Minion minion : battlefield2)
            {
                if (minion.isFrozen())
                {
                    if (minion.isToBeUnfrozen())
                        minion.unfreeze();
                    else
                        minion.unfreezeNextRound();
                }
                else
                    minion.letAttack();
            }
    }

    public void summonMinion(Minion summon)
    {
        if(this.playerOneTurn)
        {
            if(battlefield1.size() < 7 && player1.getCurrMana() >= summon.getManaCost())
            {
                battlefield1.add(summon);
                player1.removeCardFromHand(summon);
                player1.drainMana(summon.getManaCost());
            }
        }
        else
        {
            if(battlefield2.size() < 7 && player2.getCurrMana() >= summon.getManaCost())
            {
                battlefield2.add(summon);
                player2.removeCardFromHand(summon);
                player2.drainMana(summon.getManaCost());
            }
        }
    }

    private boolean isAttackOnPlayerPossible(IDeckElement e1, Player target)
    {
        if(!e1.isMinion())
            return true;
        else
        {
            Minion attacker = (Minion) e1;

            if(!attacker.canAttack() || this.playerOneTurn == target.isPlayerOne())
                return false;

            if(this.playerOneTurn)
            {
                if(!battlefield1.contains(attacker))
                    return false;

                for(Minion minion : battlefield2)
                    if(minion.isTaunt())
                        return false;
            }
            else
            {
                if(!battlefield2.contains(attacker))
                    return false;

                for(Minion minion : battlefield1)
                    if(minion.isTaunt())
                        return false;
            }
        }
        return true;
    }

    public boolean attackPlayer(IDeckElement e1, Player target)
    {
        if(isAttackOnPlayerPossible(e1, target))
        {
            if(e1.isMinion())
            {
                Minion attacker = (Minion) e1;
                attacker.makeExhausted();
                target.dealDamage(attacker.getAttackPower());
                if(attacker.isInStealth())
                    attacker.unStealth();
            }
            else
            {
                Spell spell = (Spell) e1;
                if(spell.isInDeckOne())
                    castSpell(spell, player1, target);
                else
                    castSpell(spell, player2, target);
            }
        }
        else
            return false;
        return true;
    }

    public boolean isAttackOnMinionPossible(IDeckElement e1, IDeckElement e2)
    {
        if(!e2.isMinion())
            return false;
        else
        {
            Minion target  = (Minion) e2;
            if(target.isInStealth())
                return false;

            if(!target.isTaunt())
            {
                if(this.playerOneTurn)
                {
                    for (Minion minion : battlefield2)
                        if (minion.isTaunt())
                            return false;
                }
                else
                {
                    for (Minion minion : battlefield1)
                        if(minion.isTaunt())
                            return false;
                }
            }

            if(e1.isMinion())
            {
                Minion attacker = (Minion) e1;
                if(target.isInDeckOne() == attacker.isInDeckOne() || this.playerOneTurn != attacker.isInDeckOne() || !attacker.canAttack())
                    return false;
                if(attacker.isInStealth())
                    attacker.unStealth();
            }
            else
            {
                Spell spell = (Spell) e1;
                if(playerOneTurn != spell.isInDeckOne())
                    return false;
            }
            return true;
        }
    }

    private void castSpell(Spell spell, Player caster, Minion target)
    {
        if(spell.isHeal())
            target.heal(spell.getHealPower());
        else
            target.dealDamage(spell.getAttackPower());

        if(spell.canFreeze())
            target.freeze();
        if(spell.canSilence())
            target.silence();
        if(spell.isDrawCard())
            caster.drawCard();

        caster.removeCardFromHand(spell);
    }

    private void castSpell(Spell spell, Player caster, Player target)
    {
        if(spell.isHeal())
            target.heal(spell.getHealPower());
        else
            target.dealDamage(spell.getAttackPower());

        if(spell.isDrawCard())
            caster.drawCard();
        caster.removeCardFromHand(spell);
    }

    public boolean attackMinion(IDeckElement e1, IDeckElement e2)
    {
        if(isAttackOnMinionPossible(e1, e2))
        {
            Minion target = (Minion) e2;

            if(e1.isMinion())
            {
                Minion attacker = (Minion) e1;

                target.dealDamage(attacker.getAttackPower());
                attacker.dealDamage(target.getAttackPower());
                attacker.makeExhausted();

                if(target.isDead())
                    discard(target);
                if(attacker.isDead())
                    discard(attacker);
            }
            else
            {
                Spell spell = (Spell) e1;

                if(spell.isInDeckOne())
                    castSpell(spell, player1, target);
                else
                    castSpell(spell, player2, target);


                if(target.isDead())
                    discard(target);
            }
        }
        else
            return false;
        return true;
    }

    private void discard(IDeckElement e)
    {
        if(e.isMinion())
        {
            Minion minion = (Minion) e;
            if(minion.isInDeckOne())
            {
                battlefield1.remove(minion);
                discarded1.add(minion);
            }
            else
            {
                battlefield2.remove(minion);
                discarded2.add(minion);
            }
        }
    }

    public boolean isPlayerOneTurn()
    {
        return playerOneTurn;
    }

    public Player getPlayer1()
    {
        return player1;
    }

    public Player getPlayer2()
    {
        return player2;
    }

    public List<Minion> getBattlefield1()
    {
        return battlefield1;
    }

    public List<Minion> getBattlefield2()
    {
        return battlefield2;
    }
}
