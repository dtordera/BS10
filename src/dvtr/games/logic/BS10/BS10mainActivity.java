// BS10mainActivity : Activity inicial. menu.

package dvtr.games.logic.BS10;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BS10mainActivity extends Activity {

	SharedPreferences Settings; // preferencies, per agafar el set grafic

	// onCreate
	@Override
	public void onCreate(Bundle savedInstanceState) {

		System.gc(); // cridem gc per alliberar tota la memoria posible al iniciar

		super.onCreate(savedInstanceState);
		Settings = getSharedPreferences(BS10Constants.prefName, 0); // trobem settings

		setContentView(R.layout.menu); // posem view
	}

	// onStop
	@Override
	protected void onStop() {
		super.onStop();
	}

	// SetTitleBackGround : posem fons segons set grafic
	public void SetTitleBackGround() {

		// no guardem res a memoria per no excedir VM Budget

		switch (Settings.getInt("GS", BS10Constants.GS_CLASSIC)) {
		case BS10Constants.GS_CLASSIC:
			((LinearLayout)findViewById(R.id.mainLayout)).setBackgroundDrawable(getResources().getDrawable(R.drawable.menubkc)); break;
		case BS10Constants.GS_WWII:
			((LinearLayout)findViewById(R.id.mainLayout)).setBackgroundDrawable(getResources().getDrawable(R.drawable.menubkw)); break;
		case BS10Constants.GS_NOBMPS:
			((LinearLayout)findViewById(R.id.mainLayout)).setBackgroundDrawable(getResources().getDrawable(R.drawable.menubkn)); break;
		}

		// Posem altre cop el texte "new game" desde "loading..."
		((TextView)findViewById(R.id.twOpcio1)).setText("new game");
	}

	// onResume : posem fons
	@Override
	protected void onResume(){
		super.onResume();

		System.gc();

		SetTitleBackGround();
	}

	// optionExit : click d'exit, sortim
	public void optionExit(View V) {
		setResult(RESULT_OK);
		finish();
	}

	// optionNewGame : click de NewGame, iniciem BS10GameActivity
	public void optionNewGame(View V) {

		((TextView)findViewById(R.id.twOpcio1)).setText("loading...");

		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), BS10GameActivity.class);

		startActivity(intent);
	}

	// optionHowToPlay : click de howto, iniciem BS10HowToPlayActivity
	public void optionHowToPlay(View V) {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), BS10HowToPlayActivity.class);

		startActivity(intent);
	}

	// optionSettings : click de settings, iniciem BS10SettingsActivity
	public void optionSettings(View V) {

		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), BS10SettingsActivity.class);

		startActivity(intent);
	}

	// optionAbout : click d'about, iniciem BS10AboutActivity
	public void optionAbout(View V) {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), BS10AboutActivity.class);

		startActivity(intent);
	}
}