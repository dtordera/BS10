// AboutActivity: mostra informació del autor

package dvtr.games.logic.BS10;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;

public class BS10AboutActivity extends Activity{

	SharedPreferences Settings; // Settings, per llegir set de sprites

	//onCreate
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		Settings = getSharedPreferences(BS10Constants.prefName, 0);

		setContentView(R.layout.about);
	}

	//onResume
	@Override
	public void onResume(){
		super .onResume();

		setBackGround();
	}

	// Posem fons segons graphical set
	public void setBackGround(){

		// no guardem res a memoria per no excedir VM Budget

		switch (Settings.getInt("GS", BS10Constants.GS_CLASSIC)) {
		case BS10Constants.GS_CLASSIC:
			((LinearLayout)findViewById(R.id.aboutlayout)).setBackgroundDrawable(getResources().getDrawable(R.drawable.menubkc)); break;
		case BS10Constants.GS_WWII:
			((LinearLayout)findViewById(R.id.aboutlayout)).setBackgroundDrawable(getResources().getDrawable(R.drawable.menubkw)); break;
		case BS10Constants.GS_NOBMPS:
			((LinearLayout)findViewById(R.id.aboutlayout)).setBackgroundDrawable(getResources().getDrawable(R.drawable.menubkn)); break;
		}

	}
}
