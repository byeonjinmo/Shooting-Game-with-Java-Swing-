import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Audio {
	private Clip clip;
	private File audioFile;
	private AudioInputStream audioInputStream;
	private boolean isLoop;

	// 생성자의 매개변수로는 파일의 경로와 무한반복 여부를 넣는다.
	public Audio(String pathName, boolean isLoop) {
		try {
			// 오디오 재생에 사용할 수있는 클립을 받아옴
			clip = AudioSystem.getClip();
			audioFile = new File(pathName);
			// 경로명에 있는 파일로부터 오디오 입력 스티림을 가져옴
			audioInputStream = AudioSystem.getAudioInputStream(audioFile);
			// 클립에 오디오 입력 스티림을 받아온다. (그러면 파일을 재생할 수 있는 준비가 끝남)
			clip.open(audioInputStream);
		}
		// 예외처리
		catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}

	// 클립을 재생하는 메소드
	public void start() {
		// 클립을 파일의 처음을 가리키게 하고 재생을 실행
		clip.setFramePosition(0);
		clip.start();
		// 생성자에서 받아온 무한반복 여부를 통해 true값일 경우도 구현해줌
		if (isLoop)
			clip.loop(Clip.LOOP_CONTINUOUSLY);
	}

	// 재생하고 있는 파일을 멈추는 메소드도 만들어준다.
	public void stop() {
		clip.stop();
	}

}
