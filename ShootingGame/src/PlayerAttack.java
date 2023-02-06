import java.awt.Image;

import javax.swing.ImageIcon;

public class PlayerAttack {
	Image image = new ImageIcon("src/images/player_attack.png").getImage();
	
//공격의 이미지, 위치, 공격력 등에 대한 변수 정의
	int x, y;
	//공격의 충돌 판정 범위를 위한 너비와 높이
	int width = image.getWidth(null);
	int height = image.getHeight(null);
	int attack = 5;

	public PlayerAttack(int x, int y) {
		this.x = x;
		this.y = y;
	}
//발사 메소드/ 플레이어의 공격은 오른쪽으로만 나가므로 x값만 증가시켜줌
	public void fire() {
		this.x += 15;
	}

}
