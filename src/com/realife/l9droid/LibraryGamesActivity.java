package com.realife.l9droid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.SimpleExpandableListAdapter;

public class LibraryGamesActivity extends Activity implements OnChildClickListener, OnClickListener {

	// ��������� ��� ���������
	ArrayList<Map<String, Object>> categories;
	// ��������� ��� ��������� ����� ������ ���
	ArrayList<Map<String, Object>> gameItems;
	// ����� ��������� ��� ��������� ���������
	ArrayList<ArrayList<Map<String, Object>>> games;
	// ������ ���������� ������ ��� ��������
	Map<String, Object> m;
	ExpandableListView elGames;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.library_games);
		
	    ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
	    ivBack.setOnClickListener(this);
		
		Library lib=Library.getInstance();
		
		categories = new ArrayList<Map<String, Object>>();
		// ������� ��������� ��� ��������� ��������� 
		games = new ArrayList<ArrayList<Map<String, Object>>>(); 
		
		ArrayList<GameInfo> gameList=lib.getGameList(this);
		String prevCategory=null;
		
		String categoryFrom[] = new String[] {"category"};
		int categoryTo[] = new int[] {R.id.text1/*android.R.id.text1*/};

		String gameFrom[] = new String[] {"game","mark"};
		int gameTo[] = new int[] {R.id.text1,R.id.ivMark};
		
		for (GameInfo gi: gameList) {
			if (!gi.getCategory().equals(prevCategory)) {
				if (prevCategory!=null) games.add(gameItems);
				prevCategory=gi.getCategory();
				//�������� ��������� � ������
				m = new HashMap<String, Object>();
				m.put("category", prevCategory);
				categories.add(m);
				gameItems = new ArrayList<Map<String, Object>>();
			};
			m = new HashMap<String, Object>();
			m.put("game", gi.getTitle());
			m.put("id", gi.getId());
			m.put("mark", Library.MARK_PICTURES_RESID[gi.getHighestMark()]);
			gameItems.add(m);
		};
		if (prevCategory!=null) games.add(gameItems);
	
		//SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
		ExpAdapter adapter = new ExpAdapter(
				this,
				categories,
				R.layout.library_games_category_item,
				categoryFrom,
				categoryTo,
				games,
				R.layout.library_games_game_item,
				gameFrom,
				gameTo);
      
		elGames = (ExpandableListView) findViewById(R.id.elGames);
		elGames.setAdapter(adapter);
		elGames.setOnChildClickListener(this);
	};
	
	public boolean onChildClick(ExpandableListView parent,
			View view, int groupPosition,
			int childPosition, long id) {
		
			@SuppressWarnings("unchecked")
			String selectedGame=((Map<String,String>)(parent.getExpandableListAdapter().getChild(groupPosition, childPosition))).get("id");
			
			Intent intent=new Intent(this, LibraryGameInfoActivity.class);
			intent.putExtra("selectedgame", selectedGame);
			startActivityForResult(intent, 1);

			return true;
		}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1:
		  	  	setResult(RESULT_OK,data);
		  	  	finish();
				break;
			}
		} else {
		}
	}
	
	public void onClick(View v) {
		if (v.getId()==R.id.ivBack) {
			onBackPressed();
		};
	}

}
