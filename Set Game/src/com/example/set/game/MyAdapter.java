package com.example.set.game;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

public class MyAdapter extends BaseAdapter {
	private Context context;
	private List<Card> cards;

	public MyAdapter(Context context, List<Card> cards) {
		this.context = context;
		this.cards = cards;
	}
	
	public void changeCard(Card newCard, int position) {
		cards.set(position, newCard);
	}
	
	public void addCard(Card newCard){
		cards.add(newCard);
	}
	public void removeCard(Card card){
		cards.remove(card);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View gridView;

//		if (convertView == null) {
			gridView = new View(context);
			gridView = inflater.inflate(R.layout.image, null);
			ImageView imageView = (ImageView) gridView
					.findViewById(R.id.grid_item);
			Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.all_set_cards);
			Bitmap newbm = Bitmap.createBitmap(bm, cards.get(position).xOffset,
					cards.get(position).yOffset, 95, 53);
			imageView.setImageBitmap(newbm);
//		} else {
	//		gridView = (View) convertView;
//		}
		return gridView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cards.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}
