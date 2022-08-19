package com.bj.sim.entities.floor;

import com.bj.sim.config.TableRules;
import com.bj.sim.entities.cards.Card;
import com.bj.sim.entities.cards.Deck;

import java.util.ArrayList;
import java.util.List;

// table organizes the shoe, dealer and players and rule config for table
public class Table {
    private List<Card> shoe;
    private Dealer dealer;
    private List<Player> players;
    private TableRules rules; // tableRulConfig i.e. # of shoes, rsa, # of splits allowed ace/non-ace, # of draws after ace split, surrender rules

    public Table(TableRules rules, Integer playerCount) {
        this.rules = rules;
        this.players = new ArrayList<>();
        for(int i = 0; i < playerCount; i++) {
            this.players.add(new Player(rules));
        }
        this.dealer = new Dealer(players.get(0));
        List<Deck> decks = new ArrayList<>();
        for(int i = 0; i < this.rules.numOfDecks(); i++) {
            decks.add(new Deck());
        }
        this.shoe = new ArrayList<>();
        for(Deck d : decks)
            for(Card c : d.getDeck())
                this.shoe.add(c);
    }

    @Override
    public String toString() {
        return "Table{\n\t"+shoe.toString() + "\n\t" + dealer.toString() + "\n\t" + players.toString() + "\n\t" + rules.toString() + '}';
    }

    public List<Card> getShoe() {
        return shoe;
    }

    public Dealer getDealer() {
        return dealer;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public TableRules getRules() {
        return rules;
    }
}
