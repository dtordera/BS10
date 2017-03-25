// Clase BS10Bitmaps : administra els bitmaps i crea bitmap fonsmar

package dvtr.games.logic.BS10;

import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class BS10Bitmaps {
	Bitmap 	O1,
			O2,
			E1,
			E2,
			H1,
			H2,
			M,
			B1,
			B2,
			N1,
			N2,
			S1,
			S2,
			V1,
			V2,
			FonsMar;

	private final Context ctx;
	private final Random rnd;

	// Constructor
	public BS10Bitmaps(Context context){

		ctx = context;
		rnd = new Random();

		loadBitmaps();
	}

	// Torna un bitmap de cada tipus, segons m
	public Bitmap O(int m){
		return m%2==0? O1:O2;
	}

	public Bitmap N(int m){
		return m%2==0? N1:N2;
	}

	public Bitmap S(int m){
		return m%2==0? S1:S2;
	}

	public Bitmap E(int m){
		return m%2==0? E1:E2;
	}

	public Bitmap H(int m){
		return m%2==0? H1:H2;
	}

	public Bitmap V(int m){
		return m%2==0? V1:V2;
	}

	public Bitmap B(int m){
		return m%2==0? B1:B2;
	}

	public Bitmap M(){
		return M;
	}

	// Carrega bitmaps segons preferencies
	public void loadBitmaps(){

		switch((ctx.getSharedPreferences(BS10Constants.prefName,0)).getInt("GS",BS10Constants.GS_CLASSIC)){
		case BS10Constants.GS_CLASSIC : loadClassic(); break;
		case BS10Constants.GS_WWII    : loadWWII(); break;
		default : loadNoPictures(); break;
		}
	}

	// WWII
	public void loadWWII(){
   		Resources Res = ctx.getResources();

   		E1 = BitmapFactory.decodeResource(Res,R.drawable.e1w);
   		E2 = BitmapFactory.decodeResource(Res,R.drawable.e2w);
   		O1 = BitmapFactory.decodeResource(Res,R.drawable.o1w);
   		O2 = BitmapFactory.decodeResource(Res,R.drawable.o2w);
   		N1 = BitmapFactory.decodeResource(Res,R.drawable.n1w);
   		N2 = BitmapFactory.decodeResource(Res,R.drawable.n2w);
   		S1 = BitmapFactory.decodeResource(Res,R.drawable.s1w);
   		S2 = BitmapFactory.decodeResource(Res,R.drawable.s2w);
   		H1 = BitmapFactory.decodeResource(Res,R.drawable.h1w);
   		H2 = BitmapFactory.decodeResource(Res,R.drawable.h2w);
   		V1 = BitmapFactory.decodeResource(Res,R.drawable.v1w);
   		V2 = BitmapFactory.decodeResource(Res,R.drawable.v2w);
   		M  = BitmapFactory.decodeResource(Res,R.drawable.mw);
   		B1 = BitmapFactory.decodeResource(Res,R.drawable.b1w);
   		B2 = BitmapFactory.decodeResource(Res,R.drawable.b2w);
	}

	// Classic
	public void loadClassic(){
   		Resources Res = ctx.getResources();

   		E1 = BitmapFactory.decodeResource(Res,R.drawable.e1c);
   		E2 = BitmapFactory.decodeResource(Res,R.drawable.e2c);
   		O1 = BitmapFactory.decodeResource(Res,R.drawable.o1c);
   		O2 = BitmapFactory.decodeResource(Res,R.drawable.o2c);
   		N1 = BitmapFactory.decodeResource(Res,R.drawable.n1c);
   		N2 = BitmapFactory.decodeResource(Res,R.drawable.n2c);
   		S1 = BitmapFactory.decodeResource(Res,R.drawable.s1c);
   		S2 = BitmapFactory.decodeResource(Res,R.drawable.s2c);
   		H1 = BitmapFactory.decodeResource(Res,R.drawable.h1c);
   		H2 = BitmapFactory.decodeResource(Res,R.drawable.h2c);
   		V1 = BitmapFactory.decodeResource(Res,R.drawable.v1c);
   		V2 = BitmapFactory.decodeResource(Res,R.drawable.v2c);
   		M  = BitmapFactory.decodeResource(Res,R.drawable.mc);
   		B1 = BitmapFactory.decodeResource(Res,R.drawable.b1c);
   		B2 = BitmapFactory.decodeResource(Res,R.drawable.b2c);
	}

	// No Pictures
	public void loadNoPictures(){
   		Resources Res = ctx.getResources();

   		E1 = E2 = BitmapFactory.decodeResource(Res,R.drawable.e1n);
   		O1 = O2 = BitmapFactory.decodeResource(Res,R.drawable.o1n);
   		N1 = N2 = BitmapFactory.decodeResource(Res,R.drawable.n1n);
   		S1 = S2 = BitmapFactory.decodeResource(Res,R.drawable.s1n);
   		H1 = H2 = V1 = V2 = BitmapFactory.decodeResource(Res,R.drawable.x1n);
   		M  = BitmapFactory.decodeResource(Res,R.drawable.mn);
   		B1 = B2 = BitmapFactory.decodeResource(Res,R.drawable.b1n);
	}

	// FerFonsMar : fa un bitmap w x w de fons
    public void ferFonsMar(int w){

    	System.gc();

    	Paint P = new Paint(); // Creem estil
    	P.setStrokeWidth(0);
    	P.setStyle(Style.STROKE);
    	P.setAntiAlias(true);
    	P.setDither(true);

    	FonsMar = Bitmap.createBitmap(w,w,Bitmap.Config.ARGB_8888);
    	Canvas canvas = new Canvas(FonsMar);

    	canvas.drawRGB(0,0,0xbb);

    	int x,y,i,k,m;
    	byte dx;
    	double rad =  Math.PI*rnd.nextFloat()/2 - Math.PI/4,
    		   xrad = Math.cos(rad),
    		   yrad = Math.sin(rad);

    	// Creem linies blaves aleatories
    	for (i=0;i<5000;i++)
    	{
    		x = rnd.nextInt(w);
    		dx = (byte)rnd.nextInt(50);
    		y = rnd.nextInt(w);

    		P.setARGB(0x80,0,0,rnd.nextInt(55)+200);
    		canvas.drawLine(x,y,(int)(x+dx*xrad),(int)(y+dx*yrad),P);
    	}

    	// Creem linies grises aleatories
    	for (i=0;i<2000;i++)
    	{
    		x = rnd.nextInt(w);
    		y = rnd.nextInt(w);
    		k = (byte)rnd.nextInt(50)+200;
    		m = rnd.nextInt(5)+4;

    		P.setARGB(k-180,k,k,k);
    		canvas.drawLine(x,y,(int)(x+xrad*m),(int)(y+yrad*m),P);
    	}
    }
}
