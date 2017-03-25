// Clase Puzzle : puzzle de BattleShip Solitarie. S'utilitza un model de celles, i s'apliquen tecniques
// humanes programades. Codi obsolet, millores a Yubotu.engine.

package dvtr.games.logic.BS10.puzzle;

import java.util.Arrays;

public class Puzzle {

	public byte C,R;			// mides
	public Vaixell[] B;			// Vaixells
	private final byte G[][];	// Grid

	public boolean generant;	// flags
	public boolean recursiu;

	public byte[] SR,SC;		// Sumes de solids x fila, x columna
	public CI[] I;				// Celles inicials mostrades

	// Constructor
	public Puzzle(byte[]sr,byte[]sc,CI[] i,int[] b){
		SR=sr.clone();
		SC=sc.clone();
		C=(byte) sr.length;
		R=(byte) sc.length;

		if (i==null)
		{
			I=new CI[1];
			I[0] = new CI(new Index(-1,-1),K.A);
		}
		else
			I=Arrays.copyOf(i,i.length);

		G=novaGrid(C,R);	// Resetejem grid i ponem valors inicials
		PosaInicials();

		recursiu = true; 	// Per defecte, resolució recursiva

		B=new Vaixell[b.length];	// Creem vaixells desde longituds
		for (int n=0;n<b.length;n++)
			B[n]=new Vaixell(b[n]);
	}

	// Constructor copia
	public Puzzle(Puzzle P){
		byte i,j;

		C = P.C;
		R = P.R;
		B = Arrays.copyOf(P.B,P.B.length);
		SR = P.SR.clone();
		SC = P.SC.clone();

		G = novaGrid(C,R);

		for (j=0;j<R;j++)
		for (i=0;i<C;i++)
		G[i][j] = P.G[i][j];

		generant = P.generant;
		recursiu = P.recursiu;
	}

	// Constructor puzzle buit
	public Puzzle(int c,int r){
		C  = (byte)c;
		R  = (byte)r;

		SR = new byte[C];
		SC = new byte[R];

		B  = new Vaixell[0];

		G  = novaGrid(C,R);
	}

	// aCI : comprobem si una cella es una cella inicial
	public boolean aCI(byte i,byte j){
		boolean r=false;

		for (int n=0;n<I.length;n++)
		r|=I[n].i.x==i&&I[n].i.y==j;

		return r;
	}

	// equals : compara puzzles
	public boolean equals(Puzzle P){
		boolean r=true;
		byte i,j;

		for (i=0;i<SR.length;i++)
		r &= SR[i]==P.SR[i];

		if (!r) return r;

		for (j=0;j<SC.length;j++)
		r &= SC[j]==P.SC[j];

		if (!r) return r;

		for (j=0;j<R;j++)
		for (i=0;i<C;i++)
			r&=G[i][j]==P.G[i][j];

		return r;
	}

	// novaGrid : crea una nova grid CxR
	public byte[][] novaGrid(int C,int R){
		byte[][]g = new byte[C][R];

		for (byte i=0;i<C;i++)
			Arrays.fill(g[i],K.I);

		return g;
	}

