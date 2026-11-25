/*
* File: Breakout.java
* Authors: Yelyzaveta Melnykova and Lisa Martyniuk
* */
import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * Breakout
 *
 * 
 * 
 */
public class Breakout extends GraphicsProgram {
	public static void main(String[] args) {
		new Breakout().start(args);
	}
	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 80;
	private static final int PADDLE_HEIGHT = 20;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 50;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 8;

	/** Number of rows of bricks level 1 (each next level gets one more row) */
	private static final int NBRICK_ROWS = 4;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 20;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 20;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/** Number of turns */
	private static final int NTURNS = 3;


	// КАРТИНКИ
	/** Фон (400x600) - для всіх екранів */
	private static final String IMG_BACKGROUND = "waterbackground.png";

	/** Перлина (м'яч) */
	private static final String IMG_BALL = "ball.png";

	/** Вибух (з'являється, коли ядро влучило в міну) */
	private static final String IMG_EXPLOSION = "explosion.png";

	/** Серце в куточку екрана (життя) */
	private static final String IMG_HEART = "heart.png";

	/** Картинки завантажуються з файлів ОДИН РАЗ і потім використовуються всюди */
	private static Image imgBackground;
	private static Image imgBall;
	private static Image imgHeart;
	private static Image imgExplosion;

	// ЗВУКИ
	/** Влучили в бульбашку (і в міну) */
	private static final String SND_POP = "src/sounds/pop.wav";

	/** Рівень пройдено / не пройдено */
	private static final String SND_WIN = "src/sounds/win.wav";
	private static final String SND_LOSE = "src/sounds/lose.wav";

	/** Фонова музика (грає по колу) */
	private static final String SND_MUSIC = "src/sounds/music.wav";

	
	// ІНШІ КОНСТАНТИ
	/** Назва гри на початковому екрані */
	private static final String GAME_TITLE = "BUBBLE REEF";

	/** Пауза між кадрами (мс) */
	private static final int DELAY = 10;

	/** Максимум сердець */
	private static final int MAX_LIVES = 5;

	/** Розмір серця в куточку */
	private static final int HEART_SIZE = 24;

	/** Базова швидкість перлини по вертикалі (на рівні 2 і 3 трохи більша) */
	private static final double BALL_SPEED = 4.0;

	/** Максимальна горизонтальна швидкість після відбиття від мушлі */
	private static final double MAX_VX = 4.0;

	/** Швидкість опускання бульбашок на 3 рівні (пікселів за кадр) */
	private static final double WALL_FALL_SPEED = 0.05;

	/** Шанс, що з бульбашки, яка лопнула, випаде бонус (рівні 2 і 3) */
	private static final double BONUS_CHANCE = 0.15;

	/** Шанс, що замість двох бульбашок з'явиться міна (рівень 3) */
	private static final double MINE_CHANCE = 0.08;

	/** Шанси міцних бульбашок: рівень 2 - на 2 влучання, рівень 3 - на 3 і на 2 */
	private static final double TOUGH_CHANCE_LEVEL2 = 0.40;
	private static final double HP3_CHANCE_LEVEL3 = 0.25;
	private static final double HP2_CHANCE_LEVEL3 = 0.35;

	/** Розміри та швидкість бонусу */
	private static final int BONUS_W = 40;
	private static final int BONUS_H = 24;
	private static final double BONUS_SPEED = 2.0;

	/** Скільки кадрів діє бонус (1000 кадрів * 10 мс = 10 с) */
	private static final int BONUS_DURATION = 1000;

	/** У скільки разів повільнішими стають перлини від бонусу "течія" */
	private static final double SLOW_FACTOR = 0.6;

	/** Розмір вибуху */
	private static final int EXPLOSION_SIZE = 90;

	/** Розміри великих кнопок меню та маленької кнопки "Меню" у грі */
	private static final int BTN_W = 240;
	private static final int BTN_H = 50;
	private static final int GAME_BTN_W = 80;
	private static final int GAME_BTN_H = 30;

	/** Екрани гри */
	private static final int SCREEN_START = 0;
	private static final int SCREEN_LEVELS = 1;
	private static final int SCREEN_RULES = 2;
	private static final int SCREEN_PLAY = 3;
	private static final int SCREEN_RESULT = 4;

	/** Кольори тексту */
	private static final Color GOLD = new Color(255, 225, 90);
	private static final Color NAVY = new Color(0, 45, 100);

