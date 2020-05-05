import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class MaBibliothequeTraitementImageEtendue {
	//Contient toutes les m�thodes necessaires � la transformation des images


	//Methode qui permet de transformer une matrice intialement au  format BGR au format HSV
	public static Mat transformeBGRversHSV(Mat matriceBGR){
		Mat matriceHSV=new Mat(matriceBGR.height(),matriceBGR.cols(),matriceBGR.type());
		Imgproc.cvtColor(matriceBGR,matriceHSV,Imgproc.COLOR_BGR2HSV);
		return matriceHSV;

	}

	//Methode qui convertit une matrice avec 3 canaux en un vecteur de 3 matrices monocanal (un canal par couleur)
	public static Vector<Mat> splitHSVChannels(Mat input) {
		Vector<Mat> channels = new Vector<Mat>(); 
		Core.split(input, channels);
		return channels;
	}

	//Methode qui permet d'afficher une image sur un panel
	public static void afficheImage(String title, Mat img){
		MatOfByte matOfByte=new MatOfByte();
		Highgui.imencode(".png",img,matOfByte);
		byte[] byteArray=matOfByte.toArray();
		BufferedImage bufImage=null;
		try{
			InputStream in=new ByteArrayInputStream(byteArray);
			bufImage=ImageIO.read(in);
			JFrame frame=new JFrame();
			frame.setTitle(title);
			frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
			frame.pack();
			frame.setVisible(true);

		}
		catch(Exception e){
			e.printStackTrace();
		}


	}



	//Methode qui permet d'extraire les contours d'une image donnee
	public static List<MatOfPoint> ExtractContours(Mat input) {
		// Detecter les contours des formes trouv�es
		int thresh = 100;
		Mat canny_output = new Mat();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		MatOfInt4 hierarchy = new MatOfInt4();
		Imgproc.Canny( input, canny_output, thresh, thresh*2);



		switch (AnalyseVideo.choixMethode) {
		case 1 :
			// Find extreme outer contours
			Imgproc.findContours( canny_output, contours, hierarchy,Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
			break;
		case 2 :
			// Find extreme outer contours
			Imgproc.findContours( canny_output, contours, hierarchy,Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
			break;
		case 3 :
			//Trouver le cercle INTERIEUR (OCR)
			Imgproc.findContours( canny_output, contours, hierarchy,Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
			break;
		}


		//System.out.println(hierarchy.dump());
		Mat drawing = Mat.zeros( canny_output.size(), CvType.CV_8UC3 );
		Random rand = new Random();


		/**************OCR***************/
		if(AnalyseVideo.choixMethode==3) {
			//A commenter si on veut contours externes
			List<MatOfPoint> newContours = new ArrayList<MatOfPoint>();
			//System.out.println(hierarchy.get(0, 1)[0]);

			for(int i=0;i<contours.size();i++) {
				if(hierarchy.get(0, i)[2]==-1) {
					newContours.add(contours.get(i));
				}

			}
			contours=newContours;

		}


		for( int i = 0; i< contours.size(); i++ )
		{
			Scalar color = new Scalar( rand.nextInt(255 - 0 + 1) , rand.nextInt(255 - 0 + 1),rand.nextInt(255 - 0 + 1) );
			Imgproc.drawContours( drawing, contours, i, color, 1, 8, hierarchy, 0, new Point() );
		}
		//afficheImage("Contours",drawing);

		return contours;
	}

	//Methode qui permet de decouper et identifier les contours carr�s, triangulaires ou rectangulaires. 
	//Renvoie null si aucun contour rond n'a �t� trouv�.	
	//Renvoie une matrice carr�e englobant un contour rond si un contour rond a �t� trouv�
	public static Mat DetectForm(Mat img,MatOfPoint contour) {
		MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		float[] radius = new float[1];
		Point center = new Point();
		Rect rect = Imgproc.boundingRect(contour);
		double contourArea = Imgproc.contourArea(contour);


		matOfPoint2f.fromList(contour.toList());
		// Cherche le plus petit cercle entourant le contour
		Imgproc.minEnclosingCircle(matOfPoint2f, center, radius);
		//System.out.println(contourArea+" "+Math.PI*radius[0]*radius[0]);
		//on dit que c'est un cercle si l'aire occup� par le contour est � sup�rieure �  80% de l'aire occup�e par un cercle parfait
		if ((contourArea / (Math.PI*radius[0]*radius[0])) >=0.8) {
			//System.out.println("Cercle");

			//A commenter si on veut pas tracer le cercle et le rectangle (OCR par ex)
			if(AnalyseVideo.choixMethode==1) {

				Core.circle(img, center, (int)radius[0], new Scalar(255, 0, 0), 2);
				Core.rectangle(img, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar (0, 255, 0), 2);

			}
			Mat tmp = img.submat(rect.y,rect.y+rect.height,rect.x,rect.x+rect.width);
			Mat sign = Mat.zeros(tmp.size(),tmp.type());
			tmp.copyTo(sign);


			/**************OCR***************/
			if(AnalyseVideo.choixMethode==3) {

				//Pour Methode OCR: coloriage en blanc du bord du panneau (rouge)
				//System.out.println("rayon: "+radius[0]);
				//System.out.println("centrex: "+center.x+" centrey: "+center.y);
				//System.out.println(sign.height()+" x "+sign.width());
				Scalar blanc = new Scalar(255,255,255);
				double[] BGR=new double[3];
				BGR[0]=255;
				BGR[1]=255;
				BGR[2]=255;
				Point centre=new Point(sign.height()/2,sign.width()/2);
				//System.out.println("centrex: "+centre.x+" centrey: "+centre.y);
				for(int i=0;i<sign.height();i++) {
					for(int j=0;j<sign.width();j++) {
						if(Math.sqrt((i-centre.x)*(i-centre.x) + (j-centre.y)*(j-centre.y))>=radius[0]-3) {
							sign.put(i, j, BGR);
						}

					}

				}
			}


			//afficheImage("Sign",sign);
			return sign;
		}else {

			Imgproc.approxPolyDP(matOfPoint2f, approxCurve, Imgproc.arcLength(matOfPoint2f, true) * 0.02, true);
			long total = approxCurve.total();
			if (total == 3 ) { // is triangle
				//System.out.println("Triangle");
				Point [] pt = approxCurve.toArray();
				Core.line(img, pt[0], pt[1], new Scalar(255,0,0),2);
				Core.line(img, pt[1], pt[2], new Scalar(255,0,0),2);
				Core.line(img, pt[2], pt[0], new Scalar(255,0,0),2);
				Core.rectangle(img, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar (0, 255, 0), 2);
				Mat tmp = img.submat(rect.y,rect.y+rect.height,rect.x,rect.x+rect.width);
				Mat sign = Mat.zeros(tmp.size(),tmp.type());
				tmp.copyTo(sign);
				return null;
			}
			if (total >= 4 && total <= 6) {
				List<Double> cos = new ArrayList<>();
				Point[] points = approxCurve.toArray();
				for (int j = 2; j < total + 1; j++) {
					cos.add(angle(points[(int) (j % total)], points[j - 2], points[j - 1]));
				}
				Collections.sort(cos);
				Double minCos = cos.get(0);
				Double maxCos = cos.get(cos.size() - 1);
				boolean isRect = total == 4 && minCos >= -0.1 && maxCos <= 0.3;
				boolean isPolygon = (total == 5 && minCos >= -0.34 && maxCos <= -0.27) || (total == 6 && minCos >= -0.55 && maxCos <= -0.45);
				if (isRect) {
					double ratio = Math.abs(1 - (double) rect.width / rect.height);
					//drawText(rect.tl(), ratio <= 0.02 ? "SQU" : "RECT");
					//System.out.println("Rectangle");
					Core.rectangle(img, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar (0, 255, 0), 2);
					Mat tmp = img.submat(rect.y,rect.y+rect.height,rect.x,rect.x+rect.width);
					Mat sign = Mat.zeros(tmp.size(),tmp.type());
					tmp.copyTo(sign);
					return null;
				}
				if (isPolygon) {
					//System.out.println("Polygon");
					//drawText(rect.tl(), "Polygon");
				}
			}			
		}
		return null;

	}



	public static double angle(Point a, Point b, Point c) {
		Point ab = new Point( b.x - a.x, b.y - a.y );
		Point cb = new Point( b.x - c.x, b.y - c.y );
		double dot = (ab.x * cb.x + ab.y * cb.y); // dot product
		double cross = (ab.x * cb.y - ab.y * cb.x); // cross product
		double alpha = Math.atan2(cross, dot);
		return Math.floor(alpha * 180. / Math.PI + 0.5);
	}



	public static double Similitude(Mat object,String signfile) {
		double indicateur=-1;

		// Conversion du signe de reference en niveaux de gris et normalisation
		Mat panneauref = Highgui.imread(signfile);
		Mat graySign = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		Imgproc.cvtColor(panneauref, graySign, Imgproc.COLOR_BGRA2GRAY);
		Core.normalize(graySign, graySign, 0, 255, Core.NORM_MINMAX);
		//Mat signeNoirEtBlanc=new Mat();



		//Conversion du panneau extrait de l'image en gris et normalisation et redimensionnement � la taille du panneau de r�ference
		Mat grayObject = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		Imgproc.resize(object, object, graySign.size());
		//afficheImage("Panneau extrait de l'image",object);
		Imgproc.cvtColor(object, grayObject, Imgproc.COLOR_BGRA2GRAY);
		Core.normalize(grayObject, grayObject, 0, 255, Core.NORM_MINMAX);
		Imgproc.resize(grayObject, grayObject, graySign.size());


		switch (AnalyseVideo.choixMethode) {

		/***Methode ORB***/
		case 1 :

			//Extraction des descripteurs et keypoints
			FeatureDetector orbDetector = FeatureDetector.create(FeatureDetector.ORB);
			DescriptorExtractor orbExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);

			MatOfKeyPoint objectKeypoints = new MatOfKeyPoint();
			orbDetector.detect(grayObject, objectKeypoints);

			MatOfKeyPoint signKeypoints = new MatOfKeyPoint();
			orbDetector.detect(graySign, signKeypoints);

			Mat objectDescriptor = new Mat(object.rows(), object.cols(), object.type());
			orbExtractor.compute(grayObject,  objectKeypoints,  objectDescriptor);

			Mat signDescriptor = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
			orbExtractor.compute(graySign,  signKeypoints,  signDescriptor);

			//Faire le matching
			MatOfDMatch matchs = new MatOfDMatch();
			DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
			matcher.match(objectDescriptor, signDescriptor,matchs);
			//System.out.println(matchs.dump());
			Mat matchedImage = new Mat(panneauref.rows(), panneauref.cols()*2, panneauref.type());
			Features2d.drawMatches(object, objectKeypoints, panneauref, signKeypoints, matchs, matchedImage);
			MaBibliothequeTraitementImage.afficheImage("matches",matchedImage);

			//System.out.println(matchs.size().height);


			List<Double> distances=new ArrayList<Double>();
			for(int i=0;i<matchs.size().height;i++) {
				distances.add(matchs.get(i, 0)[3]);					//matchs.get(i, 0)[3]
			}
			distances.sort(Comparator.reverseOrder());
			//System.out.println(distances);

			double moy=0;
			int n=50;
			//System.out.println(distances.size());
			for(int i=0;i<n;i++) {
				moy+=distances.get(i);
			}
			//System.out.println(distances);
			moy=moy/n;

			System.out.println("Moyenne: "+signfile+" "+moy);
			//System.out.println();

			indicateur = moy;

			break;

			/***Methode Match Pixels***/
		case 2 :

			int nbBlancs=0;
			//System.out.println(signfile);
			//afficheImage("Panneau gris",grayObject);
			//afficheImage("Ref gris",graySign);
			for(int i=0;i<grayObject.height();i++) {
				for(int j=0;j<grayObject.width();j++) {
					double[] BGRObject = grayObject.get(i, j);
					double[] BGRSign = graySign.get(i, j);

					//System.out.println(Arrays.toString(BGRObject));
					if((BGRObject[0]+BGRSign[0])/2>=230 ) {
						nbBlancs++;
					}
				}
			}
			indicateur = nbBlancs;

			break;

			/***Methode OCR***/	
		case 3 :

			String ABSOLUTE_PATH = new File("").getAbsolutePath().replace("\\", "\\\\");

			//Image en noir (0) et blanc (255) uniquement (pas de nuance)
			for(int i=0;i<grayObject.height();i++) {
				for(int j=0;j<grayObject.width();j++) {
					double[] BGRObject = grayObject.get(i, j);
					if(BGRObject[0]>100) {
						grayObject.put(i, j, 255);
					}
					else {
						grayObject.put(i, j, 0);
					}
				}
			}


			Highgui.imwrite("gray2.png",grayObject);
			File imageFile = new File("gray2.png");

			ITesseract instance = new Tesseract();
			instance.setDatapath(ABSOLUTE_PATH+"\\tessdata");

			try {
				String result = instance.doOCR(imageFile);
				System.out.println(result);

				//Traitement du r�sultat: suppression des erreurs/caracteres en trop
				result=result.split("\n")[0];
				String newResult="";
				for(int i=0;i<result.length();i++) {
					if(Character.isDigit(result.charAt(i))) newResult=newResult+result.charAt(i);
					else if (Character.toString(result.charAt(i)).toUpperCase().equals("O")) newResult=newResult+"0";
				}
				result=newResult;

				System.out.println(result);
				System.out.println(signfile.split("ref")[1].split(".jpg")[0]);
				System.out.println(result.equals(signfile.split("ref")[1].split(".jpg")[0]));
				System.out.println();


				if(result.equals(signfile.split("ref")[1].split(".jpg")[0])) return 1;


			} catch (TesseractException e) {
				System.err.println(e.getMessage());

			}

			indicateur=0;

			break;
		}

		return indicateur;
	}


}



