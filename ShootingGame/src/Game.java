
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Game extends Thread {
	private int delay = 20;
	private long pretime;
	private int cnt;// 게임의 딜레이마다 증가할 카운트 변수
	private int score;// 점수를 나타낼 변수

	private Image player = new ImageIcon("src/images/player.png").getImage();

	private int playerX, playerY;
	private int playerWidth = player.getWidth(null);
	private int playerHeight = player.getHeight(null);
	private int playerSpeed = 10;// 키입력 시 플레이어가 이동할 거리 변수
	private int playerHp = 30; // 플레이어 체력

	private boolean up, down, left, right, shooting;// 플레이어의 움직임을 제어할 변수
	private boolean isOver;// 게임 오버 여부를 나타내는 변수
	// 플레이어의 공격을 담을 ArrayList 생성
	private ArrayList<PlayerAttack> playerAttackList = new ArrayList<PlayerAttack>();
	// Enemy, EnemtAttack을 담을 ArrayList
	private ArrayList<Enemy> enemyList = new ArrayList<Enemy>();
	private ArrayList<EnemyAttack> enemyAttackList = new ArrayList<EnemyAttack>();
	// ArrayList안의 내용에 쉽게 접근하기 위해 playerAttack, enemy, enemyAttack 변수 선언
	private PlayerAttack playerAttack;
	private Enemy enemy;
	private EnemyAttack enemyAttack;

	private Audio backgroundMusic;
	private Audio hitSound;

	// run메소드는 이쓰레드를 시작할 시 실행될 내용
	@Override
	public void run() {
		// 게임 배경음악, 피격 효과음 경로 입력
		backgroundMusic = new Audio("src/audio/gameBGM.wav", true);
		hitSound = new Audio("src/audio/hitSound.wav", false);
		// 게임 배경음만 먼저 넣어주고
		backgroundMusic.start();
//reset 메소드에 대신하여 변수들을 선언해주고 while문이 시작할 때 reset메소드가 실행 될 수 있도록 선언 
		// cnt를 0으로 초기화 플레이어 위치도 초기화
		// cnt = 0;
		// playerX = 10;
		// playerY = (Main.SCREEN_HEIGHT - playerHeight) / 2;
		reset();

		while (true) {
			while (!isOver) { // 오버 여부에 따라 false값이면 게임 중지
				// delay 밀리초가 지날 때마다 cnt를 증가시켜줌
				// Thread.sleep(delay);로 해결가능하지만
				// 보다 더 정확한 주기를 위해 현재시간-cnt증가하기전 시간<delay의 경우
				// 그 차이만큼 Thread에 sleep을 줌

				pretime = System.currentTimeMillis(); // 현재시간
				if (System.currentTimeMillis() - pretime < delay) {

					try { // delay- 밀리새컨드
						Thread.sleep(delay - System.currentTimeMillis() + pretime);
						keyProcess();
						playerAttackProcess();
						enemyAppearProcess();
						enemyMoveProcess();
						enemyAttackProcess();
						cnt++;
					} catch (InterruptedException e) {
						e.printStackTrace();// 예외발생 당시의 호출스택(Call Stack)에 있었던 메소드의 정보와 예외 메시지를 화면에 출력한다

					}
				}
			}
			try { // isOver가 true 값이면 쓰레드가 계속 쉬도록 해준다.
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	// 다시하기 기능을 추가함. (게임 상태를 초기화해줄 메소드를 만듬)
	public void reset() {
		isOver = false;
		cnt = 0;
		score = 0;
		playerX = 10;
		playerY = (Main.SCREEN_HEIGHT - playerHeight) / 2;
		playerHp = 30;
		

		backgroundMusic.start();
		// ArrayList들의 내용 전부 지워줌.
		playerAttackList.clear();
		enemyList.clear();
		enemyAttackList.clear();
	}

	private void keyProcess() {
		// 화면에서 안나가는 값을 조정
		if (up && playerY - playerSpeed > 0)
			playerY -= playerSpeed;
		if (down && playerY + playerHeight + playerSpeed < Main.SCREEN_HEIGHT)
			playerY += playerSpeed;
		if (left && playerX - playerSpeed > 0)
			playerX -= playerSpeed;
		if (right && playerX + playerWidth + playerSpeed < Main.SCREEN_WIDTH)
			playerX += playerSpeed;
		// cnt가 0.02초마다 올라가는 것을 고려하였을 때 0.3초마다 미사일이 발사되도록 조건
		if (shooting && cnt % 15 == 0) {
			playerAttack = new PlayerAttack(playerX + 222, playerY + 25); // 플레이어와 적당히 떯어진 위치
			playerAttackList.add(playerAttack);
		}

	}

	// 공격을 처리해주는 메소드 선언
	private void playerAttackProcess() {
		// ArrayList의 get메소드를 통해 담긴 객체 하나하나에 접근한 후 fire메소드를 실행
		for (int i = 0; i < playerAttackList.size(); i++) {
			playerAttack = playerAttackList.get(i);
			playerAttack.fire();

			// 플레이어 공격에 충돌판정 설정
			for (int j = 0; j < enemyList.size(); j++) {
				// 플레이어 공격이 적에게 충돌 했을 경우
				enemy = enemyList.get(j);
				if (playerAttack.x > enemy.x && playerAttack.x < enemy.x + enemy.width && playerAttack.y > enemy.y
						&& playerAttack.y < enemy.y + enemy.height) {
					enemy.hp -= playerAttack.attack; // 충돌 부분이 있을 시 적의 hp를 줄이며 해당 공격체를 제거
					playerAttackList.remove(playerAttack);
				}
				// 적의 hp가 0이하가 되면 제거해줌
				if (enemy.hp <= 0) {
					// 피격음은 적을 격추했을 때 , 플레이어가 공격을 맞았을 때 실행
					hitSound.start();
					enemyList.remove(enemy);
					score += 1000; // 적을 격추했을 시 점수가 오르게 함
				}
			}

		}
	}

	// 주기적으로 적을 출현시키는 메소드
	private void enemyAppearProcess() {
		// 화면 끝에서 랜덤한 위치에 출현시키기 위해 y값을 1~620랜덤으로 지정
		if (cnt % 80 == 0) {
			enemy = new Enemy(1120, (int) (Math.random() * 621));
			enemyList.add(enemy);
		}
	}

	// 적을 이동시키는 메소드
	private void enemyMoveProcess() {
		// ArrayList 안의 요소에 접근해 move 메소드를 호출함
		for (int i = 0; i < enemyList.size(); i++) {
			enemy = enemyList.get(i);
			enemy.move();
		}
	}

	// 적 공격 생성
	private void enemyAttackProcess() {
		if (cnt % 50 == 0) {// 일정 주기마다 적의 공격을 생성화여 ArrayList안에 추가
			enemyAttack = new EnemyAttack(enemy.x - 79, enemy.y + 35);
			enemyAttackList.add(enemyAttack);
		}
		// ArrayList의 get메소드를 통해 담긴 객체 하나하나에 접근한 후 fire메소드를 실행
		for (int i = 0; i < enemyAttackList.size(); i++) {
			enemyAttack = enemyAttackList.get(i);
			enemyAttack.fire();

		}
		// 적의 공격이 플레이어 이미지와 겹쳐있는지 확인 후 조건문 실행
		if (enemyAttack.x > playerX & enemyAttack.x < playerX + playerWidth && enemyAttack.y > playerY
				&& enemyAttack.y < playerY + playerHeight) {
			// 피격 시 효과음 재생
			hitSound.start();
			playerHp -= enemyAttack.attack;
			enemyAttackList.remove(enemyAttack);
			if (playerHp <= 0)
				isOver = true; // 적에게 피격 당하여 hp가 0이 도달 시 isOver를true로 바꾸어 줌 (true로 바뀔 시 게임 관련 메소드들은 실행 되지 않음)
		}
	}

	// 메소드에서는 필요한 요소를 그려줄 것이며 페인트메소드에서 버퍼 이미지를 만들고 이를 화면에 뿌려 깜빡임을 최소하한다.
	public void gameDraw(Graphics g) {
		// 앞으로 만들 게임 안의요소들을 그려주는 메소드는 전부 여기 안에 넣음
		playerDraw(g);
		enemyDraw(g);
		infoDraw(g);

	}

	// 게임 관련 정보를 그려주는 메소드
	public void infoDraw(Graphics g) {
		// 색, 폰트, 폰트 크기
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 40));
		// 설정한 폰트를 토대로 drawString 메소드를 통해 x:40 ,y: 80 위치에 점수를 출력
		g.drawString("SCORE : " + score, 40, 80);
		if (isOver) { // Boolean의 기본값은 null boolean은 false이다.
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.BOLD, 80));
			g.drawString("Press R to restart", 295, 380); // 게임이 끝났을 시 R키를 눌러 재시작 할 수 있다는 안내문을 띄어줌
		}
	}

	public void playerDraw(Graphics g) {
		g.drawImage(player, playerX, playerY, null);
		g.setColor(Color.GREEN);
		// 체력바의 배수만큼 초록색 사각형을 플레이어와 적의 위에 그려주는 방식으로 구현
		g.fillRect(playerX + 1, playerY - 40, playerHp * 6, 20);
		for (int i = 0; i < playerAttackList.size(); i++) {
			playerAttack = playerAttackList.get(i);
			g.drawImage(playerAttack.image, playerAttack.x, playerAttack.y, null);

		}

	}

	// 적과 적 공격을 그릴 enemyDraw
	public void enemyDraw(Graphics g) {
		for (int i = 0; i < enemyList.size(); i++) {
			enemy = enemyList.get(i);
			// 선언했던 클래스의 필드 x,y를 이용해 적을 그려줌
			g.drawImage(enemy.image, enemy.x, enemy.y, null);
			g.setColor(Color.GREEN);
			// 체력바의 배수만큼 초록색 사각형을 플레이어와 적의 위에 그려주는 방식으로 구현
			g.fillRect(enemy.x + 1, enemy.y - 40, enemy.hp * 15, 20);
		}
		for (int i = 0; i < enemyAttackList.size(); i++) {
			enemyAttack = enemyAttackList.get(i);
			g.drawImage(enemyAttack.image, enemyAttack.x, enemyAttack.y, null);
		}
	}

	// isOver 변수의 상태를 알 수 있도록 이에 대한 getter를 만들어 줌 
	public boolean isOver() {
		return isOver;
	}



	// private 변수의 경우 객체를 통한 직접적 접근을 못하므로 setter를 만듬
	public void setUp(boolean up) {
		this.up = up;
	}

	public void setDown(boolean down) {
		this.down = down;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public void setShooting(boolean shooting) {
		this.shooting = shooting;
	}

}