	// Coloca : coloca les longituds al grid de manera coherent (sense tocar aigua, creuarse o pasar els limits)
	public void Coloca(int []b){

		byte n; // contador
		boolean r,fet,colocat = false; // flags
		Index T; // Index cursor

		fet = false; // inici flag

		while(!fet)
		{
			GridIndex In = new GridIndex(C,R); // Creem llista de totes les posicions posibles
			In.shuffle();

			for (n=0;n<b.length;n++) // recorrem les longituds
			{
				colocat = false;
				for (Index i:In.I){ // Intentem colocar a i...

					r = true;
					T = i;

					//...horitzontalment, comprobant que les necesaries son indeterminades
					for (byte l=0;l<b[n];l++) { r&=i(T);T=T.E();}

					T = i;

					// si tot correcte...
					if (r) {

						// Posicions colindants son aigua
						det(T.O(),K.A);  In.remove(T.O());
						det(T.NO(),K.A); In.remove(T.NO());
						det(T.SO(),K.A); In.remove(T.SO());

						for (int l=0;l<b[n];l++) {

							det(T,b[n]==1?K.B:l==0?K.O:l==b[n]-1?K.E:K.X);In.remove(T);
							det(T.N(),K.A); In.remove(T.N());
							det(T.S(),K.A); In.remove(T.S());
							T=T.E();
						}

						det(T.N(),K.A);	In.remove(T.N());
						det(T.S(),K.A); In.remove(T.S());
						det(T,K.A);	    In.remove(T);

						colocat = true;
						break;
					}

					// si no ha pogut colocar-se, provem verticalment
					r = true;
					T = i;

					for (byte l=0;l<b[n];l++) { r&=i(T);T=T.S();}

					T = i;

					if (r) {

						det(T.NO(),K.A);  	In.remove(T.NO());
						det(T.N(),K.A); 	In.remove(T.N());
						det(T.NE(),K.A); 	In.remove(T.NE());

						for (byte l=0;l<b[n];l++) {

							det(T,b[n]==1?K.B:l==0?K.N:l==b[n]-1?K.S:K.X);In.remove(T);
							det(T.E(),K.A); In.remove(T.E());
							det(T.O(),K.A); In.remove(T.O());
							T=T.S();
						}

						det(T.E(),K.A);	In.remove(T.E());
						det(T.O(),K.A); In.remove(T.O());
						det(T,K.A);	    In.remove(T);
						colocat = true;
						break;
					}
				}

				if (!colocat) break; // Si alguna longitud no pot colocarse a cap posicio, tornem a començar
			}

			fet = colocat;
		}
	}

	// v : torna el valor , aigua si surt dels limits
	public byte v(int x,int y){if(x<0||x>=C||y<0||y>=R) return K.A; return G[x][y];}
	public byte v(Index i){return v(i.x,i.y);}

	// s : verdader si el valor es solid
	public boolean s(int x,int y){if(x<0||x>=C||y<0||y>=R) return false;return v(x,y)>K.A;}
	public boolean s(Index i){return s(i.x,i.y);}

	// i : verdader si el valor es indeterminat
	public boolean i(int x,int y){if(x<0||x>=C||y<0||y>=R) return false;return v(x,y)==K.I;}
	public boolean i(Index i){return i(i.x,i.y);}

	// d : verdader si el valor es determinat
	public boolean d(int x,int y){if(x<0||x>=C||y<0||y>=R) return true;return v(x,y)==K.A||v(x,y)>K.M;}
	public boolean d(Index i){return d(i.x,i.y);}

	// r : per a representacio mode consola
	public String r(int x,int y){if(x<0||x>=C||y<0||y>=R) return "·";return ""+K.REPR[v(x,y)+1];}
	public String r(Index i){return r(i.x,i.y);}

	// reset : torna x,y a indeterminat
	public void reset(int x,int y){G[x][y]=K.I;}

	// PosaInicials : coloca les celles inicials al grid
	public void PosaInicials(){ for (int n=0;n<I.length;n++) det(I[n].i,I[n].v);}

	// SC,SR : sumes actuals de solids per columna, fila
	public byte SC(int j){byte r=0;for(byte i=0;i<C;i++)if(s(i,j))r++;return r;}
	public byte SR(int i){byte r=0;for(byte j=0;j<R;j++)if(s(i,j))r++;return r;}

	// IC,IR : sumes actuals d'indeterminats per columna, fila
	public byte IC(int j){byte r=0;for(byte i=0;i<C;i++)if(i(i,j))r++;return r;}
	public byte IR(int i){byte r=0;for(byte j=0;j<R;j++)if(i(i,j))r++;return r;}

	// toString
	@Override
	public String toString(){String r="";for(int j=0;j<R;j++){for(int i=0;i<C;i++)r+=r(i,j)+" ";r+=" " + SC[j]+" "+SC(j)+" "+IC(j)+"\n";
	}r+="\n";for(int i=0;i<C;i++)r+=SR[i]+" ";r+="\n";for(int i=0;i<C;i++)r+=SR(i)+" ";r+="\n";for(int i=0;i<C;i++)r+=IR(i)+" ";r+="\n";
	for (int i=0;i<B.length;i++)r+="\n"+B[i].toString() + " " + (B[i].det()?"("+B[i].ro.x+","+B[i].ro.y+")("+B[i].rf.x+","+B[i].rf.y+")":"");return r;}

	// A : posa els indexs a aigua
	public boolean A(int x,int y){return det(x,y,K.A);}
	public boolean A(Index i)	{return det(i.x,i.y,K.A);}
	public boolean A(Index...I) {boolean r=true;for(Index i:I)r&=det(i.x,i.y,K.A); return r;}

