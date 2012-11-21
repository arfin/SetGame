package com.example.set.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
	final static int EASY = 1;
	final static int HARD = 2;
	
	//Returns true iff the list of Cards contains a Set
	public static boolean containsSet(List<Card> cards){
		for (int i = 0; i < cards.size(); i++){
			for (int j = i+1; j < cards.size(); j++){
				for (int k = j+1; k < cards.size(); k++){
					if (Card.areSet(cards.get(i), cards.get(j), cards.get(k)))
						return true;
				}
			}
		}
		return false;
	}
	
	//Returns an array containing the indices in cards of 3 cards that form a set
	//Assumes there is a set in cards.
	public static int[] findSet(List<Card> cards){
		int[] result = new int[3];
		for (int i = 0; i < cards.size(); i++){
			for (int j = i+1; j < cards.size(); j++){
				for (int k = j+1; k < cards.size(); k++){
					if (Card.areSet(cards.get(i), cards.get(j), cards.get(k))){
						result[0] = i;
						result[1] = j;
						result[2] = k;
						return result;
					}
				}
			}
		}
		return result;
	}
	
	
	

	public int mode;
	List<Card> cards;

	public Deck(int mode) {
		this.mode = mode;
		cards = new ArrayList<Card>();
		for (int i = 1; i <= 3; i++) {
			for (int j = 1; j <= 3; j++) {
				for (int k = 1; k <= 3; k++) {
					cards.add(new Card(i, j, k, 1));
					if (mode == HARD) {
						cards.add(new Card(i, j, k, 2));
						cards.add(new Card(i, j, k, 3));
					}
				}
			}
		}
	}

	public void shuffle() {
		Collections.shuffle(cards);
	}
}
