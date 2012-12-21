package com.realife.l9droid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener,OnEditorActionListener, OnMenuItemClickListener {
	
	SharedPreferences sp;
	Typeface tf;
	Typeface tfDefault=null;
	float fontSizeDefault=0;
	
	Button bCmd;
	EditText etCmd;
	Button bSpace;
	Button bEnter;

	ListView lvMain;
	ListView lvHistory;
   
	ImageView ivScreen;
    
	String command;
    
	static Threads mt;
    
	boolean killThreadsOnDestroyActivity=true;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        
        ivScreen=(ImageView) findViewById(R.id.imageView1);
        ivScreen.setOnClickListener(this);
                       
        bCmd = (Button) findViewById(R.id.bCmd);
        bCmd.setOnClickListener(this);
        
        bSpace = (Button) findViewById(R.id.bSpace);
        bSpace.setOnClickListener(this);
    	bEnter = (Button) findViewById(R.id.bEnter);
    	bEnter.setOnClickListener(this);
        
        etCmd = (EditText) findViewById(R.id.etCmd);
        etCmd.setHint("Enter your command");

        etCmd.setOnEditorActionListener(this);
        
        etCmd.setText("");
        
        // ������� ������
        lvMain = (ListView) findViewById(R.id.lvLog);
        lvMain.setDividerHeight(0);
        
        lvHistory = (ListView) findViewById(R.id.lvHistory);
        lvHistory.setVisibility(ListView.VISIBLE);
                 
        command=null;
        
        mt = (Threads) getLastNonConfigurationInstance();
	    if (mt == null) {
	    	mt=new Threads();
	    	mt.link(this);
	        mt.create();
	        SharedPreferences sPref=getPreferences(MODE_PRIVATE);
	        String lastGame = sPref.getString("lastgame", "Worm In Paradise/Speccy/worm.sna");
	        mt.startGame(lastGame,true);
	        if (mt.l9!=null) Toast.makeText(this, "Started: "+lastGame, Toast.LENGTH_SHORT).show();
	        else Toast.makeText(this, "Fault start of: "+lastGame, Toast.LENGTH_SHORT).show();
	        
	    } else mt.link(this);
        lvMain.setAdapter(mt.lvAdapter);
        lvMain.setSelection(lvMain.getAdapter().getCount()-1);
        
        lvHistory.setAdapter(mt.lvHistoryAdapter);
        
	    ivScreen.setScaleType(ScaleType.FIT_XY);
    }
    
    public Object onRetainNonConfigurationInstance() {
    	killThreadsOnDestroyActivity=false;
	  	mt.unlink();
	    return mt;
	};
	
    protected void onResume() {
    	float fontSize=14;
    	if (tfDefault==null) tfDefault=etCmd.getTypeface();
    	if (fontSizeDefault==0) fontSizeDefault=etCmd.getTextSize();
    	//String listValue = sp.getString("list", "�� �������");
    	//tvInfo.setText("�������� ������ - " + listValue);
    	//tf=Typeface.create(Typeface.DEFAULT, (sp.getBoolean("fontbold", false)?(Typeface.BOLD):(Typeface.NORMAL)));
    	
    	if (sp.getBoolean("fontcustom", false)) {
    		tf=Typeface.create(sp.getString("fontface", "DEFAULT"), (sp.getBoolean("fontbold", false)?(Typeface.BOLD):(Typeface.NORMAL)));
    		//TODO: ���������! �� ������������� ������� �������! 
    		int s;
    		String fsa[]=getResources().getStringArray(R.array.pref_font_size_entries);
    		String fss=sp.getString("fontsize", fsa[3]);
    		for (s=0;s<fsa.length;s++)
    			if (fss.equals(fsa[s])) {
    				fontSize=12+s*2;
    				break;
    			};
    		
    	} else {
    		tf=tfDefault;
    		fontSize=fontSizeDefault;
    	};
    	//etLog.setTypeface(tf);
    	etCmd.setTypeface(tf);
    	//etLog.setTextSize(fontSize); //TODO: ��� ������� ��������� ������ � ���������?
    	etCmd.setTextSize(fontSize);
    	
    	//etLogScroll.fullScroll(ScrollView.FOCUS_DOWN);
    	
    	mt.activityPaused=false;
    	super.onResume();
    }
    
    protected void onPause() {
    	super.onPause();
    	mt.activityPaused=true;
    }
    
    protected void onDestroy() {
		super.onDestroy();
		mt.activityPaused=true;
		//Log.d("l9droid", "need to stop application");
		if (killThreadsOnDestroyActivity && mt.l9!=null) {
			SharedPreferences sPref=getPreferences(MODE_PRIVATE);
			Editor ed = sPref.edit();
			ed.putString("lastgame", mt.l9.LastGame);
			ed.commit();
			mt.destroy(true);
		}
        //mt=null;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        // ������ ���� � ID ������ = 1 �����, ���� � CheckBox ����� �����

        menu.setGroupVisible(1, mt.menuHashEnabled);
        menu.setGroupVisible(2, mt.menuHashEnabled && mt.gfx_ready && (!mt.menuPicturesEnabled));
        menu.setGroupVisible(3, mt.menuHashEnabled && mt.gfx_ready &&  mt.menuPicturesEnabled);
        
        return super.onPrepareOptionsMenu(menu);
    }
    
    //@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        //return true;
    	MenuItem mi;
    	mi = menu.add(0, 9,0,"Library");
    	mi.setOnMenuItemClickListener(this);
        mi = menu.add(1, 4, 0,"Save State");
    	mi.setOnMenuItemClickListener(this);
    	mi = menu.add(1, 5, 0,"Restore State");
    	mi.setOnMenuItemClickListener(this);
    	mi = menu.add(2, 6, 0,"Pictures");
    	mi.setOnMenuItemClickListener(this);
    	mi = menu.add(3, 7, 0,"Words");
    	mi.setOnMenuItemClickListener(this);
        mi = menu.add(0, 2, 0, "Settings");
        mi.setIntent(new Intent(this, PrefActivity.class));
    	mi = menu.add(1, 8, 0,"Play Script");
    	mi.setOnMenuItemClickListener(this);
        mi = menu.add(0, 3, 1, "About");
        mi.setIntent(new Intent(this, AboutActivity.class));
    	mi = menu.add(0, 1, 2, "Library Files");
    	mi.setOnMenuItemClickListener(this);

    	
        return super.onCreateOptionsMenu(menu);
    }

	//@Override
	public boolean onMenuItemClick(MenuItem arg0) {
		Intent intent;
		switch (arg0.getItemId()) {
		case 1: //library TODO: ���������� � id, �������� ��������� ���� � �������
			intent=new Intent(this, LibraryActivity.class);
			startActivityForResult(intent, 1); //TODO: "1"-change it or kill ))
	        //mi.setIntent(intent);
			break;
		case 9: //library TODO: ���������� � id, �������� ��������� ���� � �������
			intent=new Intent(this, LibraryGamesActivity.class);
			startActivityForResult(intent, 1); //TODO: "1"-change it or kill ))
	        //mi.setIntent(intent);
			break;
		case 4:
			postHashCommand("#save");
			break;
		case 5:
			postHashCommand("#restore");
			break;
		case 6:
			postHashCommand("pictures");
			break;
		case 7:
			postHashCommand("words");
			break;
		case 8:
			postHashCommand("#play");
			break;
		};
		return false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// ���� ������ ��
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1:
				//Toast.makeText(this, data.getStringExtra("opengame"), Toast.LENGTH_SHORT).show();
				String newGame=data.getStringExtra("opengame");
				mt.startGame(newGame,false);
				//TODO: ������� ������� ���, ���� ����������� �������� ������ �����������.
				mt.logStringCapacitor=null;
				mt.logStrId=-1;
				mt.lvAdapter.clear();
				if (mt.l9!=null) {
					Toast.makeText(this, "Started: "+newGame, Toast.LENGTH_SHORT).show();
				} else Toast.makeText(this, "Fault start of: "+newGame, Toast.LENGTH_SHORT).show();
				etCmd.setText("");
				break;
			}
			// ���� ��������� �� ��
		} else {
			Toast.makeText(this, "Wrong result", Toast.LENGTH_SHORT).show();
		}
	}
    
    // some text
	//@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bCmd: // ������ ����� �������
			postCommand();
			break;
		case R.id.imageView1:
			if (mt!=null && mt.l9!=null) 
				mt.l9.waitPictureToDraw(); 
			break;
		case R.id.bSpace:
			mt.keyPressed=' ';
			break;
		case R.id.bEnter:
			mt.keyPressed='\r';
			break;
		}
	}

	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		postCommand();
		return true;
	}
	
	void outCharToLog(char c) {
		if (mt.logStringCapacitor==null) mt.logStringCapacitor=new SpannableStringBuilder();
		 
		//every enter starts new paragraph
		if (c=='\n') outLogFlush(true);
		else mt.logStringCapacitor.append(c);
		
		//no unnecessary line breaks
		//if (c=='\n') {
		//	if (logStringCapacitor.length()>0 && logStringCapacitor.charAt(logStringCapacitor.length()-1)!='\n') logStringCapacitor.append(c);
		//} else 
		//	logStringCapacitor.append(c);
	};
	
	void outUserInputToLog(String str) {
		SpannableStringBuilder text = new SpannableStringBuilder(str);
        ForegroundColorSpan style = new ForegroundColorSpan(Color.rgb(0, 0, 255)); 
        text.setSpan(style, 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        if (mt.logStringCapacitor==null) mt.logStringCapacitor=new SpannableStringBuilder();
        mt.logStringCapacitor.append(text);
        outLogFlush(true);
	};
	
	void outLogFlush(boolean finishThisString) {
		if (mt.logStringCapacitor!=null && mt.logStringCapacitor.length()>0) {
			if ((mt.logStrId>=0) && (mt.logStrId<lvMain.getAdapter().getCount())) {
				mt.lvAdapter.getItem(mt.logStrId).append(mt.logStringCapacitor);
			} else {
				mt.lvAdapter.add(mt.logStringCapacitor);
			}
			mt.logStringCapacitor=null;
			if (finishThisString) mt.logStrId=-1;
			else mt.logStrId=lvMain.getAdapter().getCount()-1;
			lvMain.setSelection(lvMain.getAdapter().getCount()-1);
		};
	}
	
	void postCommand() {
		//TODO: ��� ������� - ������� �������, �������� � command - �� ������ � 3�4 �������
		if (etCmd.length()>0 && mt.l9.L9State==mt.l9.L9StateWaitForCommand) {
			outUserInputToLog(etCmd.getText().toString());
    
			mt.lvHistoryAdapter.add(etCmd.getText().toString());
			
			command=etCmd.getText().toString();
			etCmd.setText("");
		};

	};
	
	void postHashCommand(String cmd) {
		etCmd.setText(cmd);
		postCommand();
	};

	}

class DebugStorage {
    private char[] debug;
    private int debugptr;
    private static final int debugsize=500;
    DebugStorage() {
		debug=new char[debugsize];
		debugptr=0;
    }
    boolean putchar(char c) {
    	debug[debugptr++]=c;
    	return (debugptr>=debugsize);
    }
    String getstr() {
    	String str=String.valueOf(debug, 0, debugptr);
    	debugptr=0;
    	return str;
    }
}