	// M : posa x,y a solid
	public boolean M(int x,int y){return det(x,y,K.M);}

	// mV : comproba si a i esta el valor v
	public boolean mV(Index i,byte v){return d(i)&&v(i)==v;}

	// mX : comprobacions de valors a la cella i
	public boolean mM(Index i){return v(i)==K.M;}
	public boolean mA(Index i){return mV(i,K.A);}
	public boolean mA(int x,int y){ return mA(new Index(x,y));}
	public boolean mX(Index i){return mV(i,K.X);}
	public boolean mN(Index i){return mV(i,K.N);}
	public boolean mS(Index i){return mV(i,K.S);}
	public boolean mE(Index i){return mV(i,K.E);}
	public boolean mO(Index i){return mV(i,K.O);}
	public boolean mB(Index i){return mV(i,K.B);}
	public boolean mA(Index...I){boolean r=true;for(Index i:I)r&=mA(i);return r;}

	// det : determina la cella i amb el valor v
	public boolean det(Index i,byte v){return det(i.x,i.y,v);}
	public boolean det(int x,int y,byte v){

		if (v(x,y)>=v||d(x,y)) return false; // si ja esta determinat, sortim

		G[x][y] = v;
		return true;
	}

	// Reset : reseteja el puzzle
	public void Reset(){
		for (byte i=0;i<C;i++)
        for (byte j=0;j<R;j++)
        if (!aCI(i,j)) reset(i,j);
	}

	// PatronsAigua: tecnica de resolució : aplica a les peces solides l'aigua colindant
	public boolean PatronsAigua() {

		boolean r = false;
		Index I = new Index();

		for (I.y=0;I.y<R;I.y++)
		for (I.x=0;I.x<C;I.x++)
			switch(v(I)){
			case K.X :
			case K.M : r|=A(I.NO(),I.NE(),I.SO(),I.SE()); break;
			case K.B : r|=A(I.NO(),I.N(),I.NE(),I.O(),I.E(),I.SO(),I.S(),I.SE());break;
			case K.N : r|=A(I.NO(),I.N(),I.NE(),I.O(),I.E(),I.SO(),I.SE(),I.SO().S(),I.SE().S()); break;
			case K.S : r|=A(I.SO(),I.S(),I.SE(),I.O(),I.E(),I.NO(),I.NE(),I.NO().N(),I.NE().N()); break;
			case K.O : r|=A(I.NO(),I.N(),I.NE(),I.NE().E(),I.O(),I.SO(),I.S(),I.SE(),I.SE().E()); break;
			case K.E : r|=A(I.NO().O(),I.NO(),I.N(),I.NE(),I.E(),I.SO().O(),I.SO(),I.S(),I.SE()); break;
		}
		return r; // Tornem true si hem fet algun canvi
	}

	// AiguaxSumes : omplena daigua columnes/files resoltes
	public boolean AiguaxSumes(){
		boolean r=false;
		byte i,j;

		for (j=0;j<R;j++)
		for (i=0;i<C;i++)
			if (SR[i]==SR(i)||SC[j]==SC(j)) r|=A(i,j);

		return r; // Tornem true si hem fet algun canvi
	}

	// Solidsxindeterminats : omplena de solids columnes/files resol
	public boolean Solidsxindeterminats(){
		boolean r=false;
		byte i,j;

		for (j=0;j<R;j++)
		for (i=0;i<C;i++)
			if(SR[i]-SR(i)==IR(i)||SC[j]-SC(j)==IC(j)) r|=det(i,j,K.M);

		return r; // Tornem true si hem fet algun canvi
	}

	// xEquis : determina si una peça X es vertical o horitzontal
	public boolean xEquis(){
		boolean r = false;
		byte i,j;
		Index I = new Index();

		for (j=0;j<R;j++)
		for (i=0;i<C;i++)
		{
			I.x=i;
			I.y=j;

			if (mX(I)&&SR[i]<3){r|=det(I.N(),K.A);r|= det(I.S(),K.A);}
			if (mX(I)&&SC[j]<3){r|=det(I.O(),K.A);r|= det(I.E(),K.A);}
		}

		return r; // Tornem true si hem fet algun canvi
	}

