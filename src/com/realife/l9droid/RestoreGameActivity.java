package com.realife.l9droid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class RestoreGameActivity extends Activity {
	
	// ����� ��������� ��� Map
	final String ATTRIBUTE_NAME_NAME = "name";
	final String ATTRIBUTE_NAME_DATE = "date";
	final String ATTRIBUTE_NAME_IMAGE = "image";
	
	ListView lvStates;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.restore_game);
        
     // ������� ������
        String[] texts = { "sometext 1", "sometext 2", "sometext 3",
            "sometext 4", "sometext 5" };
        String[] dates = { "01.01.01",
        		"sometext 2",
        		"sometext 3",
                "sometext 4",
                "sometext 5" };
        int img = R.drawable.ic_launcher;

        // ����������� ������ � �������� ��� �������� ���������
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        Map<String, Object> m;
        for (int i = 0; i < texts.length; i++) {
          m = new HashMap<String, Object>();
          m.put(ATTRIBUTE_NAME_NAME, texts[i]);
          m.put(ATTRIBUTE_NAME_DATE, dates[i]);
          m.put(ATTRIBUTE_NAME_IMAGE, img);
          data.add(m);
        }

        // ������ ���� ���������, �� ������� ����� �������� ������
        String[] from = { ATTRIBUTE_NAME_NAME, ATTRIBUTE_NAME_DATE,
            ATTRIBUTE_NAME_IMAGE };
        // ������ ID View-�����������, � ������� ����� ��������� ������
        int[] to = { R.id.tvName, R.id.tvDate, R.id.ivPic };

        // ������� �������
        SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.restore_game_item,
            from, to);

        // ���������� ������ � ����������� ��� �������
        lvStates = (ListView) findViewById(R.id.lvStates);
        lvStates.setAdapter(sAdapter);
        
        
        
	};
}
