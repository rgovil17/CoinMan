package com.raghavapp.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Random random;
	Texture background;
	Texture[] man;
	int manState = 0;
	int pause = 0;
	float gravity = 0.8f;
	float velocity = 0;
	int manY = 0;
	Rectangle manRectangle;
	int score = 0;
	BitmapFont font;
	int gameState = 0;
	Texture dizzy;


	ArrayList<Integer> coinXs = new ArrayList<Integer>();
    ArrayList<Integer> coinYs = new ArrayList<Integer>();
    ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();
    Texture coin;
    int coinCount;

	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();
	Texture bomb;
	int bombCount;

	@Override
	public void create () {
		batch = new SpriteBatch();
		random = new Random();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
        manY = Gdx.graphics.getHeight()/2;

        coin = new Texture("coin.png");
        bomb = new Texture("bomb.png");

        dizzy = new Texture("dizzy-1.png");

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);
	}

	public void makeCoin() {
	    float height = random.nextFloat() * Gdx.graphics.getHeight();
	    coinYs.add((int) height);
	    coinXs.add(Gdx.graphics.getWidth());
    }

	public void makeBomb() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int) height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1) {		// GAME IS LIVE
			if (coinCount < 100) {
				coinCount++;
			} else {
				coinCount = 0;
				makeCoin();
			}

			if (bombCount < 200) {
				bombCount++;
			} else {
				bombCount = 0;
				makeBomb();
			}

			coinRectangles.clear();
			for (int i=0; i<coinXs.size(); i++) {
				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				coinXs.set(i, coinXs.get(i) - 7);
				coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
			}
			bombRectangles.clear();
			for (int i=0; i<bombXs.size(); i++) {
				batch.draw(bomb, bombXs.get(i), bombYs.get(i));
				bombXs.set(i, bombXs.get(i) - 10);
				bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
			}

			if (Gdx.input.justTouched()) {
				velocity = -20;
			}

			if (manY > Gdx.graphics.getHeight())
				manY = Gdx.graphics.getHeight();

			if (pause < 8) {
				pause++;
			} else {
				pause = 0;
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}

			velocity += gravity;
			manY -= velocity;
			if (manY < 0)
				manY = 0;
		}
		else if (gameState == 0) {	// GAME IS WAITING TO START
			if (Gdx.input.justTouched())
				gameState = 1;
		} else {	//2  // GAME OVER
			if (Gdx.input.justTouched()) {
				gameState = 1;
				manY = Gdx.graphics.getHeight()/2;
				score = 0;
				velocity = 0;
				coinXs.clear();
				coinYs.clear();
				coinRectangles.clear();
				coinCount = 0;
				bombXs.clear();
				bombYs.clear();
				bombRectangles.clear();
				bombCount = 0;
			}
		}

		if (gameState == 2) {
			batch.draw(dizzy, Gdx.graphics.getWidth()/2 - man[manState].getWidth(), manY, man[manState].getWidth()/1.5f, man[manState].getHeight()/1.5f);
		} else {
			batch.draw(man[manState], Gdx.graphics.getWidth()/2 - man[manState].getWidth(), manY, man[manState].getWidth()/1.5f, man[manState].getHeight()/1.5f);
		}

		manRectangle = new Rectangle(Gdx.graphics.getWidth()/2 - man[manState].getWidth(), manY, man[manState].getWidth()/1.5f, man[manState].getHeight()/1.5f);

		for (int i=0; i<coinXs.size(); i++) {
			if (Intersector.overlaps(manRectangle, coinRectangles.get(i))) {
				score++;
				coinXs.remove(i);
				coinYs.remove(i);
				coinRectangles.remove(i);
				break;
			}
		}
		for (int i=0; i<bombXs.size(); i++) {
			if (Intersector.overlaps(manRectangle, bombRectangles.get(i))) {
				gameState = 2;
			}
		}

		font.draw(batch, String.valueOf(score), 100, 200);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