	// MarcaSolid : segons valors colindants, marca valor solid segur
	public boolean MarcaSolid(){
		boolean r = false;
		boolean M;
		byte i,j;
		Index I = new Index(),
			  N,S,E,O;

		for (j=0;j<R;j++)
		for (i=0;i<C;i++)
		{
			I.x = i;
			I.y = j;

			N=I.N();
			S=I.S();
			E=I.E();
			O=I.O();

			M = mS(S)||mN(N)||mE(E)||mO(O); // si es la colindant a un extrem

			M |= mX(E)&&(mA(E.N())||mA(E.S())); // si a lest te peça de mig i aigua al n,s
			M |= mX(O)&&(mA(O.N())||mA(O.S())); // si a loest te peça de mig i aigua al n,s
			M |= mX(N)&&(mA(N.O())||mA(N.E())); // si al nord te peça de mig i aigua al e,o
			M |= mX(S)&&(mA(S.O())||mA(S.E())); // si al sud te peça de mig i aigua al e,o

			if (M) r|=det(I,K.M);
		}

		return r; // Tornem true si hem fet algun canvi
	}

	// MarcaExtrems : defineix tipus de peces solides segons celles colindants
	public boolean MarcaExtrems(){
		boolean r=false;
		byte i,j;
		Index I = new Index(),
			  N,S,E,O;

		for (j=0;j<R;j++)
		for (i=0;i<C;i++)
		{
			I.x = i;
			I.y = j;

			N=I.N();
			S=I.S();
			E=I.E();
			O=I.O();

			if (!mM(I)) continue; // Si no es solida, continuem
																	// ...
			if (mA(N.O(),N,N.E(),E,O,S.O(),S,S.E())) r|=det(I,K.B); // .O.
			else													// ...

			if ((mS(S)||mM(S)||mX(S))&&mA(O,E,N)) r|=det(I,K.N);	// ...
																	// .A.
																	// .X.
			else
			if ((mN(N)||mM(N)||mX(N))&&mA(O,E,S)) r|=det(I,K.S);    // .X.
																	// .V.
																	// ...
			else
			if ((mE(E)||mM(E)||mX(E))&&mA(N,S,O)) r|=det(I,K.O);	// ...
																 	// .<X
																 	// ...
			else
			if ((mO(O)||mM(O)||mX(O))&&mA(N,S,E)) r|=det(I,K.E);	// ...
																	// X>.
																	// ...
			else
			if (s(S)&&s(N)) r|=det(I,K.X);							//  .
																	//  X
																	//  .
			else
			if (s(E)&&s(O)) r|=det(I,K.X);							// .X.
		}

		return r; // Si hem fet algun canvi, tornem true
	}

	// HadeserAigua : determina si una cella pot ser o no solida (si es solida, 4 colindants son aigua,cosa que pot no
	// ser coherent amb les sumes)
	public boolean HadeserAigua(){
		boolean r=false,
				nps;
		byte i,j;
		Index I = new Index();
		int NO,SO,NE,SE;

		for (j=0;j<R;j++)
		for (i=0;i<C;i++)
		{
			nps = false;
			I.x = i;
			I.y = j;

			NO=i(I.NO())?1:0;
			SO=i(I.SO())?1:0;
			NE=i(I.NE())?1:0;
			SE=i(I.SE())?1:0;

			nps|=(NO==1||SO==1)&&(IR(i-1)-NO-SO<SR[i-1]-SR(i-1));
			nps|=(NE==1||SE==1)&&(IR(i+1)-NE-SE<SR[i+1]-SR(i+1));
			nps|=(NO==1||NE==1)&&(IC(j-1)-NO-NE<SC[j-1]-SC(j-1));
			nps|=(SO==1||SE==1)&&(IC(j+1)-SO-SE<SC[j+1]-SC(j+1));

			if (nps) r|=A(I);
		}
		return r; // Si hem fet algun canvi...
	}

	// TotesDeterminades : comproba que totes les celles tinguin un valor
	public boolean TotesDeterminades(){
		byte i,j;

		for(j=0;j<R;j++)
		for(i=0;i<C;i++)
			if (v(i,j)==K.I||v(i,j)==K.M) return false;

		return true;
	}

