package com.example.simulaciones.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
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

public class LineDDA extends Fragment {

    private float scaledProportionX, scaledProportionY;
    private ArrayList<Point> pointListInitial, pointListFinal;
    private ImageView pArea;
    private View view;
    private Spinner spnEcuationList;
    private TextView txtX1, txtY1, txtX2, txtY2, txtEcuation;
    private Button btnReset;
    private Bitmap world, worldMutable;
    private int[] worldPixels;
    private int touchCounter;

    public LineDDA() {
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
        view = inflater.inflate(R.layout.fragment_line_dda, container, false);
        spnEcuationList = view.findViewById(R.id.spinner_line_list);
        txtX1 = view.findViewById(R.id.text_line_x1);
        txtY1 = view.findViewById(R.id.text_line_y1);
        txtX2 = view.findViewById(R.id.text_line_x2);
        txtY2 = view.findViewById(R.id.text_line_y2);
        txtEcuation = view.findViewById(R.id.text_line_preview);
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
                touchCounter = 0;
                worldMutable = world.copy(Bitmap.Config.ARGB_8888, true);

                pArea.setImageBitmap(worldMutable);
            }
        });

        pArea = view.findViewById(R.id.point_draw_space);
        pArea.setOnTouchListener(new AreaTouchActions());

        return view;
    }

    private void setSpinner() {
        String[] pointListString;
        //necesito la lista como arreglo de strings
        pointListString = new String[pointListFinal.size()];
        for (int i = 0; i < pointListFinal.size(); i++) {
            float m, b;
            pointListString[i] = "y = ";
            m = (float) ((pointListFinal.get(i).y - pointListInitial.get(i).y)) /
                    (float) ((pointListFinal.get(i).x - pointListInitial.get(i).x));
            m = Math.round(m * 100) / 100f;

            b = pointListInitial.get(i).y - (m * pointListInitial.get(i).x);
            b = Math.round(b);
            pointListString[i] += m + "(x) + " + b;
        }
        ArrayAdapter<String> spnAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                pointListString);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnEcuationList.setAdapter(spnAdapter);
        spnEcuationList.setOnItemSelectedListener(new FunctionSelected());
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

    private void drawDDA(Point pI, Point pF, int color) {
        //si son iguales no tiene caso hacer nada
        if (pI.x == pF.x && pI.y == pF.y) {
            return;
        }
        int dx, dy, x, y;
        double m;
        dx = pF.x - pI.x;
        dy = pF.y - pI.y;
        if (dx == 0) {
            m = 1000d;
        } else {
            m = (double) (dy) / (double) (dx);
        }
        x = pI.x;
        y = pI.y;

        if (m <= 1 && m > -1) {
            if (x > pF.x) {
                while (x > pF.x) {
                    y = (int) Math.floor((m * (x - pI.x) + pI.y));
                    worldMutable.setPixel(x, y, color);
                    x--;
                }
            } else {
                while (x < pF.x) {
                    y = (int) Math.floor((m * (x - pI.x) + pI.y));
                    worldMutable.setPixel(x, y, color);
                    x++;
                }
            }
        } else {
            if (y > pF.y) {
                while (y > pF.y) {
                    x = (int) Math.floor((y - pI.y) / m + pI.x);
                    worldMutable.setPixel(x, y, color);
                    y--;
                }
            } else {
                while (y < pF.y) {
                    x = (int) Math.floor((y - pI.y) / m + pI.x);
                    worldMutable.setPixel(x, y, color);
                    y++;
                }
            }
        }
    }

    private void drawDDAOptimized(Point pI, Point pF, int color) {
        //si son iguales no tiene caso hacer nada
        if (pI.x == pF.x && pI.y == pF.y) {
            return;
        }
        int dx, dy, length;
        float incX, incY, x, y;
        dx = pF.x - pI.x;
        dy = pF.y - pI.y;
        if (Math.abs(dx) >= Math.abs(dy)) {
            length = dx;
        } else {
            length = dy;
        }
        incX = (float) dx / length;
        incY = (float) dy / length;

        x = (float) pI.x;
        y = (float) pI.y;

        if (length < 0) {
            for (int i = 0; i > length; i--) {
                worldMutable.setPixel((int) x, (int) y, color);
                x -= incX;
                y -= incY;
            }
        } else {
            for (int i = 0; i < length; i++) {
                worldMutable.setPixel((int) x, (int) y, color);
                x += incX;
                y += incY;
            }
        }
    }

    private void resetCanvas() {
        worldMutable = world.copy(Bitmap.Config.ARGB_8888, true);
        for (int i = 0; i < pointListInitial.size(); i++) {
            drawDDA(pointListInitial.get(i), pointListFinal.get(i), Color.GREEN);
            //drawDDAOptimized(pointListInitial.get(i), pointListFinal.get(i), Color.GREEN);
            printPoint(pointListInitial.get(i).x, pointListInitial.get(i).y, Color.BLACK);
            printPoint(pointListFinal.get(i).x, pointListFinal.get(i).y, Color.BLACK);
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
                            new Point(localX, localY));
                } else {
                    //agrego el punto final a la lista de finales
                    pointListFinal.add(pointListFinal.size(),
                            new Point(localX, localY));
                    resetCanvas();

                }
                touchCounter++;
                printPoint(localX, localY, Color.BLACK);
                setSpinner();
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
            int x1, x2, y1, y2;
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
                //drawDDAOptimized(pointListInitial.get(position), pointListFinal.get(position), Color.RED);
                printPoint(pointListInitial.get(position).x, pointListInitial.get(position).y, Color.BLACK);
                printPoint(pointListFinal.get(position).x, pointListFinal.get(position).y, Color.BLACK);
                endTime = System.nanoTime();
                Toast.makeText(getContext(), "Time for DDA: " + (endTime - startTime) + " nanoseconds"
                        , Toast.LENGTH_LONG).show();

                txtX1.setText("X1 = " + x1);
                txtY1.setText(", Y1 = " + y1);
                txtX2.setText("X2 = " + x2);
                txtY2.setText(", Y2 = " + y2);
                txtEcuation.setText(parent.getSelectedItem().toString());
                pArea.setImageBitmap(worldMutable);
            }
        }

        @Override

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

}
