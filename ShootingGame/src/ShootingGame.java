
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class ShootingGame extends JFrame {
	// 게임 실행 시 플레이어, 적의 움직임을 그릴 떄 깜빡임을 없애기 위햐ㅐ 더블 버퍼링 사용(지속적으로 실행)
	private Image bufferImage;
	private Graphics screenGraphic;

	private Image mainScreen = new ImageIcon("src/images/main_screen.png").getImage();
	private Image loadingScreen = new ImageIcon("src/images/loading_screen.png").getImage();
	private Image gameScreen = new ImageIcon("src/images/game_screen.png").getImage();

	private boolean isMainScreen, isLoadingScreen, isGameScreen; // boolean변수들로 화면을 트롤

	public static Game game = new Game();

	private Audio backgroundMusic;

	public ShootingGame() {
		setTitle("Shooting game"); // 게임 제목을 말 그대로 설정
		setSize(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		setResizable(false); // 사용자가 임의로 게임창 크기 조절 불가하게 함
		setLocationRelativeTo(null); // 게임창 중앙에 위치
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 게임 종료시 프로그램도 종료
		setVisible(true); // 게임창 정상적으로 화면에 위치
		setLayout(null); // 컴포넌트의 절대 위치와 크기 설정
		init();

	}
	// 객체를 선언하고 값을 '최초'로 할당하는 것//init 이라는 이름은 보통 초기화 의 의미를 지닌 함수나 객체를 작성할 때 많이 사용
	// init사용이유: 클래스 영역에서 선언할 경우에는 컴파일러가 자동으로 값 할당을 해주나 메소드 영역에서 선언할 경우에는 자동으로 값이
	// 할당되지 않기 때문에

	private void init() { // mainScreen만 true로 초기화

		isMainScreen = true;
		isLoadingScreen = false;
		isGameScreen = false;

		// 메뉴 배경음악을 Audio 클래스로 만들고 재생해줌
		backgroundMusic = new Audio("src/audio/menuBGM.wav", true);
		backgroundMusic.start();

		addKeyListener(new KeyListener());

	}

	// 로딩, 게임화면을 넘어가기 위한 gmaeStart 메소드를 작성
	private void gameStart() {
		isMainScreen = false;
		isLoadingScreen = true;
		// 타이머로 등록을 하여 일정 시간과 주기성을 부여하여 실행을 예약할 수 있다.

		Timer loadingTimer = new Timer();
		TimerTask loadingTask = new TimerTask() {
			/*
			 * run()은 호출하는 것을 생성된 쓰레드를 실행시키는 것이 아니라 단순히 클래스에 속한 메서드를 호출하는 것입니다. start()는 새로운
			 * 쓰레드가 작업을 실행하는데 필요한 호출스택을 생성한 다음 run을 호출해서 생성된 호출스택에 run()이 첫 번째로 저장되게 합니다.
			 */
			@Override
			public void run() { // run()메소드는 어렵게 생각하지 말고 단순히 클래스에 오버라이딩 된 메소드를 호출해서 사용하는 것으로 생각하면 된다.
				//게임 화면으로 넘어갈 때는 재생 중인 파일을 중단시켜줌 
				backgroundMusic.stop();
				isLoadingScreen = false;
				isGameScreen = true;
				// 게임이 시작되고 적 출현을 하기 위하여 loadingTimer.schedule(loadingTask, 3000); 전에 메소드 선언
				game.start();
			}
			// Timer, TimerTask를 이용하여 로딩화면에서 3초후 게임화면으로 넘어가게 설정
		}; // TimerTask
		loadingTimer.schedule(loadingTask, 3000); // 1000=1초

	}

	public void paint(Graphics g) { // paint()는 컴포넌트에 그림을 그리기 위한 것
		bufferImage = createImage(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		screenGraphic = bufferImage.getGraphics();
		screenDraw(screenGraphic);
		g.drawImage(bufferImage, 0, 0, null);

	}

	public void screenDraw(Graphics g) { // 메소드에서는 필요한 요소를 그려줄 것이며 페인트메소드에서 버퍼 이미지를 만들고 이를 화면에 뿌려 깜빡임을 최소하한다.
// 각 화면 변수가 true일 뗴 다흔 화면을 그려주기 위한 if문들
		if (isMainScreen) {
			g.drawImage(mainScreen, 0, 0, null);
		}

		if (isLoadingScreen) {
			g.drawImage(loadingScreen, 0, 0, null);
		}

		if (isGameScreen) {
			g.drawImage(gameScreen, 0, 0, null);
			game.gameDraw(g);
		}

		this.repaint();

	}

	class KeyListener extends KeyAdapter {

		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			// w,s,a,d를 누를 때 각각 game의 up, down, left, right를 true로 만듫어줍니다.
			case KeyEvent.VK_W:
				game.setUp(true);
				break;
			case KeyEvent.VK_S:
				game.setDown(true);
				break;
			case KeyEvent.VK_A:
				game.setLeft(true);
				break;
			case KeyEvent.VK_D:
				game.setRight(true);
				break;
			case KeyEvent.VK_R: // getter를 이용해 isOver상태가 true일 경우에만 이벤트 실행  
                if (game.isOver()) game.reset();
                break;
			case KeyEvent.VK_SPACE:
				game.setShooting(true);
				break;
			case KeyEvent.VK_ENTER:
				if (isMainScreen)
					gameStart();
				break;

			// esc를 누를시 게임 종료 //어뎁터 사용시 정해진 인터페이스 양식을 모두 정의할 필요 x
			case KeyEvent.VK_ESCAPE:
				System.exit(0);
				break;
			}
		}

		public void keyReleased(KeyEvent e) {
			switch (e.getKeyCode()) {
			// w,s,a,d를 때었을 때 각각 game의 up, down, left, right를 true로 만듫어줍니다.
			case KeyEvent.VK_W:
				game.setUp(false);
				break;
			case KeyEvent.VK_S:
				game.setDown(false);
				break;
			case KeyEvent.VK_A:
				game.setLeft(false);
				break;
			case KeyEvent.VK_D:
				game.setRight(false);
				break;
			case KeyEvent.VK_SPACE:
				game.setShooting(false);
				break;

			}
		}
	}

}