	// RestaVaixells : busca els vaixells al grid
	public boolean RestaVaixells(){
		boolean r = false;
		byte n,i,j;
		Index I = new Index(),
			  T;

		for (n=0;n<B.length;n++)
		{B[n].ro.x=-1;B[n].ro.y=-1;B[n].rf.x=-1;B[n].rf.y=-1;} // Indeterminem tots els vaixells

		for (j=0;j<R;j++)
		for (i=0;i<C;i++)
		{
			I.x=i;
			I.y=j;

			if (mB(I)) // si la cela es dun submari
			{
				for(n=0;n<B.length;n++)
				if((B[n].L==K.SUB)&&(!B[n].det())){
					B[n].ro=I;
					B[n].rf=I;
					break;
				}
			}
			else
			if (mN(I)) // Si es un inici nord
			{
				T=I;
				while(s(T)) T=T.S(); //Continuem mentre es solid per determinar longitud

				if (mS(T.N())) // arrivem al final sud
					for (n=0;n<B.length;n++)
						if ((B[n].L==T.y-I.y)&&(!B[n].det())) // Busquem un vaixell daquella longitud i el definim
							{B[n].ro=I;
								B[n].rf=T.N();break;}
			}
			else
			if (mO(I)) // Si es un inici oest, el mateix horitzontalment
			{
				T=I;
				while(s(T)) T=T.E();

				if (mE(T.O())) for (n=0;n<B.length;n++) if ((B[n].L==T.x-I.x)&&(!B[n].det())){B[n].ro=I;B[n].rf=T.O();break;}
			}
		}
		return r; // Si hem fet algun canvi tornem true
	}

	// CoherentSumes : torna true si les sumes son correctes
	public boolean CoherentSumes(){
		boolean r = true;
		byte n;

		for (n=0;n<C;n++)r&=SR(n)<=SR[n];
		for (n=0;n<R;n++)r&=SC(n)<=SC[n];

		return r;
	}

	// CoherentEspai : torna true si les peces solides no xoquen
	public boolean CoherentEspai(){
		boolean r = true;
		byte i,j;
		Index I = new Index();

		for (j=0;j<R&&r;j++)
		for (i=0;i<C;i++)
		{
			I.x=i;
			I.y=j;

			if (s(I)) r&=!(s(I.NO())||s(I.NE())||s(I.SO())||s(I.SE()));
		}

		return r;
	}

	// SumesResoltes : true si les sumes actuals son les demanades
	public boolean SumesResoltes(){
		boolean r = true;
		byte n;

		for(n=0;n<C;n++)r&=SR(n)==SR[n];
		for(n=0;n<R;n++)r&=SC(n)==SC[n];

		return r;
	}

	// Resolt : esta resolt si es compleix lo demanat
	public boolean Resolt(){
		return CoherentEspai()&&TotesDeterminades()&&SumesResoltes()&&TotsVaixellsTrobats();
	}

	// TotsVaixellsTrobats : comproba que els vaixells trobats siguin els demanats
	public boolean TotsVaixellsTrobats(){
		return VaixellsTrobats() == B.length;
	}

	// VaixellsTrobats : torna el nº de vaixells determinats
	public int VaixellsTrobats(){
		int r=0;
		for(byte n=0;n<B.length;n++)
			r+=B[n].det()?1:0;

		return r;
	}

	// ResolRecursivament : resol el puzzle aplicant varies tecniques. Si no es aixi,
	// sintenta resoldre per reducció al absurd
	public Puzzle ResolRecursivament(Puzzle P)
	{
		if (!P.CoherentSumes()) return null; // Error de coherencia a sumes, tornem
		if (P.VaixellsTrobats() > P.B.length) return null; // Error a vaixells trobats, tornem

		if (P.TotesDeterminades() && !P.SumesResoltes()) return null; // Error a sumes, tornem

		Puzzle Ret = new Puzzle(P); // copiem puzzle, per treballarhi

		boolean falta = true; // flag

		while (falta)
		{
			falta=false;

			falta|=Ret.PatronsAigua(); 			// apliquem patronsaigua
			falta|=Ret.AiguaxSumes();			// apliquem aiguaxsumes
			falta|=Ret.Solidsxindeterminats();	// apliquem solidxindeterminats
			falta|=Ret.MarcaSolid();			// apliquem marcasolid
			falta|=Ret.HadeserAigua();			// apliquem hadeseraigua
			falta|=Ret.xCapacitat();			// apliquem xcapacitat
			falta|=Ret.xEquis();				// apliquem xequis

			falta|=Ret.MarcaExtrems();			// marquem peces solides
			falta|=Ret.RestaVaixells();			// busquem vaixells

			if (Ret.TotesDeterminades()) falta = false; // si tot fet, sortim
		}

		if (recursiu&&generant)
		{
			byte i,j;

			// Recorrem els indeterminats i probem que siguin aigua, en cas que no estigui
			// correcte, sera solid
			for (j=0;j<Ret.R;j++)
			for (i=0;i<Ret.C;i++)
			{
				if (Ret.v(i,j) != K.I) continue;

				Ret.det(i,j,K.A);
				Ret = ResolRecursivament(Ret);
				if (Ret==null) {P.det(i,j,K.M); Ret = new Puzzle(P);}
			}

			if (Ret.Resolt()) return Ret;
			else return null;
		}

		return Ret;
	}

