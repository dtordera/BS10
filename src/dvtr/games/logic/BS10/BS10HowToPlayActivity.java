// BS10HowToPlayActivity : mostra les instruccions, amb un parell de botons per pasar de pagines

package dvtr.games.logic.BS10;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BS10HowToPlayActivity extends Activity {

	SharedPreferences Settings; // preferencies

	byte idx; // index de pagina

	// Textes
	String[] Text = {"BATTLESHIP SOLITAIRE v1.1\n\nBattleShip Solitaire is an implementation of the logical game with same name (a.k.a Yubotu, a.k.a Bimaru) invented in Argentina by Jaime Poniachik. In it, player must use logic to discover a hidden fleet. In the game board you will find a set of cells, ocassionally pre-discovered cells, and a column and a row with numbers. This numbers indicates how many solid cells are in each row/column. There are several techniques to discover the boats with that clues.",
					 "Ships size and number are indicated. Ships are all surrounded by water (or board limits). Cells can be a 1-length ship, a ship\'s beginning or ending, ship\'s middle piece, solid, water or unknown. So, first thing player can do is surround known solid pieces by water (for example, a beginning piece sure got water in each adjacent square except the opened one).",
					 "Next thing you can do is check row/column sums. If any of them is zero, means that all row/column is water. If unknown cells of a row/column coincides with the sum, or sum minus solid discovered pieces are same number as unknown cells, that means all unknown cells are solid.",
					 "Another way to determine cells is check if they can be solid. If were solid, then the NO,NE,SO,SE squares must be water. Can happens that rows/column adjacent to our cell will become incoherent if check cell is solid. For example, supose a 2-sum row, all water except only 3 undetermined joined cells. Cells just over and just down, in the middle of that space, must be water, so if not, 2 cells in our empty row will become water and then will be only one empty cell in a row with a sum of 2, so it will be incoherent.",
					 "You can determine boats by their only posible positions. For example, 4-lenght ships can only be in a 4 or more sum-row/column. You can determine solid cells this way: for example, if a 4-length ship must be in a row with 5 empty cells in-a-row, then the middle, and the two adjacent cells sure are solid.",
					 "Another way to solve the puzzle is via reduction to absurd. If you have some posibilities, you can try one of them and see what happens if you continue. If it falls into a contradiction, then the being was wrong. Because of the multiple posibilities, this technique is better to use only at final.",
					 "There are a lot of techniques and strategies to solve this puzzle game. Feel free to seek for them. Enjoy!"
					 };

	// onCreate
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		Settings = getSharedPreferences(BS10Constants.prefName, 0);

		idx = 0;

		setContentView(R.layout.howtoplay);

		setText(idx);
	}

	// setText : posa el texte segons la pagina, visibilitat dels butons seg, prev
	public void setText(byte idx){
		((TextView) findViewById(R.id.instructext)).setText(Text[idx]);

		((TextView) findViewById(R.id.nextbt)).setVisibility(idx==Text.length-1?View.INVISIBLE:View.VISIBLE);
		((TextView) findViewById(R.id.prevbt)).setVisibility(idx==0?View.INVISIBLE:View.VISIBLE);
	}

	// onResume : posa fons
	@Override
	public void onResume(){
		super .onResume();

		setBackGround();
	}

	// nextClick : avança pagina
	public void nextClick(View V){
		if (idx < Text.length-1) idx++;

		setText(idx);
	}

	// previousClick : retrocedeix pagina
	public void previousClick(View V){
		if (idx > 0) idx--;

		setText(idx);
	}

	// setBackGround : posa fons segons set grafic
	public void setBackGround(){
		// no guardem res a memoria per no excedir VM Budget

		switch (Settings.getInt("GS", BS10Constants.GS_CLASSIC)) {
		case BS10Constants.GS_CLASSIC:
			((LinearLayout)findViewById(R.id.howto)).setBackgroundDrawable(getResources().getDrawable(R.drawable.menubkc)); break;
		case BS10Constants.GS_WWII:
			((LinearLayout)findViewById(R.id.howto)).setBackgroundDrawable(getResources().getDrawable(R.drawable.menubkw)); break;
		case BS10Constants.GS_NOBMPS:
			((LinearLayout)findViewById(R.id.howto)).setBackgroundDrawable(getResources().getDrawable(R.drawable.menubkn)); break;
		}
	}
}
