// Clase GridIndex : clase ajuda de per a crear un conjunt d'indexs desendreçats.
// Nota : no sutilitza ArrayList per a optimitzar per velocitat i memoria

package dvtr.games.logic.BS10.puzzle;

import java.util.Arrays;
import java.util.Collections;

public class GridIndex {

	public Index[] I; //Llista d'indexs

	// Constructor
	public GridIndex(int C,int R){

		I = new Index[C*R];

		for (int j=0;j<R;j++)			// Omplenem la llista amb totes les posicions de CxR
		for (int i=0;i<C;i++)
			I[j*C+i] = new Index(i,j);
	}

	// Shuffle : desendreça la llista
	public void shuffle(){
		Collections.shuffle(Arrays.asList(I));
	}

	// Selimina de la llista l'index (x,y)
	public void remove(byte x, byte y){

		for (int n=0;n<I.length;n++)
			if (I[n].x==x&&I[n].y==y) {

				for (int m=n;m<I.length-1;m++) I[m] = I[m+1];

				I = Arrays.copyOf(I,I.length-1);
				return;
			}
	}
	public void remove(Index i){ remove(i.x,i.y); }
}
