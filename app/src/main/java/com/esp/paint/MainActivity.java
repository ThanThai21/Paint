package com.esp.paint;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE = 1;
    private static final int PERMISSIONS_REQUEST = 1;
    private DrawView drawView;
    private ImageButton clearButton;
    private ImageButton undoButton;
    private ImageButton redoButton;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton colorFab;
    private FloatingActionButton imageFab;
    private FloatingActionButton saveFab;
    private FloatingActionButton sizeFab;

    private boolean hasImage = false;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestAppPermission();
        initView();
    }

    private void initView() {
        drawView = (DrawView) findViewById(R.id.draw_view);
        clearButton = (ImageButton) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(this);
        undoButton = (ImageButton) findViewById(R.id.undo_button);
        undoButton.setOnClickListener(this);
        redoButton = (ImageButton) findViewById(R.id.redo_button);
        redoButton.setOnClickListener(this);
        fabMenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        imageFab = (FloatingActionButton) findViewById(R.id.add_image);
        imageFab.setOnClickListener(this);
        colorFab = (FloatingActionButton) findViewById(R.id.color);
        colorFab.setOnClickListener(this);
        saveFab = (FloatingActionButton) findViewById(R.id.save);
        saveFab.setOnClickListener(this);
        sizeFab = (FloatingActionButton) findViewById(R.id.brush_size);
        sizeFab.setOnClickListener(this);
        sizeFab.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View v) {
        if (v == clearButton) {
            Toast.makeText(this, "Clear", Toast.LENGTH_SHORT).show();
            drawView.clear();
            if (fabMenu.isOpened()) {
                fabMenu.close(true);
            }
        } else if (v == undoButton) {
            drawView.undo();
            if (fabMenu.isOpened()) {
                fabMenu.close(true);
            }
        } else if (v == redoButton) {
            drawView.redo();
            if (fabMenu.isOpened()) {
                fabMenu.close(true);
            }
        } else if (v == imageFab) {
            if (!hasImage) {
                pickImage();
            } else {
                removeImage();
            }
            fabMenu.close(true);
        } else if (v == colorFab) {
            pickColor();
            fabMenu.close(true);
        } else if (v == saveFab) {
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            progressDialog.show();
            new SaveImage(this, drawView.getBitmap(), currentDateTimeString, progressDialog).execute();
        } else if (v == sizeFab) {
            //show dialog choose size
            chooseBrushSize();
        }
        if (fabMenu.isOpened()) {
            fabMenu.close(true);
        }
    }

    private void pickColor() {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(drawView.getColor())
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        drawView.setColor(selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();

    }

    private void chooseBrushSize() {
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void removeImage() {
        drawView.setImage(null);
        imageFab.setLabelText("Add image");
        hasImage = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (data == null) {
                return;
            }
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                drawView.setImage(bitmap);
                imageFab.setLabelText("Remove image");
                hasImage = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void requestAppPermission() {
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                }
                return;
            }
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
