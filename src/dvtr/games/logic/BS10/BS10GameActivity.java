// BS10GameActivity : activity del joc

package dvtr.games.logic.BS10;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class BS10GameActivity extends Activity{

	public static String[] BT_LABELS;	// omplenar cella, columna o fila
	BS10GameView V;						// View del tauler
	AdView adView;						// View del banner

	SharedPreferences Settings;					// Settings
	SharedPreferences.Editor SettingsEditor;	// es guarda l'estat de l'eina a preferencies

	Chronometer chrono;							// cronometre

	//onCreate
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		Settings = getSharedPreferences(BS10Constants.prefName, 0);
		SettingsEditor = Settings.edit();

		// posem strings del buto eina
		setStrings();

		setContentView(R.layout.game);

		// Apliquem settings al cronometre
		chrono = (Chronometer)findViewById(R.id.time);

		if (Settings.getBoolean("ST",true)) chrono.setVisibility(View.VISIBLE);
		else								chrono.setVisibility(View.INVISIBLE);

		// Creem vista i l'adjuntem
		V = new BS10GameView(this);
		AttachBSView();
		V.ResetChrono();

		// Format grafic de l'activity
		getWindow().getAttributes().format = android.graphics.PixelFormat.RGBA_8888;

		// Posem el valor inicial del boto eina a les preferencies
		SettingsEditor.putInt("CP",BS10Constants.BT_FILLCELL);
		SettingsEditor.commit();

		// posem el valor del boto eina
		setgameCellPiece();
	}

	// AttachBSView : busquem el layout que suportara el BSView i l'adjuntem
	public void AttachBSView(){
		((ViewGroup)findViewById(R.id.gameLayout)).addView(V);

		DisplayMetrics metrics = new DisplayMetrics();

		getWindowManager().getDefaultDisplay().getMetrics(metrics);
    	float logicalDensity = metrics.density;

		V.Inicialitza(logicalDensity);
		V.drawThings();
	}

	// onResume : activem chrono (falta : recuperar de disc)
	@Override
	public void onResume(){
		super .onResume();

		setBackGround();
		setgameCellPiece();

		chrono.start();
	}

	// onStop : parem chrono (falta : guardar a disc)
	@Override
	public void onStop(){
		chrono.stop();
		super .onStop();
	}

	// setStrings : agafem les strings per al boto eina desde els recursos
	public void setStrings(){

		BT_LABELS = new String[3];

		BT_LABELS[BS10Constants.BT_FILLCELL] = getResources().getString(R.string.BT_FILLCELL);
		BT_LABELS[BS10Constants.BT_FILLCOL] = getResources().getString(R.string.BT_FILLCOL);
		BT_LABELS[BS10Constants.BT_FILLROW] = getResources().getString(R.string.BT_FILLROW);
	}

	// setBackGround : posem background segons set grafic
	public void setBackGround() {

		// no guardem res a memoria per no excedir VM Budget

		switch (Settings.getInt("GS", BS10Constants.GS_CLASSIC)) {
		case BS10Constants.GS_CLASSIC:
			((RelativeLayout)findViewById(R.id.gameLayout)).setBackgroundDrawable(getResources().getDrawable(R.drawable.menubkc_v)); break;
		case BS10Constants.GS_WWII:
			((RelativeLayout)findViewById(R.id.gameLayout)).setBackgroundDrawable(getResources().getDrawable(R.drawable.menubkw_v)); break;
		case BS10Constants.GS_NOBMPS:
			((RelativeLayout)findViewById(R.id.gameLayout)).setBackgroundDrawable(getResources().getDrawable(R.drawable.menubkn_v)); break;
		}
	}

	// gameCellPiece : click del boto eina
	public void gameCellPiece(View V){

		int d;

		switch(Settings.getInt("CP",BS10Constants.BT_FILLCELL)){
			case BS10Constants.BT_FILLCELL : d=BS10Constants.BT_FILLCOL; break;
			case BS10Constants.BT_FILLCOL : d=BS10Constants.BT_FILLROW; break;
			default : d=BS10Constants.BT_FILLCELL; break;
		}

		SettingsEditor.putInt("CP",d);
		SettingsEditor.commit();

		setgameCellPiece();
	}

	// NewGameClick : click del boto nou
	public void NewGameClick(View v){

   		V.NouPuzzle(); // Cridem a noupuzzle del BSView

   		SettingsEditor.putInt("CP",BS10Constants.BT_FILLCELL);
   		SettingsEditor.commit();
   		setgameCellPiece();

   		adView.loadAd(new AdRequest());
	}

	// ResetGameClick : click del reset
	public void ResetGameClick(View v){
		V.ResetPuzzle(); // Resetejem puzzle
	}

	// SolveGameClick : click de solucionar
	public void SolveGameClick(View v){
		V.ResolPuzzle(); // Resolem puzzle
	}

	// setgameCellPiece : posem texte al boto eina
	public void setgameCellPiece(){
//		((Button)findViewById(R.id.btPosa)).setText(BT_LABELS[Settings.getInt("CP",BS10Constants.BT_FILLCELL)]);
	}
}
