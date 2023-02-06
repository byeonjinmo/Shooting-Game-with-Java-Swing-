import java.awt.Image;

import javax.swing.ImageIcon;

public class EnemyAttack {
	Image image = new ImageIcon("src/images/enemy_attack.png").getImage();
	int x, y;
	int width = image.getWidth(null);
	int height = image.getHeight(null);
	int attack = 5;

	// 위치 정보를 매개변수로 받는 생성자
	public EnemyAttack(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void fire() {
		this.x -= 12;
	}

}
