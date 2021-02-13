package visualizer;

import game.classes.Engine;
import game.classes.Minion;
import game.classes.Spell;
import game.interfaces.IDeckElement;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class GameFrame extends JFrame
{
    private JButton player1;
    private JPanel hand1;
    private JPanel battlefield1;
    private JPanel manaPool1;

    private JButton player2;
    private JPanel hand2;
    private JPanel battlefield2;
    private JPanel manaPool2;

    private JButton endTurn;

    private final Engine engine;

    private final double minionScale = 0.63;
    private final double spellScale = 0.168;
    private final double backsideScale = 0.158;
    private final double playerScale = 0.45;
    private final double summonScale = 0.33;
    private final double crystalScale = 0.58;

    private final ImageIcon minionBackground = getScaledImage(new ImageIcon(GameFrame.class.getResource("minionFront.png")), minionScale);
    private final ImageIcon spellBackground = getScaledImage(new ImageIcon(GameFrame.class.getResource("spellFront (old version).png")), spellScale);
    private final ImageIcon cardBackside = getScaledImage(new ImageIcon(GameFrame.class.getResource("cardBack.png")), backsideScale);
    private final ImageIcon playerBackground = getScaledImage(new ImageIcon(GameFrame.class.getResource("heroFrame.png")), playerScale);
    private final ImageIcon summonBackGround = getScaledImage(new ImageIcon(GameFrame.class.getResource("minionTemplate.png")), summonScale);
    private final ImageIcon manaCrystal = getScaledImage(new ImageIcon(GameFrame.class.getResource("manaCrystal.png")), crystalScale);

    private boolean clicked = false;
    private int clickedSummonIndex = -1;
    private int clickedSpellIndex = -1;

    public GameFrame()
    {
        //set Frame parameters
        this.setTitle("Totally not Hearthstone");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setSize(1800, 1100);
        ImageIcon gameIcon = new ImageIcon(GameFrame.class.getResource("logo.png"));
        this.setIconImage(gameIcon.getImage());


        //initialize engine
        this.engine = new Engine();

        createLayout();

        this.setVisible(true);
        updateGameFrame();
        endTurn();
    }

    public void endTurn()
    {
        this.engine.nextTurn();
        updateGameFrame();
    }

    private void createLayout()
    {
        int currY = 0;

        Dimension handDimension = new Dimension(1650, 241);
        hand2 = new JPanel();
        hand2.setBounds(0, currY, handDimension.width, handDimension.height);
        hand2.setPreferredSize(handDimension);
        hand2.setLayout(new FlowLayout());
        hand2.setBackground(Color.GRAY);
        hand2.setVisible(true);
        this.add(hand2);

        currY += handDimension.height;

        Dimension playerDimension = new Dimension(120, 130);
        player2 = new JButton(engine.getPlayer2().getHealthToString(), playerBackground);
        player2.setBounds(775, currY, playerDimension.width, playerDimension.height);
        player2.setPreferredSize(playerDimension);
        player2.setLayout(null);
        player2.setVerticalTextPosition(JButton.CENTER);
        player2.setHorizontalTextPosition(JButton.CENTER);
        player2.setForeground(Color.RED);
        player2.setFont(new Font("Arial Black", Font.BOLD, 20));
        player2.setVisible(true);
        player2.setBackground(Color.WHITE);
        player2.setBorderPainted(false);
        player2.addActionListener(e ->
        {
            if (clicked)
            {
                if (clickedSummonIndex != -1)
                {
                    if (!engine.attackPlayer(engine.getBattlefield1().get(clickedSummonIndex), engine.getPlayer2()))
                        JOptionPane.showMessageDialog(this, "Illegal move. Try again");
                    clicked = false;
                    clickedSummonIndex = -1;

                }
                else
                {
                    if(engine.isPlayerOneTurn())
                        engine.attackPlayer(engine.getPlayer1().getHand().get(clickedSpellIndex), engine.getPlayer2());
                    else
                        engine.attackPlayer(engine.getPlayer2().getHand().get(clickedSpellIndex), engine.getPlayer2());
                    clicked = false;
                }
            }
            updateGameFrame();
        });
        this.add(player2);

        Dimension crystalDimension = new Dimension(manaCrystal.getIconWidth(), manaCrystal.getIconHeight());
        manaPool2 = new JPanel();
        manaPool2.setLayout(new FlowLayout(FlowLayout.LEFT));
        manaPool2.setBounds(895, currY, (int) crystalDimension.getWidth() * 11, (int) crystalDimension.getHeight());
        manaPool2.setPreferredSize(new Dimension((int) crystalDimension.getWidth() * 11, (int) crystalDimension.getHeight()));
        manaPool2.setBorder(new LineBorder(Color.BLUE, 3));
        for (int i = 0; i < engine.getPlayer2().getCurrMana(); i++)
        {
            JLabel temp = new JLabel(manaCrystal);
            temp.setPreferredSize(crystalDimension);
            temp.setVisible(true);
            manaPool2.add(temp);
        }
        manaPool2.setVisible(true);
        this.add(manaPool2);

        currY += playerDimension.height;

        Dimension battlefieldDimension = new Dimension(1650, 135);
        battlefield2 = new JPanel();
        battlefield2.setBounds(0, currY, battlefieldDimension.width, battlefieldDimension.height);
        battlefield2.setPreferredSize(battlefieldDimension);
        battlefield2.setLayout(new FlowLayout());
        battlefield2.setBackground(new Color(153, 129, 112));
        battlefield2.setVisible(true);
        //battlefield2.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
        this.add(battlefield2);

        currY += battlefieldDimension.height;

        Dimension endTurnDimension = new Dimension(150, 70);
        endTurn = new JButton("End Turn");
        endTurn.setBounds(1650, 465, endTurnDimension.width, endTurnDimension.height);
        endTurn.setPreferredSize(endTurnDimension);
        endTurn.setBackground(Color.YELLOW);
        endTurn.setForeground(Color.BLACK);
        endTurn.setFont(new Font("Arial Black", Font.BOLD, 20));
        endTurn.setBorder(new LineBorder(Color.BLACK, 4));
        endTurn.addActionListener(e -> {
            this.endTurn();
            this.clicked = false;
        });
        this.add(endTurn);

        battlefield1 = new JPanel();
        battlefield1.setBounds(0, currY, battlefieldDimension.width, battlefieldDimension.height);
        battlefield1.setPreferredSize(battlefieldDimension);
        battlefield1.setLayout(new FlowLayout());
        battlefield1.setBackground(new Color(153, 129, 112));
        battlefield1.setVisible(true);
        this.add(battlefield1);

        currY += battlefieldDimension.height;

        player1 = new JButton(engine.getPlayer1().getHealthToString(), playerBackground);
        player1.setBounds(775, currY, playerDimension.width, playerDimension.height);
        player1.setPreferredSize(playerDimension);
        player1.setLayout(null);
        player1.setVerticalTextPosition(JButton.CENTER);
        player1.setHorizontalTextPosition(JButton.CENTER);
        player1.setForeground(Color.RED);
        player1.setFont(new Font("Arial Black", Font.BOLD, 20));
        player1.setVisible(true);
        player1.setBackground(Color.WHITE);
        player1.setBorderPainted(false);
        player1.addActionListener(e ->
        {
            if (clicked)
                if (clickedSummonIndex != -1)
                {
                    if (!engine.attackPlayer(engine.getBattlefield2().get(clickedSummonIndex), engine.getPlayer1()))
                        JOptionPane.showMessageDialog(this, "Illegal move. Try again");
                    clicked = false;
                    clickedSummonIndex = -1;
                } else
                {
                    if(engine.isPlayerOneTurn())
                        engine.attackPlayer(engine.getPlayer1().getHand().get(clickedSpellIndex), engine.getPlayer1());
                    else
                        engine.attackPlayer(engine.getPlayer2().getHand().get(clickedSpellIndex), engine.getPlayer1());
                    clicked = false;
                    clickedSpellIndex = -1;
                }
            updateGameFrame();
        });
        this.add(player1);

        currY += playerDimension.height;

        manaPool1 = new JPanel();
        manaPool1.setLayout(new FlowLayout(FlowLayout.LEFT));
        manaPool1.setBounds(895, currY - (int) crystalDimension.getHeight(), (int) crystalDimension.getWidth() * 11, (int) crystalDimension.getHeight());
        manaPool1.setPreferredSize(new Dimension((int) crystalDimension.getWidth() * 11, (int) crystalDimension.getHeight()));
        manaPool1.setBorder(new LineBorder(Color.BLUE, 3));
        for (int i = 0; i < engine.getPlayer2().getCurrMana(); i++)
        {
            JLabel temp = new JLabel(manaCrystal);
            temp.setPreferredSize(crystalDimension);
            temp.setVisible(true);
            manaPool1.add(temp);
        }
        manaPool1.setVisible(true);
        this.add(manaPool1);

        hand1 = new JPanel();
        hand1.setBounds(0, currY, handDimension.width, handDimension.height);
        hand1.setPreferredSize(handDimension);
        hand1.setLayout(new FlowLayout());
        hand1.setBackground(Color.GRAY);
        hand1.setVisible(true);
        this.add(hand1);
    }

    private void updateBothHands()
    {
        hand1.removeAll();
        hand2.removeAll();
        for(IDeckElement deckElement : engine.getPlayer1().getHand())
            hand1.add(visualiseCard(deckElement));
        for(IDeckElement deckElement : engine.getPlayer2().getHand())
            hand2.add(visualiseCard(deckElement));
        hand1.revalidate();
        hand1.repaint();
        hand2.revalidate();
        hand2.repaint();
    }

    private void updateBoard()
    {
        battlefield1.removeAll();
        battlefield2.removeAll();
        for(Minion summon : engine.getBattlefield1())
            battlefield1.add(visualizeSummon(summon));
        for(Minion summon : engine.getBattlefield2())
            battlefield2.add(visualizeSummon(summon));
        battlefield1.revalidate();
        battlefield1.repaint();
        battlefield2.revalidate();
        battlefield2.repaint();
    }

    private void updateBothManaPools()
    {
        Dimension crystalDimension = new Dimension(manaCrystal.getIconWidth(), manaCrystal.getIconHeight());
        manaPool2.removeAll();
        manaPool1.removeAll();
        for (int i = 0; i < engine.getPlayer2().getCurrMana(); i++)
        {
            JLabel temp = new JLabel(manaCrystal);
            temp.setPreferredSize(crystalDimension);
            temp.setVisible(true);
            manaPool2.add(temp);
        }
        for (int i = 0; i < engine.getPlayer1().getCurrMana(); i++)
        {
            JLabel temp = new JLabel(manaCrystal);
            temp.setPreferredSize(crystalDimension);
            temp.setVisible(true);
            manaPool1.add(temp);
        }
        manaPool2.revalidate();
        manaPool2.repaint();
        manaPool1.revalidate();
        manaPool1.repaint();
    }

    private void updateBothPlayers()
    {
        player1.setText(engine.getPlayer1().getHealthToString());
        player2.setText(engine.getPlayer2().getHealthToString());
    }

    private void updateGameFrame()
    {
        updateBothHands();
        updateBoard();
        updateBothManaPools();
        updateBothPlayers();
        isGameFinished();
    }

    public ImageIcon getScaledImage(ImageIcon imageIcon, double imageScale)
    {
        Image scaled = imageIcon.getImage();
        scaled = scaled.getScaledInstance((int) (imageIcon.getIconWidth() * imageScale), (int) (imageIcon.getIconHeight() * imageScale), Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaled);
        return imageIcon;
    }

    private JButton visualiseCard(IDeckElement deckElement)
    {
        JButton card;
        if(deckElement.isMinion())
        {
            Minion minion = (Minion) deckElement;
            card = new JButton(cardToString(minion), minionBackground);
            card.setPreferredSize(new Dimension(this.minionBackground.getIconWidth(), this.minionBackground.getIconHeight()));
            card.setVerticalTextPosition(SwingConstants.TOP);
            card.setHorizontalTextPosition(SwingConstants.LEFT);
            card.setForeground(Color.WHITE);
            card.setFont(new Font("Arial", Font.BOLD, 25));

            if(engine.isPlayerOneTurn())
                if(minion.isInDeckOne())
                {
                    if (minion.getManaCost() <= engine.getPlayer1().getCurrMana())
                        card.setBorder(new LineBorder(Color.green, 3));
                    else
                        card.setBorder(new LineBorder(Color.red, 3));

                    card.addActionListener(e -> {
                        this.engine.summonMinion(minion);
                        updateGameFrame();
                    });
                }
                else
                {
                    card = new JButton("", cardBackside);
                    card.setPreferredSize(new Dimension(cardBackside.getIconWidth(), cardBackside.getIconHeight()));
                    card.setVisible(true);
                    card.setBorder(new LineBorder(Color.black, 3));
                }
            else
                if(!minion.isInDeckOne())
                {
                    if (minion.getManaCost() <= engine.getPlayer2().getCurrMana())
                        card.setBorder(new LineBorder(Color.green, 3));
                    else
                        card.setBorder(new LineBorder(Color.red, 3));

                    card.addActionListener(e ->
                    {
                        this.engine.summonMinion(minion);
                        updateGameFrame();
                    });
                }
                else
                {
                    card = new JButton("", cardBackside);
                    card.setPreferredSize(new Dimension(cardBackside.getIconWidth(), cardBackside.getIconHeight()));
                    card.setVisible(true);
                    card.setBorder(new LineBorder(Color.black, 3));
                }
        }
        else
        {
            Spell spell = (Spell) deckElement;
            card = new JButton(cardToString(spell), spellBackground);
            card.setPreferredSize(new Dimension(spellBackground.getIconWidth() + 6, spellBackground.getIconHeight() + 15));
            card.setVerticalTextPosition(SwingConstants.TOP);
            card.setHorizontalTextPosition(SwingConstants.LEFT);
            card.setForeground(Color.WHITE);
            card.setFont(new Font("Arial", Font.BOLD, 25));

            if(engine.isPlayerOneTurn())
            {
                if (spell.isInDeckOne())
                {
                    if (spell.getManaCost() <= engine.getPlayer1().getCurrMana())
                        card.setBorder(new LineBorder(Color.green, 3));
                    else
                        card.setBorder(new LineBorder(Color.red, 3));

                    card.addActionListener(e ->
                    {
                        if (spell.getManaCost() <= engine.getPlayer1().getCurrMana())
                        {
                            if(!clicked || clickedSpellIndex != engine.getPlayer1().getHand().indexOf(spell))
                            {
                                clicked = true;
                                clickedSpellIndex = engine.getPlayer1().getHand().indexOf(spell);
                            }
                            else
                            {
                                clicked = false;
                                clickedSpellIndex = -1;
                            }
                            clickedSummonIndex = -1;
                        }
                    });
                }
                else
                {
                    card = new JButton("", cardBackside);
                    card.setPreferredSize(new Dimension(cardBackside.getIconWidth(), cardBackside.getIconHeight()));
                    card.setVisible(true);
                    card.setBorder(new LineBorder(Color.black, 3));
                }
            }
            else
            {
                if(!spell.isInDeckOne())
                {
                    if (spell.getManaCost() <= engine.getPlayer2().getCurrMana())
                        card.setBorder(new LineBorder(Color.green, 3));
                    else
                        card.setBorder(new LineBorder(Color.red, 3));

                    card.addActionListener(e ->
                    {
                        if (spell.getManaCost() <= engine.getPlayer2().getCurrMana())
                        {
                            if(!clicked || clickedSpellIndex != engine.getPlayer2().getHand().indexOf(spell))
                            {
                                clicked = true;
                                clickedSpellIndex = engine.getPlayer2().getHand().indexOf(spell);
                            }
                            else
                            {
                                clicked = false;
                                clickedSpellIndex = -1;
                            }
                            clickedSummonIndex = -1;
                        }
                    });
                }
                else
                {
                    card = new JButton("", cardBackside);
                    card.setPreferredSize(new Dimension(cardBackside.getIconWidth(), cardBackside.getIconHeight()));
                    card.setVisible(true);
                    card.setBorder(new LineBorder(Color.black, 3));
                }
            }
        }
        card.setBackground(Color.GRAY);
        return card;
    }

    private JButton visualizeSummon(Minion minion)
    {
        JButton summon = new JButton(summonToString(minion), summonBackGround);
        summon.setPreferredSize(new Dimension(summonBackGround.getIconWidth(), summonBackGround.getIconHeight()));
        summon.setForeground(Color.WHITE);
        summon.setFont(new Font("Arial", Font.BOLD, 20));
        summon.setBackground(new Color(153, 129, 112));
        summon.setHorizontalTextPosition(JButton.LEFT);
        summon.setVerticalTextPosition(JButton.TOP);

        if(engine.isPlayerOneTurn() == minion.isInDeckOne())
        {
            if (minion.canAttack())
                summon.setBorder(new LineBorder(Color.GREEN, 3));
            else
                summon.setBorder(new LineBorder(Color.BLACK, 3));
        }
        else
            summon.setBorder(new LineBorder(Color.BLACK, 3));

        summon.addActionListener(e -> {
                if(!clicked && minion.canAttack() && minion.isInDeckOne() == engine.isPlayerOneTurn())
                {
                    clicked = true;
                    clickedSpellIndex = -1;
                    if(engine.getBattlefield1().contains(minion))
                        clickedSummonIndex = engine.getBattlefield1().indexOf(minion);
                    else
                        clickedSummonIndex = engine.getBattlefield2().indexOf(minion);
                }
                else if(clicked)
                {
                    if(engine.isPlayerOneTurn())
                    {
                        if(clickedSummonIndex != -1)
                        {
                            if (engine.attackMinion(engine.getBattlefield1().get(clickedSummonIndex), minion))
                                updateGameFrame();
                            else
                                JOptionPane.showMessageDialog(this, "Illegal move. Try again");
                        }
                        else
                        {
                            if(engine.attackMinion(engine.getPlayer1().getHand().get(clickedSpellIndex), minion))
                                updateGameFrame();
                            else
                                JOptionPane.showMessageDialog(this, "Illegal move. Try again");
                        }
                    }
                    else
                    {
                        if(clickedSummonIndex != -1)
                        {
                            if (engine.attackMinion(engine.getBattlefield2().get(clickedSummonIndex), minion))
                                updateGameFrame();
                            else
                                JOptionPane.showMessageDialog(this, "Illegal move. Try again");
                        }
                        else
                        {
                            if(engine.attackMinion(engine.getPlayer2().getHand().get(clickedSpellIndex), minion))
                                updateGameFrame();
                            else
                                JOptionPane.showMessageDialog(this, "Illegal move. Try again");
                        }
                    }
                    clicked = false;
                    clickedSummonIndex = -1;
                    clickedSpellIndex = -1;

                    updateGameFrame();
                }
        });

        if(minion.isFrozen())
            summon.setBorder(new LineBorder(new Color(64, 184, 181), 3));
        return summon;
    }

    private void isGameFinished()
    {
        if(engine.getPlayer1().isDead())
        {
            JOptionPane.showMessageDialog(this, "Player 2 is victorious!");
            System.exit(0);
        }
        else if(engine.getPlayer2().isDead())
        {
            JOptionPane.showMessageDialog(this, "Player 1 is victorious!");
            System.exit(0);
        }
    }

    private String cardToString(IDeckElement e)
    {
        if(e.isMinion())
            return minionToString((Minion) e);
        else
            return spellToString((Spell) e);
    }

    private String minionToString(Minion minion)
    {
        int counter = 2;
        String result = "<html>" +
                "<body>" +
                "<br>" +
                "<pre> " +
                minion.getManaCost() +
                "</pre>" +
                "<br>";
        if(minion.getAmountOfAbilities() == 1)
            result += "<br>";
        else if(minion.getAmountOfAbilities() == 0)
            result += "<br>";

        if(minion.isTaunt())
            result += "<pre>   Taunt</pre>";
        else if(minion.isInStealth())
            result += "<pre>  Stealth</pre>";
        if(minion.isCharge())
            result += "<pre>  Charge</pre>";

        counter -= minion.getAmountOfAbilities();
        for(int i = 0; i < counter; i++)
            result += "<br>";

        result += "<pre> " +
                minion.getAttackPower() +
                "       " +
                minion.getMaxHealth() +
                "</pre>" +
                "</body>" +
                "</html>";

        return result;
    }

    private String spellToString(Spell spell)
    {
        String result = "<html>" +
            spell.getManaCost()
                +"<br><br>";

        if(spell.getAmountOfAbilities() == 1)
            result += "<br>";
        else if(spell.getAmountOfAbilities() == 0)
            result += "<br>";

        if(spell.isHeal())
            result += "<pre>  Heal: " + spell.getHealPower() + "</pre>";
        else
            result += "<pre>  Attack: " + spell.getAttackPower() + "</pre>";

        if(spell.canFreeze())
            result += "<pre>  Freeze</pre>";
        if(spell.canSilence())
            result += "<pre>  Silence</pre>";
        if(spell.isDrawCard())
            result += "<pre>  Draw card</pre>";

        result += "</html>";
        return result;
    }

    private String summonToString(Minion summon)
    {
        String result = "<html>" +
                "<body>";

        if(summon.isTaunt())
            result += "<br><pre> Taunt</pre><br>";
        else if(summon.isInStealth())
            result += "<br><pre>Stealth</pre>";

        if(!summon.isTaunt() && !summon.isInStealth())
            result += "<br><br><br>";

        result += "<pre> " +
                summon.getAttackPower();

        if(summon.getAttackPower() < 10)
            result += " ";

        result += " " +
                summon.getCurrentHealth() + "/" + summon.getMaxHealth() +
                "</pre>" +
                "</body>" +
                "</html>";

        return result;
    }
}
