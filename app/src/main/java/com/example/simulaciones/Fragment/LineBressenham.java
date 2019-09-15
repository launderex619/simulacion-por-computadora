package com.example.simulaciones.Fragment;

import android.annotation.SuppressLint;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
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

import java.time.Instant;
import java.util.ArrayList;

public class LineBressenham extends Fragment {

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

    public LineBressenham() {
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
        view = inflater.inflate(R.layout.fragment_line_bressenham, container, false);
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

    private void drawBressenham(Point pI, Point pF, int color) {
        //si son iguales no tiene caso hacer nada
        if (pI.x == pF.x && pI.y == pF.y) {
            return;
        }
        int dy, dx, p0, pk, length, x, y;
        double m;
        dy = (pF.y - pI.y);
        dx = (pF.x - pI.x);
        x = pI.x;
        y = pI.y;
        if (dx == 0) {
            m = 1000d;
        } else {
            m = (double) (dy) / (double) (dx);
        }
        if (m <= 1d && m > 0d) {
            length = Math.abs(dx);
            if (pI.x > pF.x) {
                dy = (pI.y - pF.y);
                dx = (pI.x - pF.x);
                x = pF.x;
                y = pF.y;
            }
            p0 = (2 * dy) - dx;
            pk = p0;
            while (length > 0) {
                worldMutable.setPixel(x, y, color);
                if (pk < 0) {
                    pk = pk + (2 * dy);
                    x++;
                } else {
                    pk = pk + (2 * dy) - (2 * dx);
                    x++;
                    y++;
                }
                length--;
            }
        }
        else if (m <= 0d && m > -1d) {
            length = Math.abs(dx);
            if (pI.x > pF.x) {
                dy = (pI.y - pF.y);
                dx = (pI.x - pF.x);
                x = pF.x;
                y = pF.y;
            }
            p0 = 2 * dy + dx;
            pk = p0;
            while (length > 0) {
                worldMutable.setPixel(x, y, color);
                if (pk < 0) {
                    pk = pk + (2 * dy) + (2 * dx);
                    x++;
                    y--;
                } else {
                    pk = pk + (2 * dy);
                    x++;
                }
                length--;
            }
        }
        else if (m > 1d) {
            length = Math.abs(dy);
            if (pI.y > pF.y) {
                dy = (pI.y - pF.y);
                dx = (pI.x - pF.x);
                x = pF.x;
                y = pF.y;
            }
            p0 =(2 * dx) - dy;
            pk = p0;
            while (length > 0) {
                worldMutable.setPixel(x, y, color);
                if (pk < 0) {
                    pk = pk + (2 * dx);
                    y++;
                } else {
                    pk = pk + (2 * dx) - (2 * dy);
                    x++;
                    y++;
                }
                length--;
            }
        }
        else if (m < -1d) {
            length = Math.abs(dy);
            if (pI.y > pF.y) {
                dy = (pI.y - pF.y);
                dx = (pI.x - pF.x);
                x = pF.x;
                y = pF.y;
            }
            p0 = dy - (2 * dx);
            pk = p0;
            while (length > 0) {
                worldMutable.setPixel(x, y, color);
                if (pk < 0) {
                    pk = pk - (2 * dx);
                    y++;
                } else {
                    pk = pk - (2 * dx) - (2 * dy);
                    x--;
                    y++;
                }
                length--;
            }
        }
    }

    private void drawBressenhamOptimized(Point pI, Point pF, int color) {
        //si son iguales no tiene caso hacer nada
        if (pI.x == pF.x && pI.y == pF.y) {
            return;
        }
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
            worldMutable.setPixel(x, y, color);// Como mínimo se dibujará siempre 1 píxel (punto).
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

    private void resetCanvas() {
        worldMutable = world.copy(Bitmap.Config.ARGB_8888, true);
        for (int i = 0; i < pointListInitial.size(); i++) {
            drawBressenham(pointListInitial.get(i), pointListFinal.get(i), Color.GREEN);
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
                drawBressenham(pointListInitial.get(position), pointListFinal.get(position), Color.RED);
                printPoint(pointListInitial.get(position).x, pointListInitial.get(position).y, Color.BLACK);
                printPoint(pointListFinal.get(position).x, pointListFinal.get(position).y, Color.BLACK);
                endTime = System.nanoTime();
                Toast.makeText(getContext(), "Time for Bressenham: " + (endTime - startTime) + " nanoseconds"
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
