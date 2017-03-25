// BS10GameView : tauler del joc

package dvtr.games.logic.BS10;

import dvtr.games.logic.BS10.puzzle.FactoriaPuzzle;
import dvtr.games.logic.BS10.puzzle.Index;
import dvtr.games.logic.BS10.puzzle.K;
import dvtr.games.logic.BS10.puzzle.Puzzle;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class BS10GameView extends View {

	final static byte MIN_CELLES_DIBUIXADES=4; // minimes celles (o maxim zoom)

	BS10Bitmaps bmps;			// Bitmaps
	SharedPreferences Settings;	// preferencies

    Bitmap 	 PuzzleBitmap;		// Bitmap del puzzle
    Canvas   PuzzleCanvas;		// Canvas del bitmap del puzzle
    Bitmap 	 LeftBitmap;		// Bitmap left (vaixells)
    Canvas	 LeftCanvas;		// Canvas del leftbitmap

    Paint	 numerosPaint, indPaint, bmpPaint; // Estils

    int textsizeX,textsizeY;	// tamany del texte de les sumes
    int screenHeight,screenWidth;	// mides de la pantalla
    int mincellsize, maxcellsize, currentcellsize;	// mides de les celles
    int idX,idY,cdX,cdY; // cella dibuixada inicial, total celles dibuixades
    int PuzzleX;		 // Posicio X del puzzlebitmap
    int PuzzleY;		 // screenHeight - bannerY
    boolean ResoltFlag;	 // flag de resolt
    ProgressDialog pd;	 // progress dialog
    boolean okinvalidate;

    public 	Puzzle P,S;  // Puzzles de joc i solucio
	byte 	SZ;			 // tamany

    private ScaleGestureDetector mScaleDetector; // Detector del zoom

    // Constructor
	public BS10GameView(Context context) {
		super(context);

		// Busquem preferencies i treiem el tamany
		Settings = context.getSharedPreferences(BS10Constants.prefName,0);
		SZ = (byte)Settings.getInt("SZ",BS10Constants.SZ_NORMAL);

		okinvalidate = false;
	}

	public void Inicialitza(float l){
		InitView(l);
		FerPuzzle();
		okinvalidate = true;
		invalidate();
	}

	// ResetChrono : busquem el chronoview al parent i el reiniciem
	void ResetChrono(){
		Chronometer C = (Chronometer)((View)getParent()).findViewById(R.id.time);

		if (C!=null){
			C.setBase(SystemClock.elapsedRealtime());
			C.start();
		}
	}

	// StopChrono : busquem el chronoview al parent i el parem
	void StopChrono(){
		Chronometer C = (Chronometer)((View)getParent()).findViewById(R.id.time);

		if (C!=null) C.stop();
	}

	// FerPuzzle : Creem un nou puzzle
	public void FerPuzzle(){
		System.gc();

		int d[]; // flota segons tamany puzzle

		switch(SZ){
		case BS10Constants.SZ_TINY   : d = new int[] {3,2,2,1,1}; break;
		case BS10Constants.SZ_SMALL  : d = new int[] {4,3,2,2,1,1,1}; break;
		default : d = new int[] {4,3,3,2,2,2,1,1,1,1};
		}

		// Creem puzzle
		P = new FactoriaPuzzle(SZ,SZ,d,false).Genera();

		// Busquem la solucio
		S = P.Resol();

		// Resetejem flag
		ResoltFlag = false;
	}

	// Nou Puzzle : inicia progressdialog, crea puzzle, reseteja chrono, etc...
	public void NouPuzzle(){

		pd = ProgressDialog.show(getContext(),null,"Generating new " + SZ + "x" + SZ + " puzzle...",true,false);

		new NewGameThread().start();

		bmps.ferFonsMar(screenHeight);
		drawThings();
	}

	// NewGameThread : thread de creacio de nou joc
	private class NewGameThread extends Thread {

        @Override
        public void run() {

        	StopChrono();
       		FerPuzzle();

            handler.sendEmptyMessage(0);
        }

        private final Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                pd.dismiss();
                ResetPuzzle();
                ResetChrono();
            }
        };
    }

	// ResetPuzzle : reiniciem puzzle
	public void ResetPuzzle(){
		P.Reset();
		drawThings();
	}

	// ResolPuzzle : mostra solucio
	public void ResolPuzzle(){

		for (int i=0;i<P.C;i++)
		for (int j=0;j<P.R;j++)
		{
			P.reset(i,j);
			P.det(i,j,S.v(i,j));
		}

		drawThings();
	}

	// InitView : inicia view
	public void InitView(float logicalDensity){

		// Creem bitmaps
		bmps = new BS10Bitmaps(getContext());

		// Creem estils
    	numerosPaint = new Paint();
    	numerosPaint.setColor(Color.WHITE);
    	numerosPaint.setAlpha(0xbb);
    	numerosPaint.setAntiAlias(true);
    	numerosPaint.setTextSize(19);

    	indPaint = new Paint();
    	indPaint.setColor(0x000044);
    	indPaint.setAlpha(0xff);
    	indPaint.setAntiAlias(true);

    	bmpPaint = new Paint();
    	bmpPaint.setAntiAlias(true);
    	bmpPaint.setDither(true);

    	// Trobem mides necesaries per al dibuix
    	Rect TextSize = new Rect();
   		numerosPaint.getTextBounds("0",0,1,TextSize);

    	textsizeX = TextSize.right - TextSize.left;
    	textsizeY = TextSize.bottom - TextSize.top;

    	WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    	Display display = wm.getDefaultDisplay();

    	screenHeight = display.getHeight();
    	screenWidth  = display.getWidth();

    	//LinearLayout L = (LinearLayout)((View)getParent().getParent().getParent()).findViewById(R.id.bannerLayout);

    	//20 = height linearlayout.
    	int px = (int) (20 * logicalDensity + 0.5);

    	PuzzleY = screenHeight;//L.getHeight();
    	PuzzleX = (screenWidth - PuzzleY)/2;;

    	mincellsize = (PuzzleY - textsizeY - 2) / SZ;
    	maxcellsize = (PuzzleY - textsizeY - 2) / MIN_CELLES_DIBUIXADES;

    	currentcellsize = mincellsize;

    	// cella inicial dibuixada
    	idX = idY = 0;

    	// celles totals dibuixades
    	cdX = cdY = SZ;

    	// Creem detector del zoom
   		mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());

   		setFocusable(true);

   		// fem bitmap de fons
		bmps.ferFonsMar(PuzzleY);

		// Creem bitmaps
    	PuzzleBitmap = Bitmap.createBitmap(mincellsize*SZ+textsizeX*2,PuzzleY,Bitmap.Config.ARGB_8888);
    	PuzzleCanvas = new Canvas(PuzzleBitmap);

    	LeftBitmap = Bitmap.createBitmap(150,PuzzleY,Bitmap.Config.ARGB_8888);
    	LeftCanvas = new Canvas(LeftBitmap);
	}

	// drawThings : dibuixa puzzlebitmap i leftbitmap
	public void drawThings(){

		drawPuzzle();
		drawBoats();

		invalidate();
	}

	// drawBoats : dibuixem vaixells que queden
	public void drawBoats(){

		// Establim un r de dibuix
//		Rect r = new Rect(0,0,150,screenHeight);
		Rect r = new Rect(0,0,150,PuzzleY);

		// Busquem vaixells al puzzle
		P.RestaVaixells();

		int j=0;
		int k,q=10;
		int s=25;

		// Borrem bitmap
		LeftBitmap.eraseColor(Color.TRANSPARENT);

		// Recorrem vaixells...
		for (int i=0;i<P.B.length;i++)
		{
			if (P.B[i].det()) continue; // Nomes volem els no determinats

			k = PuzzleY - 10;

			// dibuixem el vaixell...
			for (int l=0;l<P.B[i].L;l++)
			{
				r = new Rect(q + l*s,k-(j+1)*s,q + (l+1)*s-1,k-(j*s)-2);

				if (P.B[i].L==1)
					LeftCanvas.drawBitmap(bmps.B(l),null,r,bmpPaint);
				else
				if (l==0)
					LeftCanvas.drawBitmap(bmps.O(l),null,r,bmpPaint);
				else
				if (l==P.B[i].L-1)
					LeftCanvas.drawBitmap(bmps.E(l),null,r,bmpPaint);
				else
					LeftCanvas.drawBitmap(bmps.H(l),null,r,bmpPaint);
			}
			j++;
		}
		// Busquem el layout corresponent i posem el bitmap
//		((LinearLayout)((LinearLayout)getParent()).findViewById(R.id.barcoslayout)).setBackgroundDrawable(new BitmapDrawable(LeftBitmap));
	}

	// PosaPeces : segons celles colindants, posem tipus de peça solida (funcio semblan a puzzle.marcaextrems)
    public void PosaPeces(){

    	byte i,j,n;
    	Index I = new Index(),
    			N,S,E,O;

    	for (n=0;n<2;n++)
    	for (j=0;j<P.R;j++)
    	for (i=0;i<P.C;i++)
    	{
    		if (P.aCI(i,j)) continue; // si era condicio inicial, contiuem...

    		I.x = i;
    		I.y = j;

    		if (!P.s(I)) continue; // no es solida, continuem

    		N=I.N();
    		S=I.S();
    		E=I.E();
    		O=I.O();

    		P.reset(i,j);

    		if (P.mA(N.O(),N,N.E(),E,O,S.O(),S,S.E())) P.det(I,K.B);
    		else
   			if ((P.mS(S)||P.mM(S)||P.mX(S))&&P.mA(O,E,N)) P.det(I,K.N);
   			else
			if ((P.mN(N)||P.mM(N)||P.mX(N))&&P.mA(O,E,S)) P.det(I,K.S);
			else
			if ((P.mE(E)||P.mM(E)||P.mX(E))&&P.mA(N,S,O)) P.det(I,K.O);
			else
			if ((P.mO(O)||P.mM(O)||P.mX(O))&&P.mA(N,S,E)) P.det(I,K.E);
    		else
    		if (P.s(S)&&P.s(N)) P.det(I,K.X);
    		else
    		if (P.s(E)&&P.s(O)) P.det(I,K.X);
    		else
    		P.det(I,K.M);
    	}
    }

    // drawBitmap : dibuixa la cella i,j
    public void drawBitmap(int i, int j){

    	// Limits
    	Rect R = new Rect((i-idX)*currentcellsize,(j-idY)*currentcellsize,(i-idX+1)*currentcellsize-1,(j-idY+1)*currentcellsize-1);

    	// indeterminada...
    	if (P.i(i,j))
    	{
    		PuzzleCanvas.drawRect(R,indPaint);
    		return;
    	}

    	// cada tipus de peça te dos posibles bitmaps, m per determinar quin de manera invariable
    	// pero sense patro fixe
    	int m=i+j+P.SR[i]+P.SC[j];

    	// dibuixem fons
   		PuzzleCanvas.drawBitmap(bmps.FonsMar,
    		new Rect(i*mincellsize,j*mincellsize,(i+1)*mincellsize-1,(j+1)*mincellsize-1),R,bmpPaint);

   		// segons valor, posem bitmap
   		switch(P.v(i,j)){
   		case K.N : PuzzleCanvas.drawBitmap(bmps.N(m),null,R,bmpPaint); break;
   		case K.O : PuzzleCanvas.drawBitmap(bmps.O(m),null,R,bmpPaint); break;
   		case K.E : PuzzleCanvas.drawBitmap(bmps.E(m),null,R,bmpPaint); break;
   		case K.S : PuzzleCanvas.drawBitmap(bmps.S(m),null,R,bmpPaint); break;
   		case K.B : PuzzleCanvas.drawBitmap(bmps.B(m),null,R,bmpPaint); break;
   		case K.X : if (P.mA(i-1,j)||P.mA(i+1,j)) {PuzzleCanvas.drawBitmap(bmps.V(m),null,R,bmpPaint); break;}
   				   else
   				   if (P.mA(i,j-1)||P.mA(i,j+1)) {PuzzleCanvas.drawBitmap(bmps.H(m),null,R,bmpPaint); break;}
   		case K.M : PuzzleCanvas.drawBitmap(bmps.M(),null,R,bmpPaint);
   		}
    }

    // drawPuzzle : dibuixa puzzlebitmap
    public void drawPuzzle(){

    	PosaPeces(); // marquem extrems

    	// borrem
    	PuzzleCanvas.drawColor(Color.BLACK);

    	int i,j;

    	// dibuixem celles
    	for (j=idY;j<idY+cdY;j++)
    	for (i=idX;i<idX+cdX;i++)
    		drawBitmap(i,j);

    	// dibuixem sumes
    	for (i=idX;i<idX+cdX;i++)
    		PuzzleCanvas.drawText(""+P.SR[i],(i-idX)*currentcellsize+currentcellsize/2-textsizeX/2,PuzzleY - 3,numerosPaint);

    	for (j=idY;j<idY+cdY;j++)
    		PuzzleCanvas.drawText(""+P.SC[j],currentcellsize*cdX+5,(j-idY+1)*currentcellsize-currentcellsize/2+textsizeY/2, numerosPaint);

    	// si es veuen els limits, dibuixem limits
   		Paint p = new Paint();
   		p.setColor(Color.CYAN);
   		p.setAlpha(0xaa);

    	if (idX==0)
    		PuzzleCanvas.drawLine(0,0,0,cdY*currentcellsize-1,p);

    	if (idX+cdX==SZ)
    		PuzzleCanvas.drawLine(cdX*currentcellsize-1,0,cdX*currentcellsize-1,cdY*currentcellsize-1,p);

    	if (idY==0)
    		PuzzleCanvas.drawLine(0,0,cdX*currentcellsize-1,0,p);

    	if (idY+cdY==SZ)
    		PuzzleCanvas.drawLine(0,cdY*currentcellsize-1,cdX*currentcellsize-1,cdY*currentcellsize-1,p);
    }

    // onDraw : custom draw
    @Override protected void onDraw(Canvas canvas){
    	super .onDraw(canvas);

    	if (!okinvalidate) return;

    	// dibuixem Puzzlebitmap
    	canvas.drawBitmap(PuzzleBitmap,PuzzleX,0,null);

    	// comprobem si esta o no resolt
    	Solved();
    }

    // Solved : comproba si esta o no resolt
    public void Solved(){

    	if (P.Resolt()&&!ResoltFlag){
    		ResoltFlag = true;

    		Chronometer C = (Chronometer)((LinearLayout)((FrameLayout)getParent()).getParent()).findViewById(R.id.time);

    		C.stop();

    		Toast.makeText(getContext(),"SOLVED! at " + C.getText(),Toast.LENGTH_SHORT).show();
    	}
    }

    // Listener del scale gesture detector
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

    	@Override public boolean onScale(ScaleGestureDetector detector) {

    		float 	cs = detector.getCurrentSpan(),
    				ps = detector.getPreviousSpan();

    		int delta=0;

    		if (cs!=ps) delta=cs<ps?-1:1; // zoom positiu o negatiu segons traç

    		// zoom sencill, no depen de la longitud del span, unicament del sentit
    		if (delta<0) {

    			if (cdY < P.R) cdY++;
    			if (cdX < P.C) cdX++;

    			if (idX + cdX>P.C) idX=P.C-cdX;
    			if (idY + cdY>P.R) idY=P.R-cdY;
    		}
    		else
    		if (delta>0) {

    			if (cdX>MIN_CELLES_DIBUIXADES) cdX--;
    			if (cdY>MIN_CELLES_DIBUIXADES) cdY--;
    		}

    		// calculem tamany de cella, creem bitmap i redibuixem
    		currentcellsize = (PuzzleY - textsizeY) / cdY;

    		drawPuzzle();
    		invalidate();
    		return true;
    	}
    }

    // Control onTouch

    private static final int INVALID_POINTER_ID = -1;

    private float mPosX;
    private float mPosY;

    private float mLastTouchX;
    private float mLastTouchY;

    private int mActivePointerId = INVALID_POINTER_ID;
    private long clicktime = 0;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

    	final int 	pointerIndex,
    				pointerId,
    				newPointerIndex;

    	// Comprobem si hi ha moviment de zoom
    	mScaleDetector.onTouchEvent(ev);

    	switch (ev.getAction() & MotionEvent.ACTION_MASK) {

    		// toc, determinem dades
    		case MotionEvent.ACTION_DOWN:

    			mPosX = 0;
    			mPosY = 0;
    			mLastTouchX = ev.getX();
    			mLastTouchY = ev.getY();

    			mActivePointerId = ev.getPointerId(0);
    			clicktime = System.currentTimeMillis();
    			break;

    		// move : movem el grid dintre dels limits
    		case MotionEvent.ACTION_MOVE:

				if (mActivePointerId == INVALID_POINTER_ID) break;

    			if (mScaleDetector.isInProgress()) break;

   				pointerIndex = ev.findPointerIndex(mActivePointerId);

				mPosX+=ev.getX(pointerIndex)-mLastTouchX;
				mPosY+=ev.getY(pointerIndex)-mLastTouchY;

   				mLastTouchX = ev.getX(pointerIndex);
   				mLastTouchY = ev.getY(pointerIndex);

   				if (mPosX > currentcellsize) {
   					mPosX = 0;
   					if (idX > 0) idX--;
   				}

   				if (mPosX < -currentcellsize) {
   					mPosX = 0;
   					if (idX + cdX< P.C) idX++;
   				}

   				if (mPosY > currentcellsize) {
   					mPosY = 0;
   					if (idY > 0) idY--;
   				}

   				if (mPosY < -currentcellsize) {
   					mPosY = 0;
   					if (idY + cdY < P.R) idY++;
   				}

   				drawThings();

   				break;

   			// up : pot ser un click, comprobem
    		case MotionEvent.ACTION_UP:

    		    if (mScaleDetector.isInProgress()) return false;

   				if (System.currentTimeMillis() - clicktime < 250)
   					ParseClick(ev.getX(),ev.getY());

    			clicktime = 0;
    			mActivePointerId = INVALID_POINTER_ID;
    			break;

    		// cancel
    		case MotionEvent.ACTION_CANCEL:
    			clicktime = 0;
    			mActivePointerId = INVALID_POINTER_ID;
    			break;

    		// final del pointer
    		case MotionEvent.ACTION_POINTER_UP:
    			clicktime = 0;
    			pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
    					>> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
    			pointerId = ev.getPointerId(pointerIndex);

    			if (pointerId == mActivePointerId) {

    				newPointerIndex = pointerIndex == 0 ? 1 : 0;
    				mLastTouchX = ev.getX(newPointerIndex);
    				mLastTouchY = ev.getY(newPointerIndex);
    				mActivePointerId = ev.getPointerId(newPointerIndex);
    			}
    			break;
    		}
    		return true;
    	}

    // ParseClick : apliquem click
    public void ParseClick(float x,float y){

    	x -= PuzzleX; // centrem x
    	if (x>0&&x<currentcellsize*cdX&&y>0&&y<currentcellsize*cdY){ // dintre limits

    		byte i,j;

    		x /= currentcellsize; // trobem la cella polsada
    		y /= currentcellsize;

    		i = (byte)(idX + x);
    		j = (byte)(idY + y);

    		// segons valor de boto eina...
            switch(Settings.getInt("CP",BS10Constants.BT_FILLCELL)){
                case BS10Constants.BT_FILLCELL : if (!P.aCI(i,j)){ // anem alternant valors...
               									switch(P.v(i,j)){
               										case K.A : P.reset(i,j);P.det(i,j,K.M); break;
               										case K.I : P.det(i,j,K.A); break;
               										default  : P.reset(i,j);
               									}
                   							  } break;
                // omplenem columna
                case BS10Constants.BT_FILLCOL : for (byte n=0;n<P.R;n++) if (P.i(i,n)) P.det(i,n,K.A); break;
                // omplenem fila
                case BS10Constants.BT_FILLROW : for (byte n=0;n<P.C;n++) if (P.i(n,j)) P.det(n,j,K.A); break;
            }

            // redibuixem
            drawThings();
    	}
    }
}
