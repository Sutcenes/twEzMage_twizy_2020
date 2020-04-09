import java.awt.font.ImageGraphicAttribute;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.xml.sax.InputSource;

public class HelloOpenCV
{
	public static void main( String[] args )
	{
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );//Charge la bibliothèque OpenCv (Code en C++)
		//      Mat mat = Mat.eye( 3, 3, CvType.CV_8UC1 );//Création de la matrice identité 3*3
		//      System.out.println( "mat = \n " + mat.dump() );//Affichage de la matrice en console
		//AfficheLogo();
		//AfficheNiveauGris();
		//AfficheRGB();
		//regb2hsv("hsv.png");
		//Seuillage("circles.jpg");
		ExtractionBordures("p1.jpg");
		//reconnaissanceCerclesRouges("circles_rectangles.jpg");
		//rechercheBalle3("Billard_Balls.jpg");


	}

	public static Mat LectureImage(String fichier) {
		File f = new File(fichier);
		Mat m =  Highgui.imread(f.getAbsolutePath());
		return m;
	}

	public static void AfficheLogo() {
		Mat opencv = LectureImage("opencv.png");
		System.out.println("Hauteur : "+opencv.height());
		for (int i=0; i< opencv.height();i++) {
			for(int j=0; j< opencv.width();j++) {
				double[] BGR = opencv.get(i,j);
				if(BGR[0]+BGR[1]+BGR[2]==255*3) {
					System.out.print(".");
				}
				else {
					System.out.print("+");
				}
			}
			System.out.println();
		}
	}

	public static void AfficheNiveauGris() {
		Mat troisCercles = LectureImage("bgr.png");
		Vector<Mat> canaux = new Vector<Mat>();
		Core.split(troisCercles, canaux);
		//BRG tri
		for(int i =0; i<canaux.size();i++) {
			ImShow(Integer.toString(i),canaux.get(i));
		}
	}

	public static void AfficheRGB() {
		Mat troisCercles = LectureImage("bgr.png");
		Vector<Mat> canaux = new Vector<Mat>();
		Vector<Mat> canx = new Vector<Mat>();
		Core.split(troisCercles, canaux);
		//BRG tri
		Mat dst = Mat.zeros(troisCercles.size(),troisCercles.type());
		Mat empty = Mat.zeros(troisCercles.size(), CvType.CV_8UC1);
		for(int i =0; i<canaux.size();i++) {
			ImShow(Integer.toString(i),canaux.get(i));
			canx.removeAllElements();
			for (int j=0; j<canaux.size();j++) {
				if(j!=i) {
					canx.add(empty);
				}
				else {
					canx.add(canaux.get(i));
				}
			}
			Core.merge(canx, dst);
			ImShow(canaux.toString(), dst);
		}
	}

	public static void regb2hsv(String img) {
		Mat m = LectureImage(img);
		Mat output = Mat.zeros(m.size(),  m.type());
		Imgproc.cvtColor(m, output, Imgproc.COLOR_BGR2HSV);
		ImShow("HSV3 [regb2hsv]", output);
		Vector<Mat> canaux = new Vector<Mat>();
		Core.split(output, canaux);
		double hsv_values[][]= {{1,255,255},{179,1,255},{179,0,1}};
		for (int i = 0; i < hsv_values.length; i++) {
			ImShow(Integer.toString(i)+"-HSV", canaux.get(i));
			Mat canx[] =  new Mat[hsv_values.length];
			for(int j=0; j<hsv_values.length;j++) {
				Mat empty = Mat.ones(m.size(), CvType.CV_8UC1);
				Mat comp = Mat.ones(m.size(), CvType.CV_8UC1);
				Scalar v = new Scalar(hsv_values[i][j]);
				Core.multiply(empty, v, comp);
				canx[j]=comp;
			}
			canx[i] = canaux.get(i);
			Mat dst = Mat.zeros(output.size(), output.type());
			Mat res = Mat.ones(dst.size(), dst.type());
			Core.merge(Arrays.asList(canx), dst);
			Imgproc.cvtColor(dst, res, Imgproc.COLOR_HSV2BGR);
			ImShow(Integer.toString(i), res);
		}
	}

	public static void Seuillage(String img) {
		Mat m = LectureImage(img);
		Mat hsv_img =  Mat.zeros(m.size(), m.type());
		Imgproc.cvtColor(m, hsv_img, Imgproc.COLOR_BGR2HSV);
		Mat seuil_img = new Mat();
		Core.inRange(hsv_img, new Scalar(0,100,100), new Scalar(10,255,255), seuil_img);
		Imgproc.GaussianBlur(seuil_img, seuil_img, new Size(9,9), 2,2);
		ImShow("Cercles Rouges [Seuillage]", seuil_img);
	}

	public static Mat Seuillage(Mat hsv_img) {
		Mat seuil_img = new Mat();
		Mat seuil_img2 = new Mat();
		Mat seuilTotal = new Mat();
		Core.inRange(hsv_img, new Scalar(0,50,50), new Scalar(10,255,255), seuil_img);
		Core.inRange(hsv_img, new Scalar(170,50,50), new Scalar(180,255,255), seuil_img2);
		Core.bitwise_or(seuil_img, seuil_img2, seuilTotal);
		Imgproc.GaussianBlur(seuilTotal, seuilTotal, new Size(9,9), 2,2);
		return seuil_img;
	}

	public static void ExtractionBordures(String img) {
		Mat m = LectureImage(img);
		ImShow("Cercles [Extraction Bordures]", m);
		Mat hsv_img =  Mat.zeros(m.size(), m.type());
		Imgproc.cvtColor(m, hsv_img, Imgproc.COLOR_BGR2HSV);
		ImShow("Cercles en HSV [Extraction Bordures]", hsv_img);
		Mat seuil_img = DetecterCercles(hsv_img);
		ImShow("Seuillage [Extraction Bordures]", seuil_img);
		int seuil = 100;
		Mat canny_output=new Mat();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		MatOfInt4 hierarchie =  new MatOfInt4();
		Imgproc.Canny(seuil_img, canny_output, seuil, seuil*2);
		Imgproc.findContours(canny_output, contours, hierarchie, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		Mat dessin = Mat.zeros(canny_output.size(), CvType.CV_8UC3);
		Random rand = new Random();
		for (int i = 0; i < contours.size(); i++) {
			Scalar couleur = new Scalar(rand.nextInt(255-0+1),rand.nextInt(255-0+1),rand.nextInt(255-0+1));
			Imgproc.drawContours(dessin, contours, i, couleur, 1,8,hierarchie,0,new Point());
		}
		ImShow("Contours [Extraction Bordures]", dessin);

	}
	public static List<MatOfPoint> ExtractionBordures(Mat hsv_img) {
		Mat seuil_img = DetecterCercles(hsv_img);
		int seuil = 100;
		Mat canny_output=new Mat();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		MatOfInt4 hierarchie =  new MatOfInt4();
		Imgproc.Canny(seuil_img, canny_output, seuil, seuil*2);
		Imgproc.findContours(canny_output, contours, hierarchie, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		Mat dessin = Mat.zeros(canny_output.size(), CvType.CV_8UC3);
		Random rand = new Random();
		for (int i = 0; i < contours.size(); i++) {
			Scalar couleur = new Scalar(rand.nextInt(255-0+1),rand.nextInt(255-0+1),rand.nextInt(255-0+1));
			Imgproc.drawContours(dessin, contours, i, couleur, 1,8,hierarchie,0,new Point());
		}
		ImShow("Contours [Extraction Bordures]", dessin);
		return contours;

	}

	public static void reconnaissanceCerclesRouges(String img) {
		Mat m  = LectureImage(img);
		ImShow("Cercles [Reconnaissance Cercles Rouges]", m);
		Mat hsv_image = Mat.zeros(m.size(), m.type());
		Imgproc.cvtColor(m, hsv_image, Imgproc.COLOR_BGR2HSV);
		ImShow("HSV [Reconnaissance Cercles Rouges]", hsv_image);
		Mat seuil_img = DetecterCercles(hsv_image);
		ImShow("Seuillage [Reconnaissance Cercles Rouges]", seuil_img);


		List<MatOfPoint> contours = ExtractionBordures(seuil_img);

		MatOfPoint2f matOfPoint2f =  new MatOfPoint2f();
		float[] rayons =  new float[1];
		Point center = new Point();
		for (int i = 0; i < contours.size(); i++) {
			MatOfPoint contour =  contours.get(i);
			double contourArea = Imgproc.contourArea(contour);
			matOfPoint2f.fromList(contour.toList());
			Imgproc.minEnclosingCircle(matOfPoint2f, center, rayons);
			if((contourArea/(Math.PI*rayons[0]*rayons[0]))>=0.8) {
				Core.circle(m, center, (int)rayons[0], new Scalar(0,255,0),2);
			}
		}
		ImShow("Détection Cercles Rouges [Reconnaissance Cercles Rouges]", m);
	}

	public static void rechercheBalle3(String img) {
		ArrayList<Mat> balls = extractionBalleRouge(img);
		for (Mat ball : balls) {
			ImShow("Détection Balles Rouges [Recherche Balle 3]", ball);
			
			//Mise à l'échelle
			Mat balleRef= Highgui.imread("Ball_three.png");
			Mat sObject = new Mat();
			Imgproc.resize(ball, sObject, balleRef.size());
			Mat grayBall = new Mat(sObject.rows(), sObject.cols(),sObject.type());
			Imgproc.cvtColor(sObject, grayBall, Imgproc.COLOR_BGRA2GRAY);
			Core.normalize(grayBall, grayBall,0,255,Core.NORM_MINMAX);
			Mat grayBallRef =  new Mat(balleRef.rows(),balleRef.cols(),balleRef.type());
			Imgproc.cvtColor(balleRef, grayBallRef, Imgproc.COLOR_BGRA2GRAY);
			Core.normalize(grayBallRef, grayBallRef,0,255,Core.NORM_MINMAX);
			
			//Extraction des caractéristiques et keypoints
			FeatureDetector orbDetector = FeatureDetector.create(FeatureDetector.ORB);
			DescriptorExtractor orbExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
			
			MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
			orbDetector.detect(grayBall, objectKeyPoints);
			
			MatOfKeyPoint refKeyPoints = new MatOfKeyPoint();
			orbDetector.detect(grayBallRef, refKeyPoints);
			
			Mat objectDescriptor = new Mat(ball.rows(), ball.cols(), ball.type());
			orbExtractor.compute(grayBall, objectKeyPoints, objectDescriptor);
			
			Mat refDescriptor = new Mat(balleRef.rows(), balleRef.cols(), balleRef.type());
			orbExtractor.compute(grayBall, refKeyPoints, refDescriptor);
			
			//Matching
			MatOfDMatch matchs = new MatOfDMatch();
			DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
			matcher.match(objectDescriptor, refDescriptor,matchs);
			System.out.println(matchs.dump());
			Mat matchedImage =  new Mat(balleRef.rows(),balleRef.cols()*2,balleRef.type());
			Features2d.drawMatches(sObject, objectKeyPoints,balleRef, refKeyPoints, matchs, matchedImage);
			ImShow("MATCHED IMAGE [recherche balle 3]", matchedImage);
			
		}
		System.out.println("END");
		
		
	}

	public static ArrayList<Mat> extractionBalleRouge(String img) {
		Mat m  = LectureImage(img);
		ImShow("Cercles [Extraction Balle Rouge]", m);
		Mat hsv_image = Mat.zeros(m.size(), m.type());
		Imgproc.cvtColor(m, hsv_image, Imgproc.COLOR_BGR2HSV);
		ImShow("HSV [Extraction Balle Rouge]", hsv_image);
		Mat seuil_img = DetecterCercles(hsv_image);
		ImShow("Seuillage [Extraction Balle Rouge]", seuil_img);

		List<MatOfPoint> contours = ExtractionBordures(seuil_img);
		ArrayList<Mat> M_BALL = new ArrayList<Mat>();
		MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
		float[] rayons =  new float[1];
		Point center = new Point();
		for(int i = 0; i < contours.size(); i++) {
			MatOfPoint contour =  contours.get(i);
			double contourArea = Imgproc.contourArea(contour);
			matOfPoint2f.fromList(contour.toList());
			Imgproc.minEnclosingCircle(matOfPoint2f, center, rayons);
			if((contourArea/(Math.PI*rayons[0]*rayons[0]))>=0.8) {
				Core.circle(m, center, (int)rayons[0], new Scalar(0,255,0),2);
				Rect rect =  Imgproc.boundingRect(contour);
				Core.rectangle(m, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(0,255,0),2);
				Mat tmp = m.submat(rect.y, rect.y+rect.height, rect.x, rect.x+rect.height);
				Mat ball = Mat.zeros(tmp.size(), tmp.type());
				tmp.copyTo(ball);
				M_BALL.add(ball);
			}
		}
		return M_BALL;
	}
	public static void ImShow(String title, Mat img) {
		MatOfByte m_Byte =  new MatOfByte();
		Highgui.imencode(".png", img, m_Byte);
		byte[] byteArray = m_Byte.toArray();
		BufferedImage bufImage = null;
		try {
			InputStream in =  new ByteArrayInputStream(byteArray);
			bufImage = ImageIO.read(in);
			JFrame frame = new JFrame();
			frame.setTitle(title);
			frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
			frame.pack();
			frame.setVisible(true);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}

	public static Mat DetecterCercles(Mat hsv_img) {
		Mat m = Seuillage(hsv_img);
		return m;
	}



}
