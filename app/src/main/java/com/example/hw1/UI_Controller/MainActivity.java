package com.example.hw1.UI_Controller;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hw1.Logic.GameManager;
import com.example.hw1.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    private final int NEW_TERRORIST_LINE = 0;
    private final int NUM_ROWS = 5;
    private final int NUM_COLS = 3;
    private final int DELAY = 1000;
    private final int DROP_RATE = 3;
    private final int LEFT = 0;
    private final int RIGHT = 2;
    private final int IS_TOON = -1;
    private final int COLLISION_LINE = NUM_ROWS-1;
    private final int INIT_PREVIOUS_COL = -1;


    private boolean isIconNeedChange=false;
    private Random rnd = new Random();
    private int toonLocation = 1;
    private Timer gameTimer;
    private int dropRateRunner = 0;
    private int previousObstacleColumn = INIT_PREVIOUS_COL;
    private int newLocation;

    private ImageButton main_BTN_right;
    private ImageButton main_BTN_left;
    private ShapeableImageView[][] main_IMG_cells = new ShapeableImageView[NUM_ROWS][NUM_COLS];
    private ShapeableImageView[] main_IMG_toon = new ShapeableImageView[NUM_COLS];
    private ShapeableImageView[] main_IMG_hearts;

    private GameManager gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        gameManager = new GameManager(main_IMG_hearts.length);

        loadImages();
        handleButtons();
        startGame();
    }



    private void handleButtons() {
        main_BTN_left.setOnClickListener(v -> {
            if (toonLocation > LEFT) {
                toonLocation--;
                updateToonLocation();
            }
        });
        main_BTN_right.setOnClickListener(v -> {
            if (toonLocation < RIGHT) {
                toonLocation++;
                updateToonLocation();
            }
        });
    }

    private void updateToonLocation() {
        for (int i = 0; i < NUM_COLS; i++)
            if (i == toonLocation)
                main_IMG_toon[i].setVisibility(View.VISIBLE);
            else
                main_IMG_toon[i].setVisibility(View.INVISIBLE);
    }

    private void startGame() {
        gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> updateUI());
            }
        }, 0, DELAY);

    }

    private void updateUI() {
        isIconNeedChange = false;
        changeToonIconToHit();
        moveObstacles();
        if(dropRateRunner%DROP_RATE == 0)
        {
            do {
                newLocation = gameManager.getRandomCol(NUM_COLS);
            }
            while(newLocation == previousObstacleColumn);
            generateTerrorist(NEW_TERRORIST_LINE, newLocation);
            previousObstacleColumn = newLocation;
        }
        dropRateRunner++;
    }

    private void checkForHit() {
        for (int j = 0; j < NUM_COLS; j++) {
            if(main_IMG_cells[COLLISION_LINE][j].getVisibility() == View.VISIBLE && j == toonLocation) // hit
                hitDrill();
            else if(main_IMG_cells[COLLISION_LINE][j].getVisibility() == View.VISIBLE && j != toonLocation) // miss
                missDrill();

        }
    }
    private void generateTerrorist(int row, int col)
    {
        main_IMG_cells[row][col].setVisibility(View.VISIBLE);
    }

    private void missDrill() {
        vibrate();
        if(!gameManager.isEndlessMode())
        {
            main_IMG_hearts[gameManager.getMisses()].setVisibility(View.INVISIBLE);
            gameManager.incrementMisses();
        }
    }

    private void hitDrill() {
        isIconNeedChange = true;
        changeToonIconToHit();
        Toast.makeText(this, "BOOM", Toast.LENGTH_SHORT).show();
    }

    private void moveObstacles()
    {
        for (int col = 0; col < NUM_COLS; col++)
            for (int row = 0; row < NUM_ROWS; row++)
                if (main_IMG_cells[row][col].getVisibility() == View.VISIBLE) // gM
                {
                    if(row < NUM_ROWS - 1) // no potential collision in the next line
                        main_IMG_cells[row+1][col].setVisibility(View.VISIBLE);
                    else
                        if (col == toonLocation) // hit
                            hitDrill();
                        else // miss
                            missDrill();
                    main_IMG_cells[row][col].setVisibility(View.INVISIBLE);
                    break;
                }
    }

    private void changeToonIconToHit() {
        if (isIconNeedChange) { // gM
            for (int i = 0; i < NUM_COLS; i++) {
                Glide.
                        with(this).
                        load(R.drawable.hit).
                        fitCenter().
                        placeholder(R.drawable.temp_for_hit).
                        into(main_IMG_toon[i]);
            }
        }
        else
        {
            loadImagesIntoToon();
        }

    }
    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
        else
            v.vibrate(500);
    }


    private void findViews() {
        // find each obstacle and toon's id:
        for (int i = 0; i < NUM_ROWS+1; i++)
            for (int j = 0; j < NUM_COLS; j++)
                if(i<NUM_ROWS)
                    main_IMG_cells[i][j] = findViewById(getActualId(i,j));
                else
                    main_IMG_toon[j] = findViewById(getActualId(IS_TOON,j));

        main_IMG_hearts=new ShapeableImageView[]{
                findViewById(R.id.main_IMG_heart1),
                findViewById(R.id.main_IMG_heart2),
                findViewById(R.id.main_IMG_heart3)
        };
        main_BTN_right = findViewById(R.id.main_BTN_right);
        main_BTN_left = findViewById(R.id.main_BTN_left);
    }
    private void loadImages(){
        loadImagesIntoObstacles();
        loadImagesIntoToon();
    }

    private void loadImagesIntoToon() {
        for (int i = 0; i < NUM_COLS; i++) {
            Glide.
                    with(this).
                    load(R.drawable.rocket).
                    fitCenter().
                    placeholder(R.drawable.temp_for_rocket).
                    into(main_IMG_toon[i]);
        }
    }

    private void loadImagesIntoObstacles() {
        for (int i = 0; i < NUM_ROWS; i++)
            for (int j = 0; j < NUM_COLS; j++) {
                Glide.
                        with(this).
                        load(R.drawable.sinwar).
                        fitCenter().
                        placeholder(R.drawable.temp_for_obstacles).
                        into(main_IMG_cells[i][j]);
            }
    }

    private int getActualId(int row, int col) {
        String cellId;
        if(row != IS_TOON)
            cellId = String.format("main_IMG_cell%d%d", row, col);
        else
            cellId = String.format("main_IMG_toonCell%d", col);
        return getResources().getIdentifier(cellId,"id", getPackageName());
    }

    private int randomCol(){
        return rnd.nextInt(NUM_COLS);
    }
}