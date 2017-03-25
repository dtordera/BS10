// BS10SettingsActivity : activity de preferencies

package dvtr.games.logic.BS10;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BS10SettingsActivity extends Activity {

	private String 	GS_NOBMPS,	// estils de grafic
				   	GS_WWII,
				   	GS_CLASSIC;

	private String 	SZ_TINY,	// tamanys
					SZ_SMALL,
					SZ_NORMAL;

	private String  ST_TRUE,	// show time
					ST_FALSE;

	SharedPreferences Settings;
	SharedPreferences.Editor SettingsEditor;

	// onCreate
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		Settings = getSharedPreferences(BS10Constants.prefName, 0);
		SettingsEditor = Settings.edit();

		setStrings();
		setContentView(R.layout.settings);
	}

	// setStrings : agafa strings desde els recursos
	public void setStrings(){
		Resources Res = getResources();

		GS_NOBMPS  = Res.getString(R.string.GS_NOBMPS);
		GS_WWII    = Res.getString(R.string.GS_WWII);
		GS_CLASSIC = Res.getString(R.string.GS_CLASSICAL);

		SZ_TINY   = Res.getString(R.string.SZ_TINY);
		SZ_SMALL  = Res.getString(R.string.SZ_SMALL);
		SZ_NORMAL = Res.getString(R.string.SZ_NORMAL);

		ST_TRUE   = Res.getString(R.string.ST_TRUE);
		ST_FALSE  = Res.getString(R.string.ST_FALSE);
	}

	// onResume : posa fons, textes
	@Override
	protected void onResume(){
		super.onResume();

		setSettingsBackGround();
		setSizeText();
		setShowTimeText();
	}

	// setSettingsBackGround : posa fons
	public void setSettingsBackGround() {

		switch (Settings.getInt("GS", BS10Constants.GS_CLASSIC)) {
		case BS10Constants.GS_WWII:
			((LinearLayout)findViewById(R.id.settingsLayout)).setBackgroundDrawable(getResources().getDrawable(R.drawable.menubkw));
			((TextView)findViewById(R.id.setting1)).setText(GS_WWII);
			break;
		case BS10Constants.GS_NOBMPS:
			((LinearLayout)findViewById(R.id.settingsLayout)).setBackgroundDrawable(getResources().getDrawable(R.drawable.menubkn));
			((TextView)findViewById(R.id.setting1)).setText(GS_NOBMPS);
			break;
		default:
			((LinearLayout)findViewById(R.id.settingsLayout)).setBackgroundDrawable(getResources().getDrawable(R.drawable.menubkc));
			((TextView)findViewById(R.id.setting1)).setText(GS_CLASSIC);
		}
	}

	// settingGraphicalSet : alterna set grafic
	public void settingGraphicalSet(View V) {

		int n;

		switch(Settings.getInt("GS",BS10Constants.GS_CLASSIC)) {
			case BS10Constants.GS_CLASSIC : n = BS10Constants.GS_WWII; break;
			case BS10Constants.GS_WWII    : n = BS10Constants.GS_NOBMPS; break;
			default : n = BS10Constants.GS_CLASSIC;
		}

		SettingsEditor.putInt("GS", n);
		SettingsEditor.commit();

		setSettingsBackGround();
	}

	// settingSize : alterna tamanys
	public void settingSize(View V){
		int d;

		switch(Settings.getInt("SZ",BS10Constants.SZ_NORMAL)){
			case BS10Constants.SZ_TINY  : d=BS10Constants.SZ_SMALL; break;
			case BS10Constants.SZ_SMALL : d=BS10Constants.SZ_NORMAL; break;
			default                     : d=BS10Constants.SZ_TINY; break;
		}

		SettingsEditor.putInt("SZ",d);
		SettingsEditor.commit();

		setSizeText();
	}

	// setSizeText : posa texte del size
	public void setSizeText(){
		String s;

		switch(Settings.getInt("SZ",BS10Constants.SZ_NORMAL)){
		case BS10Constants.SZ_TINY   : s = SZ_TINY; break;
		case BS10Constants.SZ_SMALL  : s = SZ_SMALL; break;
		default : s = SZ_NORMAL;
		}

		((TextView)findViewById(R.id.setting2)).setText(s);
	}

	// settingShowTime : alterna valor show time
	public void settingShowTime(View V){
		SettingsEditor.putBoolean("ST",!Settings.getBoolean("ST",true)).commit();

		setShowTimeText();
	}

	// setShowTimeText : posa show time text
	public void setShowTimeText(){
		((TextView)findViewById(R.id.setting3)).setText(Settings.getBoolean("ST",true)?ST_TRUE:ST_FALSE);
	}
}
