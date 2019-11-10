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
public class ElipseDDAall extends Fragment {

    private float scaledProportionX, scaledProportionY;
    private ArrayList<android.graphics.Point> pointListInitial, pointListFinal;
    private ImageView pArea;
    private View view;
    private Spinner spnEcuationList;
    private TextView txtX1, txtY1, txtX2, txtY2, txtR;
    private Button btnReset;
    private Bitmap world, worldMutable;
    private int[] worldPixels;
    private int touchCounter;

    public ElipseDDAall() {
        // Required empty public constructor
        touchCounter = 0;
        worldPixels = new int[Constants.POINT_HEIGHT_AREA * Constants.POINT_WIDTH_AREA];
        pointListInitial = new ArrayList<>();
        pointListFinal = new ArrayList<>();
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
        view = inflater.inflate(R.layout.fragment_elipse_ddaall, container, false);
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
        pArea.setOnTouchListener(new ElipseDDAall.AreaTouchActions());

        return view;
    }

    private void setSpinner() {
        String[] pointListString;
        //necesito la lista como arreglo de strings
        pointListString = new String[pointListFinal.size()];
        for (int i = 0; i < pointListFinal.size(); i++) {
            int x, y, r;
            pointListString[i] = "Elipse: " + i;
        }
        ArrayAdapter<String> spnAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                pointListString);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnEcuationList.setAdapter(spnAdapter);
        spnEcuationList.setOnItemSelectedListener(new ElipseDDAall.FunctionSelected());
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

    private void drawDDA(android.graphics.Point pI, android.graphics.Point pF, int color) {
        //si son iguales no tiene caso hacer nada
        if (pI.x == pF.x && pI.y == pF.y) {
            return;
        }
        int initialx, initialy;
        int y = 0, x = 0;
        int rx = Math.abs(pF.x - pI.x);
        int ry = Math.abs(pI.y - pF.y);
        long rxs = rx * rx;
        long rys = ry * ry;
        x = -rx;
        initialx = pI.x;
        initialy = pI.y;

        while (x < rx) {
            long a = (rxs * rys);
            long b = (x * x * rys);
            long c = (a - b) / rxs;
            double d = Math.sqrt(c);
            y = (int) Math.round(d);
            if(y == 0){
                System.out.println(y);
            }
            try {
                worldMutable.setPixel(x + initialx, initialy + y, color);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                worldMutable.setPixel(x + initialx, initialy - y, color);
            } catch (Exception e) {
                e.printStackTrace();
            }
            x++;
        }
        y = -ry;
        while (y < ry) {
            double d = Math.sqrt(((rxs * rys) - (y * y * rxs)) / rys);
            x = (int) Math.round(d);
            try {
                worldMutable.setPixel(initialx + x, initialy + y, color);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                worldMutable.setPixel(initialx - x, initialy + y, color);
            } catch (Exception e) {
                e.printStackTrace();
            }
            y++;
        }
    }

    private void resetCanvas() {
        worldMutable = world.copy(Bitmap.Config.ARGB_8888, true);
        for (int i = 0; i < pointListInitial.size(); i++) {
            drawDDA(pointListInitial.get(i), pointListFinal.get(i), Color.GREEN);
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
                resetCanvas();
                startTime = System.nanoTime();
                drawDDA(pointListInitial.get(position), pointListFinal.get(position), Color.RED);
                printPoint(pointListInitial.get(position).x, pointListInitial.get(position).y, Color.BLACK);
                endTime = System.nanoTime();
                Snackbar.make(txtR, "Time for DDA: " + (endTime - startTime) + " nanoseconds"
                        , Snackbar.LENGTH_LONG)
                        .setAction("ok", null).show();
                txtX1.setText("X1 = " + x1);
                txtY1.setText(", Y1 = " + y1);
                txtX2.setText("X2 = " + x2);
                txtY2.setText(", Y2 = " + y2);
                txtR.setText("Rx = "+ Math.abs(x1 - x2) + ", Ry = " +Math.abs(y1 - y2));
                pArea.setImageBitmap(worldMutable);
            }
        }

        @Override

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

}