	/** Кольори намальованих елементів: мушля (ракетка) і кнопки */
	private static final Color PADDLE_COLOR = new Color(240, 120, 150);
	private static final Color PADDLE_BORDER = new Color(150, 40, 85);
	private static final Color BTN_COLOR = new Color(20, 130, 160);
	private static final Color BTN_LOCKED_COLOR = new Color(90, 110, 125);

	/** Кольори бульбашок: чим більше влучань витримує - тим темніша */
	private static final Color BUBBLE_COLOR_1 = new Color(130, 205, 255);
	private static final Color BUBBLE_COLOR_2 = new Color(50, 140, 230);
	private static final Color BUBBLE_COLOR_3 = new Color(10, 65, 150);

	/** Колір морської міни */
	private static final Color MINE_COLOR = new Color(200, 30, 30);

	//Стан гри
	private RandomGenerator rand = RandomGenerator.getInstance();

	private int screen = SCREEN_START;
	private int level = 1;
	private int maxUnlocked = 1; //найвищий відкритий рівень

	private int lives;
	private int score;
	private int frame = 0; //лічильник кадрів

	private GRect paddle;
	private double paddleW = PADDLE_WIDTH;
	private int widenFrames = 0; //скільки ще кадрів діє широка мушля
	private int slowFrames = 0;  //скільки ще кадрів діє повільна течія

	private Ball heldBall;       //перлина, яка лежить на мушлі до запуску
	private boolean waitingLaunch = false;
	private GLabel hintLabel;
	private GLabel hintShadow;

	private GLabel scoreLabel;
	private MenuButton btn1, btn2, btn3, btn4, btnGame;
	private int buttonCount = 0;
	private GImage background;
	private int bricksLeft;
	//Звуки
	private Clip soundPop;
	private Clip soundWin;
	private Clip soundLose;
	private Clip soundMusic;

	//Дані від миші
	private double mouseX = WIDTH / 2.0;
	private boolean clicked = false;
	private double clickX;
	private double clickY;

	// Власні класи об'єктів
	/** Перлина: має швидкість vx, vy */
	private class Ball extends GImage {
		double vx;
		double vy;
		int lastFrame = -1;

		Ball(double vx, double vy) {
			super(imgBall);
			setSize(2.0 * BALL_RADIUS, 2.0 * BALL_RADIUS);
			this.vx = vx;
			this.vy = vy;
		}
	}

	/** Бульбашка (hp - скільки влучань лишилось) або морська міна - намальований прямокутник */
	private class Brick extends GRect {
		int hp;
		boolean mine;

		Brick(double w, int hp, boolean mine) {
			super(w, BRICK_HEIGHT);
			this.hp = hp;
			this.mine = mine;
			setFilled(true);
			if (mine) {
				setFillColor(MINE_COLOR);
				setColor(Color.YELLOW);
			} else {
				setFillColor(bubbleColor(hp));
				setColor(Color.WHITE);
			}
		}
	}

	/** Бонус, що падає вниз
	 *  type: 0 - широка мушля, 1 - серце, 2 - три перлини, 3 - повільна течія */
	private class Bonus extends GCompound {
		int type;
		int lastFrame = -1;

		Bonus(int type) {
			this.type = type;
			GRect r = new GRect(BONUS_W, BONUS_H);
			r.setFilled(true);
			String text;
			if (type == 0) {
				r.setFillColor(new Color(240, 130, 160));
				text = "<->";
			} else if (type == 1) {
				r.setFillColor(new Color(220, 50, 80));
				text = "life";
			} else if (type == 2) {
				r.setFillColor(new Color(60, 170, 120));
				text = "x3";
			} else {
				r.setFillColor(new Color(70, 120, 220));
				text = "~~~";
			}
			r.setColor(Color.WHITE);
			add(r);
			GLabel l = new GLabel(text);
			l.setFont("SansSerif-bold-12");
			l.setColor(Color.WHITE);
			add(l, (BONUS_W - l.getWidth()) / 2, (BONUS_H + l.getAscent()) / 2 - 1);
		}
	}

	/** Серце життя в куточку екрана */
	private class Heart extends GImage {
		Heart() {
			super(imgHeart);
			setSize(HEART_SIZE, HEART_SIZE);
		}
	}

	/** Кнопка */
	private class MenuButton extends GCompound {
		String id;

