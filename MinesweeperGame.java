package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 13;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score = 0;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(5) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
                setCellValue(x, y, "");
                gameField[y][x].isOpen = false;
            }
        }
        countFlags = countMinesOnField;
        countMineNeighbors();


    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors() {
        List<GameObject> list = new ArrayList<>();
        for ( int x = 0; x < SIDE; x++){
            for ( int y = 0; y < SIDE; y++){
                if (!gameField[y][x].isMine){
                    list = getNeighbors(gameField[y][x]);
                    for( int i = 0; i < list.size(); i++){
                        if (list.get(i).isMine){
                            gameField[y][x].countMineNeighbors++;
                        }
                    }
                }
            }
        }
    }

    private void openTile(int x, int y) {
        if (isGameStopped)
            return;
        if (gameField[y][x].isFlag)
            return;
        if (gameField[y][x].isOpen)
            return;

        gameField[y][x].isOpen = true;
        countClosedTiles--;

        setCellColor(x, y, Color.GREEN);
        if (countClosedTiles == countMinesOnField && !gameField[y][x].isMine)
            win();
        if (gameField[y][x].isMine) {
            setCellValueEx(x, y, Color.RED, MINE);
            gameOver();
        }
        else {
            score +=5;
            setScore(score);
            if (gameField[y][x].countMineNeighbors != 0)
                setCellNumber(x, y, gameField[y][x].countMineNeighbors);
            else
            {
                setCellValue(x, y, "");
                List<GameObject> list1 = getNeighbors(gameField[y][x]);
                for (GameObject gameObject : list1) {
                    if (gameObject.isOpen == false)
                        openTile(gameObject.x, gameObject.y);
                }
            }
        }
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        super.onMouseLeftClick(x, y);
        if (isGameStopped)
            restart();
        else
            openTile(x, y);
    }

    private void markTile(int x, int y) {
        if (isGameStopped)
            return;
        if (gameField[y][x].isOpen)
            return;
        if (countFlags == 0 && gameField[y][x].isFlag == false)
            return;
        if (gameField[y][x].isFlag == true)
        {
            gameField[y][x].isFlag = false;
            countFlags++;
            setCellValue(x, y, "");
            setCellColor(x, y, Color.ORANGE);
        }
        else {
            gameField[y][x].isFlag = true;
            countFlags--;
            setCellValue(x, y, FLAG);
            setCellColor(x, y, Color.YELLOW);
        }
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        super.onMouseRightClick(x, y);
        markTile(x, y);
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.GREY, "Game Over!", Color.BLACK, 30);
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.AZURE, "You WIN!", Color.AQUA, 30);
    }

    private void restart() {
        setScore(0);
        score = 0;
        countClosedTiles = SIDE * SIDE;
        countMinesOnField = 0;
        isGameStopped = false;

        createGame();
    }
}