	// PosaH : funcio ajuda de xCapacitat, marca a les celles de g les longituds que poden tenir
	public void PosaH(Index I,int l,int[][] g){

		boolean ok = true;
		Index m = I;
		byte s = 0;

		for (byte n=0;n<l&&ok;n++)
		{
			ok &= !mA(m);  // comprobem que no sigui aigua (solid o indeterminat)
			s += s(m)?1:0; // si ja era solid, ho contem, per les sumes
			m = m.E();
		}

		ok &= SC(I.y)+(l-s)<=SC[I.y]; // coherent amb les sumes

		if (ok) // ok, la longitud es valida per la posicio I
		for (int n=0;n<l;n++)
			if (g[I.x+n][I.y]<l) g[I.x+n][I.y]=l;
	}

	// PosaV : funcio ajuda de xCapacitat, anàleg a PosaH verticalment
	public void PosaV(Index I,int l,int[][] g){

		boolean ok = true;

		Index m = I;

		byte s = 0;

		for (byte n=0;n<l&&ok;n++)
		{
			ok &= !mA(m);
			s += s(m)?1:0;
			m = m.S();
		}

		ok &= SR(I.x)+(l-s)<=SR[I.x];

		if (ok)
		for (int n=0;n<l;n++)
			if (g[I.x][I.y+n]<l) g[I.x][I.y+n]=l;
	}

	// xCapacitat : intenta colocar vaixells si les posicions valides son les minimes necesaries
	public boolean xCapacitat(){
		boolean r = false;
		Puzzle T = new Puzzle(this);
		Index I = new Index();
		byte n,i,j,l;


		// preparem una llista de longituds, a on el valor es el nº de vaixells de longitud=index
		byte mL = 0;
		for (n=0;n<B.length;n++)
		mL = B[n].L>mL?(byte)B[n].L:mL;

		int[] Ls = new int[mL+1];

		for (n=0;n<B.length;n++)
			Ls[B[n].L]++;

		// preparem un grid de treball, les celles indicaran la maxima longitud que pot tenir
		int [][]g = new int[C][R];

		for (i=0;i<C;i++)
		g[i] = new int[R];

		// posem les posibles longituds a la grid de treball
		for (n=1;n<mL+1;n++)
		for (j=0;j<R;j++)
		for (i=0;i<C;i++)
		{
			I.x=i;
			I.y=j;

			T.PosaH(I,n,g);
			T.PosaV(I,n,g);
		}

		// mirem quantes celles hi han en les que quepiguen cada longitud
		int[] c = new int[mL+1];

		for(j=0;j<R;j++)
		for(i=0;i<C;i++)
			c[g[i][j]]++;

		// recorrem les diverses longituds...
		for (l=0;l<c.length;l++)
			if (Ls[l]*l<=c[l]) // si les celles requerides daquella longitud son menors a les trobades
				for (j=0;j<R;j++) // (o el que es el mateix, les posicions existens per a certa longitud son les minimes...)
				for (i=0;i<C;i++) // (...significa que totes les celles que poden tenir aquella longitud son d'aquella longitud)
					if (g[i][j]<=l&&i(new Index(i,j)))
						T.det(i,j,K.M);

		if (T.CoherentSumes()&&T.CoherentEspai()) // tot correcte, passem el puzzle de treball al real
		{
			for (j=0;j<R;j++)
			for (i=0;i<C;i++)
			G[i][j] = T.G[i][j];
			r = true;
		}

		return r;
	}

	// Resol : resol el puzzle
	public Puzzle Resol(){

		return ResolRecursivament(this);
	}
}
