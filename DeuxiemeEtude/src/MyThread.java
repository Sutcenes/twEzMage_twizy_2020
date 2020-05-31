import javax.swing.JPanel;
import javax.swing.JTextField;

import org.opencv.core.Core;

public class MyThread extends Thread {
	private String videoName;
	private JTextField vitesse;
	private JPanel panel_1;
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.loadLibrary("opencv_ffmpeg2413_64"); //lecture video
	}
	public MyThread(String videoName, JTextField vitesse, JPanel panel_1 )
	   {
		this.videoName = videoName;
		this.vitesse = vitesse;
		this.panel_1 = panel_1;
		return;
	   }
	
	@Override
	public void run() {
		myinterface.LectureVideo(this.videoName,this.vitesse,this.panel_1);
		return;
	}
}