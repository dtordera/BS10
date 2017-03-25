// FactoriaPuzzle : crea un puzzle donades unes mides i una flota de vaixells (longituds).
// Es crea el puzzle mitjançant Genera(). El métode és: Creació dun puzzle, colocacio dels vaixells
// de manera coherent, colocació de condicions inicials fins que sigui resoluble, eliminació de les
// condicions inicials innecesaries.
// NOTA : codi obsolet, nova versió a Yubotu.engine

package dvtr.games.logic.BS10.puzzle;

import java.util.Arrays;
import java.util.Random;

public class FactoriaPuzzle {

	int C,R;			// Columnes i files
	int[] B;			// Vaixells (llista de longituds)
	boolean Recursiu;	// S'aplica recursivitat o no

	Random rnd;			// random generator

	// Constructor
	public FactoriaPuzzle(int c,int r,int[] b, boolean rec){
		C = c;
		R = r;

		rnd = new Random();

		Recursiu = rec;
		B = b.clone();
	}

	// Genera : torna un puzzle sense resoldre
	public Puzzle Genera(){

		CI[] I = new CI[1];		// Condicions inicials
		boolean Totfet = false;	// flag bucle creació
		byte i,j;				// contadors
		byte x,y;				// coordenades nova CI
		int z;					// contador

		Puzzle P = null;		// Puzzle

		while (!Totfet){

			P = new Puzzle(C,R); // Creem puzzle
			P.Coloca(B);		 // Coloquem vaixells

			P.recursiu = Recursiu;	// activem flags
			P.generant = true;		//

			for (j=0;j<P.R;j++)			// resetejem
				for (i=0;i<P.C;i++)
					if (P.i(i,j)) P.A(i,j);

			for (j=0;j<P.R;j++)	// Calculem sumes
				P.SC[j]=P.SC(j);

			for (i=0;i<P.C;i++)
				P.SR[i] = P.SR(i);

			boolean minimcelles = false;

			while(!minimcelles) // Busquem el mínim de CIs per a que sigui resoluble
			{
				x = (byte)rnd.nextInt(C);
				y = (byte)rnd.nextInt(R);

				I[I.length-1] = new CI(x,y,P.v(x,y));
				minimcelles = new Puzzle(P.SR,P.SC,I,B).Resol().equals(P);

				if (!minimcelles)  I=Arrays.copyOf(I,I.length+1);
			}

			// Recorrem les condicions inicials i eliminem les que no afecten a la resolució
			for (z=0;z<I.length;z++)
			{
				if (I[z].v==K.A&&(P.SR[I[z].i.x]==0||P.SC[I[z].i.y]==0)) I[z]=new CI(-1,-1,K.A);

				CI[] bak = Arrays.copyOf(I,I.length);

				I[z]=new CI(-1,-1,K.A);

				if (!new Puzzle(P.SR,P.SC,I,B).Resol().equals(P)) I[z]=bak[z];
			}

			Totfet = true;
		}

		// Retornem puzzle
		return new Puzzle(P.SR,P.SC,I,B);
	}
}
