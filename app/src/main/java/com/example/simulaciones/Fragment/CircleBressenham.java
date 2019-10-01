package com.example.simulaciones.Fragment;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simulaciones.Helper.Constants;
import com.example.simulaciones.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CircleBressenham  extends Fragment {

    private float scaledProportionX, scaledProportionY;
    private ArrayList<android.graphics.Point> pointListInitial, pointListFinal;
    private ArrayList<Integer> radioList;
    private ImageView pArea;
    private View view;
    private Spinner spnEcuationList;
    private TextView txtX1, txtY1, txtX2, txtY2, txtR;
    private Button btnReset;
    private Bitmap world, worldMutable;
    private int[] worldPixels;
    private int touchCounter;

    public CircleBressenham() {
        // Required empty public constructor
        touchCounter = 0;
        worldPixels = new int[Constants.POINT_HEIGHT_AREA * Constants.POINT_WIDTH_AREA];
        pointListInitial = new ArrayList<>();
        pointListFinal = new ArrayList<>();
        radioList = new ArrayList<>();
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
        view = inflater.inflate(R.layout.fragment_circle_bressenham, container, false);
        spnEcuationList = view.findViewById(R.id.spinner_circle_list);
        txtX1 = view.findViewById(R.id.text_circle_x1);
        txtY1 = view.findViewById(R.id.text_circle_y1);
        txtX2 = view.findViewById(R.id.text_circle_x2);
        txtY2 = view.findViewById(R.id.text_circle_y2);
        txtR = view.findViewById(R.id.txt_r_circle);
        btnReset = view.findViewById(R.id.btn_reset);
        worldMutable = world.copy(Bitmap.Config.ARGB_8888, true);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "pressed", Toast.LENGTH_SHORT).show();
                pointListInitial.clear();
                pointListFinal.clear();
                radioList.clear();
                setSpinner();
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

        pArea = view.findViewById(R.id.iv_circle_draw_space);
        pArea.setOnTouchListener(new CircleBressenham.AreaTouchActions());

        return view;
    }

    private void setSpinner() {
        String[] pointListString;
        //necesito la lista como arreglo de strings
        pointListString = new String[pointListFinal.size()];
        for (int i = 0; i < pointListFinal.size(); i++) {
            int x, y, r;
            pointListString[i] = "r = ";
            y = ((pointListFinal.get(i).y + pointListInitial.get(i).y) / 2);
            x = ((pointListFinal.get(i).x + pointListInitial.get(i).x) / 2);
            r = radioList.get(i);
            pointListString[i] += x + "² + " + y + "² = " + r + "²";
        }
        ArrayAdapter<String> spnAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                pointListString);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnEcuationList.setAdapter(spnAdapter);
        spnEcuationList.setOnItemSelectedListener(new CircleBressenham.FunctionSelected());
    }

    private void printPoint(int x, int y, int color) {
        for (int i = -8; i < 8; i++) {
            for (int j = -8; j < 8; j++) {
                if (x + i > 0 && y + j > 0 && x + i < world.getWidth() && y + j < world.getHeight()) {
                    worldMutable.setPixel(x + i, y + j, color);
                }
            }
        }
    }

    private void drawMidpoint(android.graphics.Point pI, android.graphics.Point pF, int radio, int color) {
        //si son iguales no tiene caso hacer nada
        if (pI.x == pF.x && pI.y == pF.y) {
            return;
        }
        /*
        r² = (xf-xi)² + (yf - yi)²
        (yf - yi)² =  r² - (xf-xi)²
        yf - yi = sqrt(r² - (xf-xi)²)
        yf = yi + sqrt(r² - (xf-xi)²)
         */
        int pk, p0;
        int y = 1, x = 0;
        p0 = 1 - radio;
        pk = p0;
        y = radio;
        while (x < y) {
            drawOctants(pI.x, pI.y, x, y, radio, color);
            if(pk < 0){
                pk = pk +( 2 * x) + 3;
            }else {
                pk = pk + (2 * x) - (2*y) + 5;
                y--;
            }
            x++;
        }
    }

    private void drawOctants(int xc, int yc, int x, int y, int radio, int color) {
        int difX = x;
        int difY = y;

        try {
            worldMutable.setPixel(xc + difX, yc + difY, color);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            worldMutable.setPixel(xc - difX, yc + difY, color);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            worldMutable.setPixel(xc - difX, yc - difY, color);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            worldMutable.setPixel(xc + difX, yc - difY, color);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            worldMutable.setPixel(xc + difY, yc - difX, color);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            worldMutable.setPixel(xc + difY, yc + difX, color);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            worldMutable.setPixel(xc - difY, yc - difX, color);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            worldMutable.setPixel(xc - difY, yc + difX, color);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetCanvas() {
        worldMutable = world.copy(Bitmap.Config.ARGB_8888, true);
        for (int i = 0; i < pointListInitial.size(); i++) {
            drawMidpoint(pointListInitial.get(i), pointListFinal.get(i), radioList.get(i), Color.GREEN);
            printPoint(pointListInitial.get(i).x, pointListInitial.get(i).y, Color.BLACK);
        }
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
                if (touchCounter % 2 == 0) {
                    //agrego el punto inicial a la lista de iniciales
                    restoreCanvasToDefaultColors();
                    pointListInitial.add(pointListInitial.size(),
                            new android.graphics.Point(localX, localY));
                    printPoint(localX, localY, Color.BLACK);
                } else {
                    //agrego el punto final a la lista de finales
                    pointListFinal.add(pointListFinal.size(),
                            new Point(localX, localY));
                    int initialX = pointListInitial.get(pointListInitial.size() - 1).x;
                    int initialY = pointListInitial.get(pointListInitial.size() - 1).y;
                    radioList.add((int) Math.round(Math.sqrt(Math.pow(localX - initialX, 2d) + Math.pow(localY - initialY, 2d))));
                    resetCanvas();
                    setSpinner();
                }
                touchCounter++;
                pArea.setImageBitmap(worldMutable);
            }
            return true;
        }

        private void restoreCanvasToDefaultColors() {
        }
    }

    private class FunctionSelected implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int x1, x2, y1, y2, r;
            long startTime;
            long endTime;
            if (touchCounter % 2 == 0) {
                x1 = pointListInitial.get(position).x;
                x2 = pointListFinal.get(position).x;
                y1 = pointListInitial.get(position).y;
                y2 = pointListFinal.get(position).y;
                r = radioList.get(position);
                resetCanvas();
                startTime = System.nanoTime();
                drawMidpoint(pointListInitial.get(position), pointListFinal.get(position), radioList.get(position), Color.RED);
                printPoint(pointListInitial.get(position).x, pointListInitial.get(position).y, Color.BLACK);
                endTime = System.nanoTime();
                Snackbar.make(txtR, "Time for DDA: " + (endTime - startTime) + " nanoseconds"
                        , Snackbar.LENGTH_LONG)
                        .setAction("ok", null).show();
                txtX1.setText("X1 = " + x1);
                txtY1.setText(", Y1 = " + y1);
                txtX2.setText("X2 = " + x2);
                txtY2.setText(", Y2 = " + y2);
                txtR.setText("R = " + r);
                pArea.setImageBitmap(worldMutable);
            }
        }

        @Override

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

}