		MenuButton(String text, String id, double w, double h, Color fill, int fontSize) {
			this.id = id;
			GRect r = new GRect(w, h);
			r.setFilled(true);
			r.setFillColor(fill);
			r.setColor(Color.WHITE);
			add(r);
			GLabel l = new GLabel(text);
			l.setFont("SansSerif-bold-" + fontSize);
			l.setColor(Color.WHITE);
			add(l, (w - l.getWidth()) / 2, (h + l.getAscent()) / 2 - 2);
		}
	}

	/** Який колір для бульбашки з hp влучаннями */
	private static Color bubbleColor(int hp) {
		if (hp >= 3) {
			return BUBBLE_COLOR_3;
		}
		if (hp == 2) {
			return BUBBLE_COLOR_2;
		}
		return BUBBLE_COLOR_1;
	}


	// Запуск
	public void init() {
		setSize(WIDTH, HEIGHT);
		addMouseListeners();
	}

	/* Method: run() */
	/** Runs the Breakout program. */
	public void run() {
		loadImages();
		loadSounds();
		startMusic();
		showStartScreen();
		while (true) {
			if (clicked) {
				clicked = false;
				handleClick(clickX, clickY);
			}
			if (screen == SCREEN_PLAY) {
				playFrame();
			}
			pause(DELAY);
		}
	}

	/** Завантажує кожну картинку один раз */
	private void loadImages() {
		imgBackground = loadImage(IMG_BACKGROUND);
		imgBall = loadImage(IMG_BALL);
		imgHeart = loadImage(IMG_HEART);
		imgExplosion = loadImage(IMG_EXPLOSION);
	}

	private Image loadImage(String path) {
		return new GImage(path).getImage();
	}

	// Звук 
	private void loadSounds() {
		soundPop = loadSound(SND_POP);
		soundWin = loadSound(SND_WIN);
		soundLose = loadSound(SND_LOSE);
		soundMusic = loadSound(SND_MUSIC);
	}

	private Clip loadSound(String path) {
		try {
			File f = new File(path);
			AudioInputStream in = AudioSystem.getAudioInputStream(f);
			Clip c = AudioSystem.getClip();
			c.open(in);
			return c;
		} catch (Exception e) {
			System.out.println("Не вдалося завантажити звук: " + path);
			return null;
		}
	}

	/** Програє звук з початку */
	private void playSound(Clip c) {
		if (c != null) {
			c.stop();
			c.setFramePosition(0);
			c.start();
		}
	}

