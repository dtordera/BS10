// CI : condicions inicials. Celles inicials mostrades

package dvtr.games.logic.BS10.puzzle;

public class CI {
	public Index i; // localització
	byte  v;		// valor

	// Constructors
	public CI(int x,int y,byte g){i=new Index(x,y);v=g;}
	public CI(Index j,byte g){i=j;v=g;}
}
