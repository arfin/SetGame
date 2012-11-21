package com.example.set.game;

public class Card {
	final static int ONE = 1;
	final static int TWO = 2;
	final static int THREE = 3;
	final static int DIAMOND = 1;
	final static int OVAL = 2;
	final static int SQUIGGLE = 3;
	final static int RED = 1;
	final static int GREEN = 2;
	final static int PURPLE = 3;
	final static int EMPTY = 1;
	final static int SHADED = 2;
	final static int FULL = 3;
	
	//Returns true iff two of the integers are the same but not the third
	static boolean areTwoSame(int a, int b, int c){
		if (a == b && a !=c){
			return true;
		}
		if (a==c && a!=b){
			return true;
		}
		if (b == c && b != a){
			return true;
		}
		return false;
	}
	
	//Returns a string stating something wrong with these 3 cards being a set.
	//Returns "Nothing" if they are a set
	static String whatsWrongWithCards(Card first, Card second, Card third){
		if (first.equals(second) || first.equals(third) || first.equals(second)){
			return "Duplicate Cards";
		}
		if (areTwoSame(first.number, second.number,third.number)){
			return "Exactly two of the same number";
		}
		if (areTwoSame(first.color,second.color,third.color)){
			return "Exactly two of the same color";
		}
		if (areTwoSame(first.shape,second.shape, third.shape)){
			return "Exactly two of the same shape";
		}
		if (areTwoSame(first.shading, second.shading, third.shading)){
			return "Exactly two of the same shading";
		}
		return "Nothing";
	}
	
	//Returns true iff the three cards are a set
	static boolean areSet(Card first, Card second, Card third){
		return (whatsWrongWithCards(first, second, third).equals("Nothing"));
	}
	

	public int number;
	public int shape;
	public int color;
	public int shading;
	public int xOffset;
	public int yOffset;
	public boolean clicked;

	public Card(int number, int shape, int color, int shading) {
		this.number = number;
		this.shape = shape;
		this.color = color;
		this.shading = shading;
		clicked = false;
		xOffset = 5;
		yOffset = 5;
		if (number == TWO){
			xOffset += 100;
		}
		else if (number == THREE){
			xOffset += 200;
		}
		if (shape == OVAL){
			xOffset += 318;
		}
		else if (shape == SQUIGGLE){
			xOffset += 636;
		}
		if (color == GREEN){
			yOffset += 220;
		}
		else if (color == PURPLE){
			yOffset += 440;
		}
		if (shading == SHADED){
			yOffset += 68;
		}
		else if (shading == FULL){
			yOffset += 136;
		}
	}

	public boolean equals(Card other) {
		return (number == other.number && shape == other.shape
				&& color == other.color && shading == other.shading);
	}
}