	private void startMusic() {
		if (soundMusic != null) {
			soundMusic.setFramePosition(0);
			soundMusic.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}


	// Миша
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
	}

	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
	}

	public void mousePressed(MouseEvent e) {
		clickX = e.getX();
		clickY = e.getY();
		clicked = true;
	}

	/** Обробка кліку залежно від поточного екрана */
	private void handleClick(double x, double y) {
		GObject obj = getElementAt(x, y);
		if (obj != null) {
			if (obj == btn1) {
				doAction(btn1.id);
				return;
			} else if (obj == btn2) {
				doAction(btn2.id);
				return;
			} else if (obj == btn3) {
				doAction(btn3.id);
				return;
			} else if (obj == btn4) {
				doAction(btn4.id);
				return;
			} else if (obj == btnGame) {
				doAction(btnGame.id);
				return;
			}
		}
		if (screen == SCREEN_PLAY && waitingLaunch) {
			launchBall();
		}
	}

	private void doAction(String id) {
		if (id.equals("menu")) {
			showLevelSelect();
		} else if (id.equals("rules")) {
			showRules();
		} else if (id.equals("back")) {
			showStartScreen();
		} else if (id.equals("level1")) {
			startLevel(1);
		} else if (id.equals("level2")) {
			startLevel(2);
		} else if (id.equals("level3")) {
			startLevel(3);
		} else if (id.equals("retry")) {
			startLevel(level);
		} else if (id.equals("next")) {
			startLevel(level + 1);
		}
	}

	// Екрани меню
	/** Додає фон. Викликається одразу після removeAll() */
	private void addBackground() {
		background = new GImage(imgBackground);
		background.setSize(getWidth(), getHeight());
		add(background, 0, 0);
	}

	private void showStartScreen() {
		removeAll();
		buttonCount = 0;
		addBackground();
		screen = SCREEN_START;
		addCenteredLabel(GAME_TITLE, "SansSerif-bold-44", Color.WHITE, 180);
		addCenteredLabel("Лопай бульбашки в морі", "SansSerif-italic-20", GOLD, 220);
		addButton("Меню", "menu", 300, true);
		addButton("Правила", "rules", 370, true);
		clicked = false;
	}

	private void showLevelSelect() {
		removeAll();
		buttonCount = 0;
		addBackground();
		screen = SCREEN_LEVELS;
		addCenteredLabel("Вибір рівня", "SansSerif-bold-32", Color.WHITE, 120);
		addButton("Рівень 1", "level1", 190, true);
		if (maxUnlocked >= 2) {
			addButton("Рівень 2", "level2", 260, true);
		} else {
			addButton("Рівень 2 (закрито)", "level2", 260, false);
		}
		if (maxUnlocked >= 3) {
			addButton("Рівень 3", "level3", 330, true);
		} else {
			addButton("Рівень 3 (закрито)", "level3", 330, false);
		}
		addButton("Назад", "back", 420, true);
		clicked = false;
	}

	private void showRules() {
		removeAll();
		buttonCount = 0;
		addBackground();
		screen = SCREEN_RULES;
		addCenteredLabel("Правила гри", "SansSerif-bold-32", Color.WHITE, 70);
		Color c = Color.WHITE;
		String f = "SansSerif-bold-14";
		addCenteredLabel("Лопай блакитні бульбашки перлиною!", f, c, 115);
		addCenteredLabel("Рухай мушлю мишею й відбивай перлину.", f, c, 139);
		addCenteredLabel("Усі бульбашки лопнули - рівень пройдено.", f, c, 163);
		addCenteredLabel("Перлина впала - втрачаєш серце.", f, c, 187);
		addCenteredLabel("Усі серця втрачено - рівень не пройдено.", f, c, 211);
		addCenteredLabel("Клікни мишею, щоб запустити перлину.", f, c, 235);
		addCenteredLabel("Рівні 2 і 3: міцніші бульбашки", f, GOLD, 270);
		addCenteredLabel("(лопаються з кількох влучань) і бонуси:", f, GOLD, 294);
		addCenteredLabel("широка мушля, додаткове серце,", f, c, 318);
		addCenteredLabel("три перлини, повільна течія.", f, c, 342);
		addCenteredLabel("Рівень 3: бульбашки опускаються до мушлі.", f, GOLD, 378);
		addCenteredLabel("Морські міни підривають сусідні бульбашки.", f, c, 402);
		addButton("Назад", "back", 480, true);
		clicked = false;
	}

	private void showResult(boolean win) {
		removeAll();
		buttonCount = 0;
		addBackground();
		screen = SCREEN_RESULT;
		if (win && level == 3) {
			addCenteredLabel("Всі рівні пройдено", "SansSerif-bold-30", new Color(140, 255, 160), 170);
		} else if (win) {
			addCenteredLabel("Пройдено", "SansSerif-bold-36", new Color(140, 255, 160), 170);
		} else {
			addCenteredLabel("Не пройдено", "SansSerif-bold-36", new Color(255, 130, 120), 170);
		}
		addCenteredLabel("Очки: " + score, "SansSerif-bold-20", Color.WHITE, 215);

		if (win && level < 3) {
			addCenteredLabel("Перейти на наступний рівень чи повторити?", "SansSerif-bold-14", Color.WHITE, 250);
			addButton("Меню", "menu", 290, true);
			addButton("Повторити", "retry", 355, true);
			addButton("Наступний рівень", "next", 420, true);
		} else if (win) {
			addButton("Меню", "menu", 290, true);
			addButton("Повторити", "retry", 355, true);
		} else {
			addCenteredLabel("Повторити або в меню?", "SansSerif-bold-14", Color.WHITE, 250);
			addButton("Меню", "menu", 290, true);
			addButton("Повторити", "retry", 355, true);
		}
		clicked = false;
	}

	/** Додає напис по центру вікна (з темною тінню, щоб було видно на фоні) */
	private GLabel addCenteredLabel(String text, String font, Color color, double y) {
		GLabel shadow = new GLabel(text);
		shadow.setFont(font);
		shadow.setColor(NAVY);
		shadow.setLocation((getWidth() - shadow.getWidth()) / 2 + 2, y + 2);
		add(shadow);

		GLabel l = new GLabel(text);
		l.setFont(font);
		l.setColor(color);
		l.setLocation((getWidth() - l.getWidth()) / 2, y);
		add(l);
		return l;
	}

	/** Додає кнопку по центру. Якщо enabled == false - сіра, і клік нічого не робить */
	private MenuButton addButton(String text, String id, double y, boolean enabled) {
		Color fill = BTN_COLOR;
		String realId = id;
		if (!enabled) {
			fill = BTN_LOCKED_COLOR;
			realId = "locked";
		}
		MenuButton b = new MenuButton(text, realId, BTN_W, BTN_H, fill, 18);
		add(b, (getWidth() - BTN_W) / 2.0, y);
		buttonCount++;
		if (buttonCount == 1) {
			btn1 = b;
		} else if (buttonCount == 2) {
			btn2 = b;
		} else if (buttonCount == 3) {
			btn3 = b;
		} else {
			btn4 = b;
		}
		return b;
	}

	// Початок рівня
	private void startLevel(int n) {
		level = n;
		removeAll();
		buttonCount = 0;
		addBackground();
		lives = NTURNS;
		score = 0;
		widenFrames = 0;
		slowFrames = 0;
		paddleW = PADDLE_WIDTH;

		buildWall();
		createPaddle();
		createHud();
		spawnHeldBall();

		screen = SCREEN_PLAY;
		clicked = false;
	}

	/** Будує бульбашки. Рядів: 4 / 5 / 6 залежно від рівня */
	private void buildWall() {
		bricksLeft = 0;
		int rows = NBRICK_ROWS + level - 1;
		double fullWidth = NBRICKS_PER_ROW * BRICK_WIDTH + (NBRICKS_PER_ROW - 1) * BRICK_SEP;
		double x0 = (getWidth() - fullWidth) / 2;

		for (int row = 0; row < rows; row++) {
			double y = BRICK_Y_OFFSET + row * (BRICK_HEIGHT + BRICK_SEP);
			int col = 0;
			while (col < NBRICKS_PER_ROW) {
				double x = x0 + col * (BRICK_WIDTH + BRICK_SEP);
				boolean canBeMine = level == 3 && col < NBRICKS_PER_ROW - 1;
				if (canBeMine && rand.nextBoolean(MINE_CHANCE)) {
					// міна займає місце двох бульбашок
					Brick m = new Brick(2 * BRICK_WIDTH + BRICK_SEP, 1, true);
					add(m, x, y);
					bricksLeft++;
					col += 2;
				} else {
					Brick b = new Brick(BRICK_WIDTH, randomHp(), false);
					add(b, x, y);
					bricksLeft++;
					col++;
				}
			}
		}
	}

	/** Скільки влучань витримає нова бульбашка */
	private int randomHp() {
		if (level == 1) {
			return 1;
		}
		double r = rand.nextDouble(0, 1);
		if (level == 2) {
			if (r < TOUGH_CHANCE_LEVEL2) {
				return 2;
			}
			return 1;
		}
		// рівень 3
		if (r < HP3_CHANCE_LEVEL3) {
			return 3;
		}
		if (r < HP3_CHANCE_LEVEL3 + HP2_CHANCE_LEVEL3) {
			return 2;
		}
		return 1;
	}

	private void createPaddle() {
		paddle = new GRect(paddleW, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setFillColor(PADDLE_COLOR);
		paddle.setColor(PADDLE_BORDER);
		add(paddle, (getWidth() - paddleW) / 2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
	}

	private void createHud() {
		scoreLabel = new GLabel("");
		scoreLabel.setFont("SansSerif-bold-16");
		scoreLabel.setColor(NAVY);
		add(scoreLabel, 10, 25);

		GLabel lvl = new GLabel("Рівень " + level);
		lvl.setFont("SansSerif-bold-16");
		lvl.setColor(NAVY);
		add(lvl, (getWidth() - lvl.getWidth()) / 2, 25);

		// кнопка "Меню" прямо з гри (зліва внизу)
		MenuButton mb = new MenuButton("Меню", "menu", GAME_BTN_W, GAME_BTN_H, BTN_COLOR, 14);
		add(mb, 5, getHeight() - GAME_BTN_H - 5);
		btnGame = mb;
		updateHud();
	}

	/** Оновлює очки та серця (серця - картинки в правому верхньому куті) */
	private void updateHud() {
		scoreLabel.setLabel("Очки: " + score);

		// прибираємо старі серця: дивимось, що лежить на кожному місці
		for (int i = 1; i <= MAX_LIVES; i++) {
			double x = getWidth() - 8 - i * (HEART_SIZE + 4);
			GObject o = getElementAt(x + HEART_SIZE / 2.0, 6 + HEART_SIZE / 2.0);
			if (o != null && o != background) {
				remove(o);
			}
		}
		// додаємо стільки сердець, скільки життів
		for (int i = 1; i <= lives; i++) {
			Heart h = new Heart();
			add(h, getWidth() - 8 - i * (HEART_SIZE + 4), 6);
		}
	}

	/** Кладе нову перлину на мушлю і чекає кліку */
	private void spawnHeldBall() {
		heldBall = new Ball(0, 0);
		add(heldBall);
		waitingLaunch = true;

		String text = "Клікни, щоб запустити перлину";
		hintShadow = new GLabel(text);
		hintShadow.setFont("SansSerif-bold-14");
		hintShadow.setColor(NAVY);
		hintShadow.setLocation((getWidth() - hintShadow.getWidth()) / 2 + 1, getHeight() / 2.0 + 1);
		add(hintShadow);

		hintLabel = new GLabel(text);
		hintLabel.setFont("SansSerif-bold-14");
		hintLabel.setColor(Color.WHITE);
		hintLabel.setLocation((getWidth() - hintLabel.getWidth()) / 2, getHeight() / 2.0);
		add(hintLabel);

		placeHeldBall();
	}

	private void placeHeldBall() {
		heldBall.setLocation(paddle.getX() + paddleW / 2 - BALL_RADIUS,
				paddle.getY() - 2 * BALL_RADIUS);
	}

	private void launchBall() {
		double vx = rand.nextDouble(1.0, 2.5);
		if (rand.nextBoolean()) {
			vx = -vx;
		}
		heldBall.vx = vx;
		heldBall.vy = -ballSpeed();
		waitingLaunch = false;
		remove(hintLabel);
		remove(hintShadow);
	}

	private double ballSpeed() {
		return BALL_SPEED + 0.5 * (level - 1);
	}


	// Один кадр гри
	private void playFrame() {
		frame++;
		movePaddle();

		if (waitingLaunch) {
			placeHeldBall();
		} else {
			Ball b = nextBall();
			while (b != null) {
				updateBall(b);
				b = nextBall();
			}
			if (countBalls() == 0) {
				loseLife();
				if (screen != SCREEN_PLAY) {
					return;
				}
			}
		}

		if (level == 3) {
			moveWall();
		}

		Bonus bn = nextBonus();
		while (bn != null) {
			updateBonus(bn);
			bn = nextBonus();
		}
		updateEffects();

		if (level == 3 && wallReachedPaddle()) {
			endLevel(false);
			return;
		}
		if (countBricks() == 0) {
			endLevel(true);
		}
	}

	private void movePaddle() {
		double x = mouseX - paddleW / 2;
		if (x < 0) {
			x = 0;
		}
		if (x > getWidth() - paddleW) {
			x = getWidth() - paddleW;
		}
		paddle.setLocation(x, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
	}

	private void updateBall(Ball b) {
		double f = 1.0;
		if (slowFrames > 0) {
			f = SLOW_FACTOR;
		}
		b.move(b.vx * f, b.vy * f);
		double d = 2.0 * BALL_RADIUS;

		// стіни
		if (b.getX() <= 0) {
			b.setLocation(0, b.getY());
			b.vx = Math.abs(b.vx);
		}
		if (b.getX() + d >= getWidth()) {
			b.setLocation(getWidth() - d, b.getY());
			b.vx = -Math.abs(b.vx);
		}
		if (b.getY() <= 0) {
			b.setLocation(b.getX(), 0);
			b.vy = Math.abs(b.vy);
		}

		// мушля
		double cx = b.getX() + BALL_RADIUS;
		double reach = BALL_RADIUS * 0.2;
		if (b.vy > 0 && b.getY() + d -6 >= paddle.getY() && b.getY() <= paddle.getY() + PADDLE_HEIGHT && cx + reach >= paddle.getX() && cx - reach <= paddle.getX() + paddleW) {
			b.setLocation(b.getX(), paddle.getY() - d);
			double rel = (b.getX() + BALL_RADIUS - (paddle.getX() + paddleW / 2)) / (paddleW / 2);
			b.vx = rel * MAX_VX;
			if (Math.abs(b.vx) < 0.5) {
				if (b.vx < 0) {
					b.vx = -0.5;
				} else {
					b.vx = 0.5;
				}
			}
			b.vy = -Math.abs(b.vy);
		}

		// бульбашки
		Brick hit = findHitBrick(b);
		if (hit != null) {
			double ox = Math.min(b.getX() + b.getWidth(), hit.getX() + hit.getWidth())
					- Math.max(b.getX(), hit.getX());
			double oy = Math.min(b.getY() + b.getHeight(), hit.getY() + hit.getHeight())
					- Math.max(b.getY(), hit.getY());
			double ballCx = b.getX() + b.getWidth() / 2;
			double ballCy = b.getY() + b.getHeight() / 2;
			double brickCx = hit.getX() + hit.getWidth() / 2;
			double brickCy = hit.getY() + hit.getHeight() / 2;
			// відбиваємо і виштовхуємо перлину з бульбашки, щоб не вдарити двічі
			if (ox < oy) {
				b.vx = -b.vx;
				if (ballCx < brickCx) {
					b.setLocation(hit.getX() - d, b.getY());
				} else {
					b.setLocation(hit.getX() + hit.getWidth(), b.getY());
				}
			} else {
				b.vy = -b.vy;
				if (ballCy < brickCy) {
					b.setLocation(b.getX(), hit.getY() - d);
				} else {
					b.setLocation(b.getX(), hit.getY() + hit.getHeight());
				}
			}
			hitBrick(hit, b);
		}

		// впала вниз
		if (b.getY() > getHeight()) {
			remove(b);
		}
	}

	private void loseLife() {
		lives--;
		updateHud();
		if (lives <= 0) {
			endLevel(false);
		} else {
			spawnHeldBall();
		}
	}

	private void endLevel(boolean win) {
		screen = SCREEN_RESULT;
		if (win && level == maxUnlocked && level < 3) {
			maxUnlocked++;
		}
		if (win) {
			playSound(soundWin);
		} else {
			playSound(soundLose);
		}
		pause(800);
		showResult(win);
	}


	// Бульбашки та міни
	private Brick findHitBrick(Ball b) {
		double d = 2.0 * BALL_RADIUS;
		double left = b.getX() - 1;
		double right = b.getX() + d + 1;
		double top = b.getY() - 1;
		double bottom = b.getY() + d + 1;
		double midX = b.getX() + d / 2;
		double midY = b.getY() + d / 2;

		Brick r = brickAt(left, top);
		if (r == null) {
			r = brickAt(right, top);
		}
		if (r == null) {
			r = brickAt(left, bottom);
		}
		if (r == null) {
			r = brickAt(right, bottom);
		}
		if (r == null) {
			r = brickAt(midX, top);
		}
		if (r == null) {
			r = brickAt(midX, bottom);
		}
		if (r == null) {
			r = brickAt(left, midY);
		}
		if (r == null) {
			r = brickAt(right, midY);
		}
		return r;
	}

	/** Повертає цеглинку в точці або null, якщо там нічого немає чи це не цеглинка */
	private Brick brickAt(double x, double y) {
		GObject o = getElementAt(x, y);
		if (isBrick(o)) {
			return (Brick) o;
		}
		return null;
	}

	/** Перлина влучила в бульбашку або міну */
	private void hitBrick(Brick br, Ball b) {
		playSound(soundPop);
		if (br.mine) {
			explode(br, b);
			return;
		}
		score += 10;
		if (br.hp > 1) {
			// бульбашка міцна: замінюємо її "слабшою" (з меншим hp і іншою картинкою)
			Brick weaker = new Brick(br.getWidth(), br.hp - 1, false);
			add(weaker, br.getX(), br.getY());
			remove(br);
		} else {
			remove(br);
			bricksLeft--;
			if (level >= 2 && rand.nextBoolean(BONUS_CHANCE)) {
				dropBonus(br);
			}
		}
		updateHud();
	}

	/** Вибух міни: знищує її і сусідні бульбашки (незалежно від міцності) */
	private void explode(Brick mine, Ball ball) {
		double cx = mine.getX() + mine.getWidth() / 2;
		double cy = mine.getY() + mine.getHeight() / 2;

		// ховаємо перлину, щоб вона не заважала getElementAt
		double bx = ball.getX();
		double by = ball.getY();
		remove(ball);

		// дивимось 4 колонки і 3 ряди навколо міни
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				double px = cx - 75 + 50 * i;
				double py = cy + (j - 1) * (BRICK_HEIGHT + BRICK_SEP);
				GObject o = getElementAt(px, py);
				if (isBrick(o)) {
					remove(o);
					bricksLeft--;
					score += 10;
				}
			}
		}

		add(ball, bx, by);

		GImage flash = new GImage(imgExplosion);
		flash.setSize(EXPLOSION_SIZE, EXPLOSION_SIZE);
		add(flash, cx - EXPLOSION_SIZE / 2.0, cy - EXPLOSION_SIZE / 2.0);

		updateHud();
		pause(80);
		remove(flash);
	}

	/** Чи це цеглинка (за розміром): перевіряємо висоту і ширину */
	private boolean isBrick(GObject o) {
		if (o == null) {
			return false;
		}
		if (o.getHeight() != BRICK_HEIGHT) {
			return false;
		}
		return o.getWidth() == BRICK_WIDTH || o.getWidth() == 2 * BRICK_WIDTH + BRICK_SEP;
	}

	private int countBricks() {
		return bricksLeft;
	}

	/** Рівень 3: усі бульбашки повільно їдуть вниз */
	private void moveWall() {
		for (int i = 0; i < getElementCount(); i++) {
			GObject o = getElement(i);
			if (isBrick(o)) {
				o.move(0, WALL_FALL_SPEED);
			}
		}
	}

	private boolean wallReachedPaddle() {
		for (int i = 0; i < getElementCount(); i++) {
			GObject o = getElement(i);
			if (isBrick(o) && o.getY() + o.getHeight() >= paddle.getY()) {
				return true;
			}
		}
		return false;
	}


	// Пошук перлин
	private Ball nextBall() {
		for (int i = 0; i < getElementCount(); i++) {
			GObject o = getElement(i);
			if (isBall(o)) {
				Ball b = (Ball) o;
				if (b.lastFrame != frame) {
					b.lastFrame = frame;
					return b;
				}
			}
		}
		return null;
	}

	private boolean isBall(GObject o) {
		if (o == null) {
			return false;
		}
		return o.getWidth() == 2 * BALL_RADIUS && o.getHeight() == 2 * BALL_RADIUS;
	}

	private int countBalls() {
		int n = 0;
		for (int i = 0; i < getElementCount(); i++) {
			if (isBall(getElement(i))) {
				n++;
			}
		}
		return n;
	}


	private Ball firstBall() {
		for (int i = 0; i < getElementCount(); i++) {
			GObject o = getElement(i);
			if (isBall(o)) {
				return (Ball) o;
			}
		}
		return null;
	}


	// Бонуси
	private void dropBonus(Brick from) {
		Bonus bn = new Bonus(rand.nextInt(0, 3));
		add(bn, from.getX() + from.getWidth() / 2 - BONUS_W / 2.0, from.getY());
	}

	private Bonus nextBonus() {
		for (int i = 0; i < getElementCount(); i++) {
			GObject o = getElement(i);
			if (o instanceof Bonus) {
				Bonus bn = (Bonus) o;
				if (bn.lastFrame != frame) {
					bn.lastFrame = frame;
					return bn;
				}
			}
		}
		return null;
	}

	private void updateBonus(Bonus bn) {
		bn.move(0, BONUS_SPEED);
		if (bn.getBounds().intersects(paddle.getBounds())) {
			applyBonus(bn.type);
			remove(bn);
		} else if (bn.getY() > getHeight()) {
			remove(bn);
		}
	}

	private void applyBonus(int type) {
		score += 25;
		if (type == 0) {
			widenFrames = BONUS_DURATION;
			setPaddleWidth(PADDLE_WIDTH * 1.7);
		} else if (type == 1) {
			if (lives < MAX_LIVES) {
				lives++;
			}
		} else if (type == 2) {
			multiBall();
		} else {
			slowFrames = BONUS_DURATION;
		}
		updateHud();
	}

	private void setPaddleWidth(double w) {
		paddleW = w;
		paddle.setSize(w, PADDLE_HEIGHT);
	}

	/** До однієї перлини додає ще дві */
	private void multiBall() {
		if (waitingLaunch) {
			return;
		}
		Ball src = firstBall();
		if (src == null) {
			return;
		}
		Ball b1 = new Ball(src.vx + 1.5, src.vy);
		Ball b2 = new Ball(src.vx - 1.5, src.vy);
		add(b1, src.getX(), src.getY());
		add(b2, src.getX(), src.getY());
	}

	/** Зменшує таймери бонусів */
	private void updateEffects() {
		if (widenFrames > 0) {
			widenFrames--;
			if (widenFrames == 0) {
				setPaddleWidth(PADDLE_WIDTH);
			}
		}
		if (slowFrames > 0) {
			slowFrames--;
		}
	}
}