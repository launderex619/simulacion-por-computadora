package com.example.simulaciones.Fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Point extends Fragment {

    private float scaledProportionX, scaledProportionY;
    private ArrayList<android.graphics.Point> pointList, pointListRelative;
    private ImageView pArea;
    private View view;
    private Spinner spnPointList;
    private TextView txtXRelative, txtYRelative, txtXAbsolute, txtYAbsolute;
    private Button btnLoad, btnSave, btnReset;
    private Bitmap world, worldMutable;
    private int[] worldPixels;


    public Point() {
        // Required empty public constructor
        worldPixels = new int[Constants.POINT_HEIGHT_AREA * Constants.POINT_WIDTH_AREA];
        pointList = new ArrayList<>();
        pointListRelative = new ArrayList<>();
        world = Bitmap.createBitmap(Constants.POINT_WIDTH_AREA, Constants.POINT_HEIGHT_AREA,
                Bitmap.Config.ARGB_8888);
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
        view = inflater.inflate(R.layout.fragment_point, container, false);
        ButtonActions buttonActions = new ButtonActions();
        spnPointList = view.findViewById(R.id.spinner_point_list);
        txtXRelative = view.findViewById(R.id.text_point_x_local);
        txtYRelative = view.findViewById(R.id.text_point_y_local);
        txtXAbsolute = view.findViewById(R.id.text_point_x_global);
        txtYAbsolute = view.findViewById(R.id.text_point_y_global);
        btnLoad = view.findViewById(R.id.btn_load);
        btnReset = view.findViewById(R.id.btn_reset);
        btnSave = view.findViewById(R.id.btn_save);

        btnLoad.setOnClickListener(buttonActions);
        btnReset.setOnClickListener(buttonActions);
        btnSave.setOnClickListener(buttonActions);

        pArea = view.findViewById(R.id.point_draw_space);
        pArea.setOnTouchListener(new AreaTouchActions());

        return view;
    }

    private void printPoints() {
        worldMutable = world.copy(Bitmap.Config.ARGB_8888, true);
        if (pointList.size() > 0) {
            for (android.graphics.Point p : pointList) {
                for (int i = -8; i < 8; i++) {
                    for (int j = -8; j < 8; j++) {
                        if (p.x + i > 0 && p.y + j > 0 && p.x + i < world.getWidth() &&
                                p.y + j < world.getHeight()) {
                            worldMutable.setPixel(p.x + i, p.y + j, Color.GREEN);
                        }
                    }
                }
            }
        }

    }

    private void setSpinner() {
        int i = 0;
        String[] pointListString;
        //necesito la lista como arreglo de strings
        pointListString = new String[pointList.size()];
        for (android.graphics.Point p : pointList) {
            pointListString[i] = p.toString();
            i++;
        }
        ArrayAdapter<String> spnAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                pointListString);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPointList.setAdapter(spnAdapter);
        spnPointList.setOnItemSelectedListener(new ColorPoint());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent d) {
        if (requestCode == Constants.REQUEST_FILE_EXTERNAL && resultCode == Activity.RESULT_OK) {
            Uri fileUri = d.getData();
            String points = "";
            String[] pointsXY;
            btnReset.performClick();
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(fileUri);
                int data = inputStream.read();
                while (data != -1) {
                    points += (char) data;
                    data = inputStream.read();
                }
                pointsXY = points.split("\n");
                //creacion de la lista de puntos absolutos
                for (String s : pointsXY) {
                    String[] temp = s.split(",");
                    pointList.add(new android.graphics.Point(Integer.parseInt(temp[0]),
                            Integer.parseInt(temp[1])));

                    //creacion de la lista de puntos relativos
                    if (pointListRelative.size() == 0) {
                        pointListRelative.add(pointListRelative.size(), new android.graphics.Point(
                                Integer.parseInt(temp[0]),
                                Integer.parseInt(temp[1])));
                    } else {
                        android.graphics.Point pR = pointList.get(pointList.size() - 2);
                        pointListRelative.add(pointListRelative.size(), new android.graphics.Point(
                                (Math.abs(pR.x) * -1) + Integer.parseInt(temp[0]),
                                (Math.abs(pR.y) * -1) + Integer.parseInt(temp[1])));
                    }
                }

                Toast.makeText(getContext(), "file loaded: " + fileUri.getPath(), Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            printPoints();
            pArea.setImageBitmap(worldMutable);
            setSpinner();
        }
    }

    private class ColorPoint implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int pointSelectedX, pointSelectedY;
            pointSelectedX = pointList.get(position).x;
            pointSelectedY = pointList.get(position).y;
            printPoints();
            for (int i = -8; i < 8; i++) {
                for (int j = -8; j < 8; j++) {
                    if (pointSelectedX + i > 0 && pointSelectedY + j > 0) {
                        worldMutable.setPixel(pointSelectedX + i, pointSelectedY + j, Color.RED);
                    }
                }
            }
            txtXAbsolute.setText("Absoluto X = " + pointList.get(position).x);
            txtYAbsolute.setText(", Y = " + pointList.get(position).y);
            txtXRelative.setText("Relativo X = " + pointListRelative.get(position).x);
            txtYRelative.setText(", Y = " + pointListRelative.get(position).y);
            pArea.setImageBitmap(worldMutable);
        }

        @Override

        public void onNothingSelected(AdapterView<?> parent) {

        }
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
                //agrego el punto a una lista, se agrega con valor global
                pointList.add(pointList.size(), new android.graphics.Point(localX, localY));
                if (pointListRelative.size() == 0) {
                    pointListRelative.add(pointListRelative.size(),
                            new android.graphics.Point(localX, localY));
                } else {
                    android.graphics.Point p = pointList.get(pointList.size() - 2);
                    pointListRelative.add(pointListRelative.size(), new android.graphics.Point(
                            (Math.abs(p.x) * -1) + localX,
                            (Math.abs(p.y) * -1) + localY));
                }
                setSpinner();
            }
            printPoints();
            return true;
        }
    }

    private class ButtonActions implements View.OnClickListener,
            ActivityCompat.OnRequestPermissionsResultCallback {

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                               @NonNull int[] grantResults) {
            // BEGIN_INCLUDE(onRequestPermissionsResult)
            switch (requestCode) {
                case Constants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL:
                    if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission has been granted. Start camera preview Activity.
                        Snackbar.make(view, R.string.write_permission_granted,
                                Snackbar.LENGTH_SHORT)
                                .show();
                        writeTxtFile();
                    } else {
                        // Permission request was denied.
                        Snackbar.make(view, R.string.write_permission_denied,
                                Snackbar.LENGTH_SHORT)
                                .show();
                    }
                    break;
                case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL:
                    if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission has been granted. Start camera preview Activity.
                        Snackbar.make(view, R.string.read_permission_granted,
                                Snackbar.LENGTH_SHORT)
                                .show();
                        loadTxtFile();
                    } else {
                        // Permission request was denied.
                        Snackbar.make(view, R.string.read_permission_denied,
                                Snackbar.LENGTH_SHORT)
                                .show();
                    }
                    break;
            }
        }


        private void loadTxtFile() {

            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
            startActivityForResult(chooseFile, Constants.REQUEST_FILE_EXTERNAL);

        }

        private void writeTxtFile() {
            if (pointList.size() == 0) {
                Toast.makeText(getContext(), getString(R.string.point_list_empty), Toast.LENGTH_SHORT).show();
            } else {
                File file = new File(Environment.getExternalStorageDirectory() + "/" +
                        File.separator + Constants.pointFileName);
                String points = "";
                for (android.graphics.Point p : pointList) {
                    points += p.x + "," + p.y + "\n";
                }
                try {
                    file.createNewFile();
                    if (file.exists()) {
                        OutputStream fo;
                        fo = new FileOutputStream(file);
                        fo.write(points.getBytes());
                        fo.close();
                    }
                    Toast.makeText(getContext(), "file created: " + file, Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onClick(View v) {
            if (v == btnLoad) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Toast.makeText(getContext(),
                                getString(R.string.read_permission),
                                Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL);
                    }
                } else {
                    // Permission has already been granted
                    loadTxtFile();
                }
            } else if (v == btnReset) {
                Toast.makeText(getContext(), "pressed", Toast.LENGTH_SHORT).show();
                pointList.clear();
                pointListRelative.clear();
                setSpinner();
                txtXAbsolute.setText("");
                txtXRelative.setText("");
                txtYAbsolute.setText("");
                txtYRelative.setText("");

                printPoints();
                pArea.setImageBitmap(world);
            } else if (v == btnSave) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Toast.makeText(getContext(),
                                getString(R.string.write_permission),
                                Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                Constants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);
                    }
                } else {
                    // Permission has already been granted
                    writeTxtFile();
                }
            }
        }
    }

}
