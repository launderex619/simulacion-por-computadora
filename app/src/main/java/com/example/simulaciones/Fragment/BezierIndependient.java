package com.example.simulaciones.Fragment;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simulaciones.Helper.Constants;
import com.example.simulaciones.R;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

/**
 * A simple {@link Fragment} subclass.
 */
public class BezierIndependient extends Fragment {


    private float scaledProportionX, scaledProportionY;
    private Points curves;
    private ImageView pArea;
    private View view;
    private Spinner spnEcuationList, spnInterpolations;
    private SeekBar skbK;
    private TextView txtX1, txtY1, txtX2, txtY2, txtR, txtK;
    private Button btnReset;
    private Bitmap world, worldMutable;
    private int[] worldPixels;
    private int touchCounter, kthAdvance, curveGrade, errorRange;
    private boolean firtsTouch = true;

    public BezierIndependient() {
        // Required empty public constructor
        errorRange = 1000;
        touchCounter = 0;
        kthAdvance = 1;
        worldPixels = new int[Constants.POINT_HEIGHT_AREA * Constants.POINT_WIDTH_AREA];
        world = Bitmap.createBitmap(Constants.POINT_WIDTH_AREA, Constants.POINT_HEIGHT_AREA, Bitmap.Config.ARGB_8888);
        int c = 0;
        for (int i = 0; i < Constants.POINT_HEIGHT_AREA; i++) {
            for (int j = 0; j < Constants.POINT_WIDTH_AREA; j++) {
                world.setPixel(j, i, Color.WHITE);
                if (j % 50 == 0) {
                    world.setPixel(j, i, Color.BLACK);
                }
                if (i % 50 == 0) {
                    world.setPixel(j, i, Color.BLACK);
                }
                worldPixels[c] = world.getPixel(j, i);
                c++;
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bezier_independient, container, false);
        spnEcuationList = view.findViewById(R.id.spinner_bezier_list);
        txtX1 = view.findViewById(R.id.text_bezier_x1);
        txtY1 = view.findViewById(R.id.text_bezier_y1);
        txtX2 = view.findViewById(R.id.text_bezier_x2);
        txtY2 = view.findViewById(R.id.text_bezier_y2);
        txtK = view.findViewById(R.id.bezier_k);
        txtR = view.findViewById(R.id.txt_r_bezier);
        btnReset = view.findViewById(R.id.btn_reset);
        spnInterpolations = view.findViewById(R.id.spinner_bezier_interpolations);
        skbK =  view.findViewById(R.id.bezier_seek_k);
        worldMutable = world.copy(Bitmap.Config.ARGB_8888, true);

        String[] curveOrder;
        //necesito la lista como arreglo de strings
        curveOrder = new String[3];
        for (int i = 0; i < 3; i++) {
            int x, y, r;
            curveOrder[i] = "Curva de orden: " + (i+2);
        }
        ArrayAdapter<String> spnAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                curveOrder);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnInterpolations.setAdapter(spnAdapter);
        spnInterpolations.setOnItemSelectedListener(new BezierIndependient.GradeCurves());
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "pressed", Toast.LENGTH_SHORT).show();
                txtX1.setText("");
                txtX2.setText("");
                txtY1.setText("");
                txtY2.setText("");
                txtR.setText("");
                touchCounter = 0;
                worldMutable = world.copy(Bitmap.Config.ARGB_8888, true);
                pArea.setImageBitmap(worldMutable);
            }
        });
        skbK.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                kthAdvance = (progress/2)+1;
                txtK.setText("K: " + ((progress/2)+1) + "/" + errorRange);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        pArea = view.findViewById(R.id.iv_bezier_draw_space);
        pArea.setOnTouchListener(new BezierIndependient.AreaTouchActions());

        return view;
    }

    private void printPoint(int x, int y, int color, Bitmap bitmap) {
        for (int i = -8; i < 8; i++) {
            for (int j = -8; j < 8; j++) {
                if (x + i > 0 && y + j > 0 && x + i < world.getWidth() && y + j < world.getHeight()) {
                    bitmap.setPixel(x + i, y + j, color);
                }
            }
        }
    }


    private void drawLine(Point pI, Point pF, int color, Bitmap bitmap) {

        int dy, dx, incYi, incXi, incXr, incYr, temp, x, y, avR, av, avI;
        dy = (pF.y - pI.y);
        dx = (pF.x - pI.x);
        //se analiza hacia que direccion en el ejex y ejey se va a avanzar
        //se hace de esta manera para no tener que calcular m = (y2,y1)/(x2,x1), ya que involucraria
        //decimales y es lo que estamos tratando de evitar
        if (dy >= 0) {
            incYi = 1;
        } else {
            dy = dy * -1;
            incYi = -1;
        }

        if (dx >= 0) {
            incXi = 1;
        } else {
            dx = dx * -1;
            incXi = -1;
        }
        // 2 - Incrementos para las secciones con avance recto:
        if (dx >= dy) {
            incYr = 0;
            incXr = incXi;
        } else {
            incXr = 0;
            incYr = incYi;
            // Cuando dy es mayor que dx, se intercambian, para procurar siempre iterar en x.
            temp = dx;
            dx = dy;
            dy = temp;
        }
        x = pI.x;
        y = pI.y;

        //valores de error
        avR = (2 * dy);
        av = (avR - dx);
        avI = (av - dx);

        // 4  - Bucle para el trazado de las línea.
        do {
            bitmap.setPixel(x, y, color);// Como mínimo se dibujará siempre 1 píxel (punto).
            //Mensaje(av + " ") // (debug) para ver los valores de error global que van apareciendo.
            if (av >= 0) {
                x = (x + incXi);     // X aumenta en inclinado.
                y = (y + incYi);     // Y aumenta en inclinado.
                av = (av + avI);     // Avance Inclinado
            } else {
                x = (x + incXr);    // X aumenta en recto.
                y = (y + incYr);     // Y aumenta en recto.
                av = (av + avR);     // Avance Recto
            }
        } while (x != pF.x);
    }


    private void startAnimation() {
        for (int i = 0; i < curves.getGrade(); i++){
            drawLine(curves.getPoint(i), curves.getPoint(i+1), 0xFF999999, worldMutable);
        }
        pArea.setImageBitmap(worldMutable);
        if(curves.getGrade() == 2)
            (new DrawerGrade2()).execute(curves.getLines());
        else if (curves.getGrade() == 3)
            (new DrawerGrade3()).execute(curves.getLines());
        else if (curves.getGrade() == 4)
            (new DrawerGrade4()).execute(curves.getLines());
    }


    private void updateBitmap(Bitmap bitmap) {
        pArea.setImageBitmap(bitmap);
    }
    private void resetCanvas() {
        worldMutable = world.copy(Bitmap.Config.ARGB_8888, true);
        pArea.setImageBitmap(worldMutable);
    }

    private class AreaTouchActions implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int localX, localY;
                //obtengo el ratio x, y
                scaledProportionX = Constants.POINT_WIDTH_AREA / (float) v.getWidth();
                scaledProportionY = Constants.POINT_HEIGHT_AREA / (float) v.getHeight();
                //obtengo el x, y dentro del view
                localX = Math.round(event.getX() * scaledProportionX);
                localY = Math.round(event.getY() * scaledProportionY);
                touchCounter++;
                if (firtsTouch){
                    curves = new Points(curveGrade);
                    firtsTouch = false;
                    resetCanvas();
                }
                curves.addPoint(new android.graphics.Point(localX, localY));
                printPoint(localX, localY, 0xFF444444, worldMutable);
                pArea.setImageBitmap(worldMutable);

                if (touchCounter % (curveGrade+1) == 0) {
                    firtsTouch = true;
                    startAnimation();
                    touchCounter = 0;
                }
            }
            return true;
        }
    }

    private class GradeCurves implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
           curveGrade = position + 2;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {  }
    }


    private class Points {
        private ArrayList<Point> pointList;
        private int curveGrade;
        public Points(int grade){
            curveGrade = grade;
            pointList = new ArrayList<>(5);
        }

        public void addPoint(Point p){
            pointList.add(p);
        }

        public int getGrade() {
            return curveGrade;
        }

        public Point getPoint(int index){
            return pointList.get(index);
        }

        public int getSize() {
            return pointList.size();
        }

        public ArrayList<Point> getLines() {
            return pointList;
        }
    }

    private class DrawerGrade2 extends AsyncTask<ArrayList<Point>, Bitmap, Void >{
        @Override
        protected Void doInBackground(ArrayList<Point>... arrayLists) {
            int x, y;
            ArrayList<Point> mainPoints = arrayLists[0];
            Point p1, p2, pc;
            for (float i = 0; i < errorRange; i+= kthAdvance){
                
                final Bitmap bitmapAnim = worldMutable.copy(Bitmap.Config.ARGB_8888, true);
                p1 = new Point((int)(((1-(i/errorRange)) * (float)mainPoints.get(0).x) + ((float)mainPoints.get(1).x *(i/errorRange))),
                        (int)(((1-(i/errorRange)) * (float)mainPoints.get(0).y) + ((float)mainPoints.get(1).y * (i/errorRange))));
                p2 = new Point((int)(((1-(i/errorRange)) * (float)mainPoints.get(1).x) + ((float)mainPoints.get(2).x *(i/errorRange))),
                        (int)(((1-(i/errorRange)) * (float)mainPoints.get(1).y) + ((float)mainPoints.get(2).y * (i/errorRange))));
                pc = new Point((int)(((1-(i/errorRange)) * (float)p1.x) + ((float)p2.x *(i/errorRange))),
                        (int)(((1-(i/errorRange)) * (float)p1.y) + ((float)p2.y * (i/errorRange))));

                printPoint(p1.x, p1.y, 0xffff0000,bitmapAnim);
                printPoint(p2.x, p2.y, 0xff00ff00,bitmapAnim);
                drawLine(p1, p2, 0xff000000, bitmapAnim);
                printPoint(pc.x, pc.y, 0xff000000,bitmapAnim);
                printPoint(pc.x, pc.y, 0xff0000ff,worldMutable);
                drawLine(p1,p2, 0x0000FF,bitmapAnim);
                publishProgress(bitmapAnim);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    // we were cancelled, stop sleeping!
                }
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Bitmap... values) {
            updateBitmap(values[0]);
        }
    }

    private class DrawerGrade3 extends AsyncTask<ArrayList<Point>, Bitmap, Void >{
        @Override
        protected Void doInBackground(ArrayList<Point>... arrayLists) {
            int x, y;
            ArrayList<Point> mainPoints = arrayLists[0];
            Point p1, p2, p3, p4, p5, pc;
            for (float i = 0; i < errorRange; i+= kthAdvance){

                final Bitmap bitmapAnim = worldMutable.copy(Bitmap.Config.ARGB_8888, true);
                p1 = new Point((int)(((1-(i/errorRange)) * (float)mainPoints.get(0).x) + ((float)mainPoints.get(1).x *(i/errorRange))),
                        (int)(((1-(i/errorRange)) * (float)mainPoints.get(0).y) + ((float)mainPoints.get(1).y * (i/errorRange))));
                p2 = new Point((int)(((1-(i/errorRange)) * (float)mainPoints.get(1).x) + ((float)mainPoints.get(2).x *(i/errorRange))),
                        (int)(((1-(i/errorRange)) * (float)mainPoints.get(1).y) + ((float)mainPoints.get(2).y * (i/errorRange))));
                p3 = new Point((int)(((1-(i/errorRange)) * (float)mainPoints.get(2).x) + ((float)mainPoints.get(3).x *(i/errorRange))),
                        (int)(((1-(i/errorRange)) * (float)mainPoints.get(2).y) + ((float)mainPoints.get(3).y * (i/errorRange))));
                p4 = new Point((int)(((1-(i/errorRange)) * (float)p1.x) + ((float)p2.x *(i/errorRange))),
                        (int)(((1-(i/errorRange)) * (float)p1.y) + ((float)p2.y * (i/errorRange))));
                p5 = new Point((int)(((1-(i/errorRange)) * (float)p2.x) + ((float)p3.x *(i/errorRange))),
                        (int)(((1-(i/errorRange)) * (float)p2.y) + ((float)p3.y * (i/errorRange))));
                pc = new Point((int)(((1-(i/errorRange)) * (float)p4.x) + ((float)p5.x *(i/errorRange))),
                        (int)(((1-(i/errorRange)) * (float)p4.y) + ((float)p5.y * (i/errorRange))));

                printPoint(p1.x, p1.y, 0xffff0000,bitmapAnim);
                printPoint(p2.x, p2.y, 0xffff0000,bitmapAnim);
                printPoint(p3.x, p3.y, 0xffff0000,bitmapAnim);
                printPoint(p4.x, p4.y, 0xff00ff00,bitmapAnim);
                printPoint(p5.x, p5.y, 0xff00ff00,bitmapAnim);
                drawLine(p1, p2, 0xff000000, bitmapAnim);
                drawLine(p2, p3, 0xff000000, bitmapAnim);
                drawLine(p4, p5, 0xff0000ff, bitmapAnim);
                printPoint(pc.x, pc.y, 0xff000000,bitmapAnim);
                printPoint(pc.x, pc.y, 0xff0000ff,worldMutable);
                publishProgress(bitmapAnim);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    // we were cancelled, stop sleeping!
                }
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Bitmap... values) {
            updateBitmap(values[0]);
        }
    }


    private class DrawerGrade4 extends AsyncTask<ArrayList<Point>, Bitmap, Void > {
        @Override
        protected Void doInBackground(ArrayList<Point>... arrayLists) {
            int x, y;
            ArrayList<Point> mainPoints = arrayLists[0];
            Point p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11,pc;
            for (float i = 0; i < errorRange; i += kthAdvance) {

                final Bitmap bitmapAnim = worldMutable.copy(Bitmap.Config.ARGB_8888, true);
                p1 = new Point((int) (((1 - (i / errorRange)) * (float) mainPoints.get(0).x) + ((float) mainPoints.get(1).x * (i / errorRange))),
                        (int) (((1 - (i / errorRange)) * (float) mainPoints.get(0).y) + ((float) mainPoints.get(1).y * (i / errorRange))));
                p2 = new Point((int) (((1 - (i / errorRange)) * (float) mainPoints.get(1).x) + ((float) mainPoints.get(2).x * (i / errorRange))),
                        (int) (((1 - (i / errorRange)) * (float) mainPoints.get(1).y) + ((float) mainPoints.get(2).y * (i / errorRange))));
                p3 = new Point((int) (((1 - (i / errorRange)) * (float) mainPoints.get(2).x) + ((float) mainPoints.get(3).x * (i / errorRange))),
                        (int) (((1 - (i / errorRange)) * (float) mainPoints.get(2).y) + ((float) mainPoints.get(3).y * (i / errorRange))));
                p4 = new Point((int) (((1 - (i / errorRange)) * (float) mainPoints.get(3).x) + ((float) mainPoints.get(4).x * (i / errorRange))),
                        (int) (((1 - (i / errorRange)) * (float) mainPoints.get(3).y) + ((float) mainPoints.get(4).y * (i / errorRange))));

                p5 = new Point((int) (((1 - (i / errorRange)) * (float) p1.x) + ((float) p2.x * (i / errorRange))),
                        (int) (((1 - (i / errorRange)) * (float) p1.y) + ((float) p2.y * (i / errorRange))));
                p6 = new Point((int) (((1 - (i / errorRange)) * (float) p2.x) + ((float) p3.x * (i / errorRange))),
                        (int) (((1 - (i / errorRange)) * (float) p2.y) + ((float) p3.y * (i / errorRange))));
                p7 = new Point((int) (((1 - (i / errorRange)) * (float) p3.x) + ((float) p4.x * (i / errorRange))),
                        (int) (((1 - (i / errorRange)) * (float) p3.y) + ((float) p4.y * (i / errorRange))));
                p8 = new Point((int) (((1 - (i / errorRange)) * (float) p5.x) + ((float) p6.x * (i / errorRange))),
                        (int) (((1 - (i / errorRange)) * (float) p5.y) + ((float) p6.y * (i / errorRange))));
                p9 = new Point((int) (((1 - (i / errorRange)) * (float) p6.x) + ((float) p7.x * (i / errorRange))),
                        (int) (((1 - (i / errorRange)) * (float) p6.y) + ((float) p7.y * (i / errorRange))));

                pc = new Point((int) (((1 - (i / errorRange)) * (float) p8.x) + ((float) p9.x * (i / errorRange))),
                        (int) (((1 - (i / errorRange)) * (float) p8.y) + ((float) p9.y * (i / errorRange))));

                printPoint(p1.x, p1.y, 0xffff0000, bitmapAnim);
                printPoint(p2.x, p2.y, 0xffff0000, bitmapAnim);
                printPoint(p3.x, p3.y, 0xffff0000, bitmapAnim);
                printPoint(p4.x, p4.y, 0xffff0000, bitmapAnim);
                printPoint(p5.x, p5.y, 0xff00ff00, bitmapAnim);
                printPoint(p6.x, p6.y, 0xff00ff00, bitmapAnim);
                printPoint(p7.x, p7.y, 0xff00ff00, bitmapAnim);
                printPoint(p8.x, p8.y, 0xff000ff0, bitmapAnim);
                printPoint(p9.x, p9.y, 0xff000ff0, bitmapAnim);
                drawLine(p1, p2, 0xffff0000, bitmapAnim);
                drawLine(p2, p3, 0xffff0000, bitmapAnim);
                drawLine(p3, p4, 0xffff0000, bitmapAnim);
                drawLine(p5, p6, 0xff00ff00, bitmapAnim);
                drawLine(p6, p7, 0xff00ff00, bitmapAnim);
                drawLine(p8, p9, 0xff0000ff, bitmapAnim);
                printPoint(pc.x, pc.y, 0xff000000, bitmapAnim);
                printPoint(pc.x, pc.y, 0xff0000ff, worldMutable);
                publishProgress(bitmapAnim);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    // we were cancelled, stop sleeping!
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            updateBitmap(values[0]);
        }
    }
}
