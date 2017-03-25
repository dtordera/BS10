// Clase Vaixell : longitud + posicio + orientacio

package dvtr.games.logic.BS10.puzzle;

public class Vaixell {
	public int L;	// longitud
	Index ro,rf;	// posicio inicial i final

	// Constructor
	public Vaixell(int l){
		L=l;
		ro = new Index();
		rf = new Index();
	}

	// toString
	@Override
	public String toString(){
		String r="";

		for (int i=0;i<L;i++)
			r+= L==1?K.REPR[K.B+1]:i==0?K.REPR[K.O+1]:i==L-1?K.REPR[K.E+1]:K.REPR[K.X+1];

		return r;
	}

	// det : determinat si la posicio es correcte
	public boolean det(){return ro.x!=-1;}
}
