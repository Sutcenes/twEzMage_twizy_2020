


import java.awt.image.BufferedImage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

public class VideoCap {
    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    VideoCapture cap;
    Mat2Image mat2Img = new Mat2Image();

    public VideoCap(){
        cap = new VideoCapture();
        
        cap.open(0);
        
    } 
 
    public BufferedImage getOneFrame() {
        cap.read(mat2Img.mat);
        return mat2Img.getImage(mat2Img.mat);
    }

	public Mat read() {
		Mat frame = new Mat();
		cap.read(frame);
		return frame;
	}
}