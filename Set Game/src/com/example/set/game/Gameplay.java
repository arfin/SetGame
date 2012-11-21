package com.example.set.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class Gameplay extends Activity {

	GridView gridView;
	Deck deck;
	List<Card> inPlay;
	List<Integer> clickedSet;
	MyAdapter adapter;
	boolean showWrong = true;
	boolean Hard = true;
	SharedPreferences sharedPrefs;
	int mode;
	TextView text;
	long startTime = 0;
	long pausedTime = 0;
	Timer timer;
	Handler handle = new Handler();
	boolean playing = true;
	Button b;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gameplay);

		// Load preferences
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		changeSettings();

		// Set up timer and timer button
		text = (TextView) findViewById(R.id.timer);
		b = (Button) findViewById(R.id.button);
		b.setOnClickListener(buttonListener);
		startTime = System.currentTimeMillis();
		pausedTime = 0;
		timer = new Timer();
		handle.postDelayed(run, 0);
		b.setText("Pause");

		// Set up deck for game
		deck = new Deck(mode);
		deck.shuffle();
		inPlay = new ArrayList<Card>();
		clickedSet = new ArrayList<Integer>();
		for (int i = 0; i < 12; i++) {
			inPlay.add(deck.cards.remove(deck.cards.size() - 1));
		}

		// Set up the gridview to show the cards
		gridView = (GridView) findViewById(R.id.gridView);
		adapter = new MyAdapter(this, inPlay);
		gridView.setAdapter(adapter);

		addCardsIfNeeded();

		// Listen when cards are clicked
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// Selecting a card
				if (!clickedSet.contains(position)) {
					if (clickedSet.size() < 2) {
						v.setPadding(1, 1, 1, 1);
						v.setBackgroundColor(Color.RED);
						inPlay.get(position).clicked = true;
						clickedSet.add(position);
						// They are a set
					} else if (Card.areSet(inPlay.get(clickedSet.get(0)),
							inPlay.get(clickedSet.get(1)), inPlay.get(position))) {
						// Too many cards in play - don't add more back
						if (inPlay.size() <= 12 && deck.cards.size() != 0) {
							dealToPosition(clickedSet.get(0));
							dealToPosition(clickedSet.get(1));
							dealToPosition(position);
							// Need to replace the cards
						} else {
							Card card1 = inPlay.get(clickedSet.get(0));
							Card card2 = inPlay.get(clickedSet.get(1));
							Card card3 = inPlay.get(position);
							inPlay.remove(card1);
							adapter.removeCard(card1);
							inPlay.remove(card2);
							adapter.removeCard(card2);
							inPlay.remove(card3);
							adapter.notifyDataSetChanged();
						}
						clickedSet.clear();
						// If there is no set, add more cards
						addCardsIfNeeded();

						// Game over condition
						if (!Deck.containsSet(inPlay)) {
							b.setText("Game Over");
							b.setEnabled(false);
							handle.removeCallbacks(run);
							Toast.makeText(
									getApplicationContext(),
									"Game Over. Press Menu Button To Start New Game",
									Toast.LENGTH_LONG).show();
						}
						// They didnt choose a set
					} else {
						String msg = Card.whatsWrongWithCards(
								inPlay.get(clickedSet.get(0)),
								inPlay.get(clickedSet.get(1)),
								inPlay.get(position));
						if (showWrong) {
							Toast.makeText(getApplicationContext(), msg,
									Toast.LENGTH_SHORT).show();
						}
					}
					// Unselecting a card
				} else {
					v.setBackgroundColor(Color.WHITE);
					inPlay.get(position).clicked = false;
					clickedSet.remove((Integer) position);
				}
			}
		});

	}

	@Override
	public void onPause() {
		if (playing) {
			pausedTime += System.currentTimeMillis() - startTime;
		}
		startTime = 0;
		handle.removeCallbacks(run);
		super.onPause();
	}

	public void onRestart() {
		super.onRestart();
		int seconds = (int) (pausedTime / 1000);
		int minutes = seconds / 60;
		seconds = seconds % 60;
		text.setText(String.format("%d:%02d", minutes, seconds));
		if (playing) {
			startTime = System.currentTimeMillis();
			handle.postDelayed(run, 0);
		}
	}

	// Deals a card to the specified position. If pos < 0 or > # of cards, it
	// just deals
	// to the end. Otherwise, it replaces the card at pos
	// Returns false if deck is empty
	private boolean dealToPosition(int pos) {
		List<Card> cards = deck.cards;
		if (cards.size() == 0) {
			return false;
		}
		if (pos >= inPlay.size() || pos < 0) {
			Card toAdd = cards.remove(cards.size() - 1);
			inPlay.add(toAdd);
			// adapter.addCard(toAdd);
			adapter.notifyDataSetChanged();
		} else {
			Card toAdd = cards.remove(cards.size() - 1);
			inPlay.remove(pos);
			inPlay.add(pos, toAdd);
			adapter.changeCard(toAdd, pos);
			adapter.notifyDataSetChanged();
		}
		return true;
	}

	// Adds 3 cards if there is no set out
	public void addCardsIfNeeded() {
		List<Card> cards = deck.cards;
		while (!cards.isEmpty() && !Deck.containsSet(inPlay)) {
			dealToPosition(-1);
			dealToPosition(-1);
			dealToPosition(-1);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_gameplay, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent intent = new Intent(this, SetPreferences.class);
			startActivityForResult(intent, 1);
			return true;
		case R.id.new_game:
			gridView.setEnabled(true);
			b.setText("Pause");
			b.setEnabled(true);
			startTime = System.currentTimeMillis();
			pausedTime = 0;
			handle.postDelayed(run, 0);

			deck = new Deck(mode);
			deck.shuffle();
			inPlay = new ArrayList<Card>();
			clickedSet = new ArrayList<Integer>();
			for (int i = 0; i < 12; i++) {
				inPlay.add(deck.cards.remove(deck.cards.size() - 1));
			}
			clickedSet.clear();
			adapter = new MyAdapter(this, inPlay);
			gridView.setAdapter(adapter);
			addCardsIfNeeded();
			return true;
		case R.id.hint:
			for (int i : clickedSet) {
				View v = gridView.getChildAt(i);
				v.setBackgroundColor(Color.WHITE);
				inPlay.get(i).clicked = false;
				clickedSet.remove((Integer) i);
			}
			int[] foundSet = Deck.findSet(inPlay);
			for (int i = 0; i < 1; i++) {
				View v = gridView.getChildAt(foundSet[i]);
				v.setBackgroundColor(Color.RED);
				inPlay.get(foundSet[i]).clicked = true;
				clickedSet.add((Integer) foundSet[i]);
			}
			return true;
		case R.id.show:
			for (int i : clickedSet) {
				View v = gridView.getChildAt(i);
				v.setBackgroundColor(Color.WHITE);
				inPlay.get(i).clicked = false;
			}
			clickedSet.clear();
			int[] foundSet1 = Deck.findSet(inPlay);
			for (int i = 0; i < 2; i++) {
				View v = gridView.getChildAt(foundSet1[i]);
				v.setBackgroundColor(Color.RED);
				inPlay.get(foundSet1[i]).clicked = true;
				clickedSet.add((Integer) foundSet1[i]);
			}
			return true;
		default:
			return true;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		// Return from preference setting activity
		case 1:
			changeSettings();
			break;
		}
	}

	private void changeSettings() {
		showWrong = sharedPrefs.getBoolean("show_wrong", false);
		boolean keepOn = sharedPrefs.getBoolean("keep_on", false);
		if (keepOn) {
			getWindow()
					.addFlags(
							android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			getWindow()
					.clearFlags(
							android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		mode = Integer.parseInt(sharedPrefs.getString("difficulty", "1"));
		if (mode == 1) {
			mode = Deck.HARD;
		} else {
			mode = Deck.EASY;
		}
	}

	// For the timer
	Runnable run = new Runnable() {

		@Override
		public void run() {
			long millis = System.currentTimeMillis() - startTime + pausedTime;
			int seconds = (int) (millis / 1000);
			int minutes = seconds / 60;
			seconds = seconds % 60;

			text.setText(String.format("%d:%02d", minutes, seconds));

			handle.postDelayed(this, 500);
		}

	};

	OnClickListener buttonListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Button but = (Button) v;
			if (but.getText().equals("Start")) {
				startTime = System.currentTimeMillis();
				timer = new Timer();
				handle.postDelayed(run, 0);
				but.setText("Pause");
				gridView.setEnabled(true);
				playing = true;
			} else {
				timer.cancel();
				timer.purge();
				handle.removeCallbacks(run);
				pausedTime += System.currentTimeMillis() - startTime;
				startTime = 0;
				but.setText("Start");
				gridView.setEnabled(false);
				playing = false;
			}
		}
	};
}
