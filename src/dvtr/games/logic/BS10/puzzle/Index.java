// Clase Index : localitzacio duna cella i colindants

package dvtr.games.logic.BS10.puzzle;

public class Index {
	public byte x,y;

	// Constructors
	public Index(int i,int j){x=(byte)i;y=(byte)j;}
	public Index(){x=-1;y=-1;}

	// Colindants
	public Index N(){return new Index(x,y-1);}
	public Index S(){return new Index(x,y+1);}
	public Index E(){return new Index(x+1,y);}
	public Index O(){return new Index(x-1,y);}
	public Index NE(){return new Index(x+1,y-1);}
	public Index NO(){return new Index(x-1,y-1);}
	public Index SE(){return new Index(x+1,y+1);}
	public Index SO(){return new Index(x-1,y+1);}
}
