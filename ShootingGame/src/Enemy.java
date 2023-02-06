import java.awt.Image;

import javax.swing.ImageIcon;
//Enemy클래스를 생성하여 위치정보 체력등을 변수 선언

public class Enemy {
	Image image = new ImageIcon("src/images/enemy.png").getImage();
	int x, y;
	int width = image.getWidth(null);
	int height = image.getHeight(null);
	int hp = 10;

//위치 정보를 매개변수로 받는 생성자 
	public Enemy(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void move() {
		this.x -= 7;
	}

}
