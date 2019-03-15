package model;

import model.cards.Card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * The {@link Player} class represents a player in the game.
 */
public abstract class Player implements GameObserver {
  /** The {@link Player.Role} enum represents the two roles in the game */
  public enum Role {SABOTEUR, GOLD_MINER}

  /** The player's name */
  private final String name;
  /** The player's hand */
  private ArrayList<Card> cards;
  /** The player's role */
  private Role role;
  /** Sabotaged tools of the player */
  private HashSet<Tool> sabotaged;
  /** Discarded cards of the player */
  private ArrayList<Card> discarded;
  /** A reference to the game state */
  private GameState state;

  /**
   * Creates a {@link Player} object based on the specified name
   *
   * @param name the name of the player
   */
  public Player(String name) {
    this.name = name;
  }

  /**
   * Initializes the player's role and hand
   *
   * @param role  the player's role
   * @param cards the supposed card at hand
   */
  final void initialize(Role role, ArrayList<Card> cards) {
    this.role = role;
    this.cards = cards;
    this.sabotaged = new HashSet<>();
    this.discarded = new ArrayList<>();
  }

  /**
   * Adds a card to the player's hand
   *
   * @param card the card to be added
   */
  final void giveCard(Card card) {
    if (card != null) this.cards.add(card);
  }

  /**
   * Takes and returns a card at the specified index from the player
   *
   * @param index the card's index
   * @return the taken card
   * @throws GameException when index out of bounds
   */
  final Card takeCardAt(int index) throws GameException {
    try {
      return this.cards.remove(index);
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new GameException("Taken card index out of bounds");
    }
  }

  /**
   * Peek's a card at the specified index from the player
   *
   * @param index the card's index
   * @return the peeked card
   */
  protected final Card peekCardAt(int index) {
    return this.cards.get(index);
  }

  /**
   * Sabotages the specified tool of the player
   *
   * @param tool the tool to be sabotaged
   * @throws GameException when the player's <code>tool</code> is already sabotaged
   */
  final void sabotageTool(Tool tool) throws GameException {
    // Prevent the same tool from being sabotaged again
    if (sabotaged.contains(tool)) {
      String msgFormat = "%s's %s is already sabotaged";
      throw new GameException(msgFormat, name, tool);
    }

    // Mark tool as sabotaged
    this.sabotaged.add(tool);
  }

  /**
   * Repairs the specified tools of the player
   *
   * @param tools the tools to be repaired
   * @throws GameException when none of the given <code>tools</code> are sabotaged
   */
  final void repairTool(Tool... tools) throws GameException {
    // Prevent repairing intact tools
    boolean intact = true;
    for (Tool tool : tools) intact = intact && !isSabotaged(tool);
    if (intact) {
      String msgFormat = "%s's %s " + (tools.length > 1 ? "are" : "is") + " still intact";
      String toolSentence;
      if (tools.length == 1) {
        toolSentence = tools[0].name().toLowerCase();
      } else if (tools.length == 2) {
        toolSentence = tools[0].name().toLowerCase() + " and" + tools[1].name().toLowerCase();
      } else {
        String[] pre = Arrays
          .stream(Arrays.copyOfRange(tools, 0, tools.length - 1))
          .map(v -> v.name().toLowerCase()).toArray(String[]::new);
        String preString = String.join(", ", pre);
        toolSentence = preString + ", and " + tools[tools.length - 1].name().toLowerCase();
      }
      throw new GameException(String.format(msgFormat, name, toolSentence));
    }

    // Mark tools as repaired
    for (Tool tool : tools) sabotaged.remove(tool);
  }

  /**
   * Adds a card to the discarded set
   *
   * @param card the discarded card
   */
  final void addDiscard(Card card) { if (card != null) this.discarded.add(card); }

  /**
   * Sets the game state reference of the player
   *
   * @param state the game state
   */
  final void setState(GameState state) { this.state = state; }

  /**
   * Checks if the player is sabotaged
   *
   * @return a boolean marking the player is sabotaged
   */
  public final boolean isSabotaged() { return !sabotaged.isEmpty(); }

  /**
   * Checks if the specified player's tool is sabotaged
   *
   * @param tool the tool to be checked
   * @return a boolean indicating if the tool is sabotaged
   */
  public final boolean isSabotaged(Tool tool) {
    return sabotaged.contains(tool);
  }

  /**
   * Returns the name of the player
   *
   * @return the player's name
   */
  public final String name() { return this.name; }

  /**
   * Returns all the cards at the player's hand
   *
   * @return the player's hand
   */
  protected ArrayList<Card> hand() { return cards; }

  /**
   * Returns the game state reference
   *
   * @return the game state
   */
  protected GameState state() { return this.state; }
}
