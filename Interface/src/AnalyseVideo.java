import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
public class AnalyseVideo {

	static ArrayList<Integer> ListPanneaux = new ArrayList<Integer>();

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.loadLibrary("opencv_ffmpeg2413_64"); //lecture video
	}

	static Mat imag = null;
	static int choixMethode = 1;	//1=Orb  2=Match  3=OCR
	public static void main(String[] args) {

		JFrame jframe = new JFrame("Detection de panneaux sur un flux vid�o");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel vidpanel = new JLabel();
		jframe.setContentPane(vidpanel);
		jframe.setSize(720, 480);
		jframe.setVisible(true);

		Mat frame = new Mat();
		VideoCapture camera = new VideoCapture("video2.avi");
		Mat PanneauAAnalyser = null;
		int Moyenne = 30;
		int AncienneMoyenne;
		int indexmaxAbsent = 0;
		
		while (camera.read(frame)) {

			AncienneMoyenne = Moyenne;
			ImageIcon image = new ImageIcon(Mat2bufferedImage(frame));
			vidpanel.setIcon(image);
			vidpanel.repaint();

			Mat transformee=MaBibliothequeTraitementImageEtendue.transformeBGRversHSV(frame);
			//la methode seuillage est ici extraite de l'archivage jar du meme nom 
			Mat saturee=MaBibliothequeTraitementImage.seuillage(transformee, 6, 170, 90); 
			Mat objetrond = null;
			//Cr�ation d'une liste des contours � partir de l'image satur�e
			List<MatOfPoint> ListeContours= MaBibliothequeTraitementImageEtendue.ExtractContours(saturee);
			int i=0;
			int indexmax=0;

			for (MatOfPoint contour: ListeContours  ){
				i++;
				objetrond=MaBibliothequeTraitementImageEtendue.DetectForm(frame,contour);
				indexmax=identifiepanneau(objetrond);

				switch(indexmax){
				case -1:;break;
				case 0:
					//System.out.println("Panneau 30 d�t�ct�");
					ListPanneaux.add(30);
					break;
				case 1:
					//System.out.println("Panneau 50 d�t�ct�");
					ListPanneaux.add(50);
					break;
				case 2:
					//System.out.println("Panneau 70 d�t�ct�");
					ListPanneaux.add(70);
					break;
				case 3:
					//System.out.println("Panneau 90 d�t�ct�");
					ListPanneaux.add(90);
					break;
				case 4:
					//System.out.println("Panneau 110 d�t�ct�");
					ListPanneaux.add(110);
					break;
				case 5:
					//System.out.println("Panneau interdiction de d�passer d�t�ct�");
					break;
				}
			}
			if (indexmax == -1) {
				indexmaxAbsent ++;
			}
			if (indexmaxAbsent > 50) {
				indexmaxAbsent = 0;
				ListPanneaux.clear();
			}
			if (ListPanneaux.size() > 20){
				ListPanneaux.remove(0);
			}
			Moyenne = Moyenne(ListPanneaux);
			
			if (Moyenne != AncienneMoyenne && !ListPanneaux.isEmpty()) {
				System.out.println(ListPanneaux);
				System.out.println(Moyenne+"\n\n");
			}
		}
	}


	
	
	
	public static int Moyenne(ArrayList<Integer> L) {
		int[] T = new int[5];
		for (Integer i : L) {
			if(i == 30) {
				T[0]++;
			}
			else if(i == 50) {
				T[1]++;
			}
			else if(i==70) {
				T[2]++;
			}
			else if (i==90) {
				T[3]++;
			}
			else {//i==110
				T[4]++;
			}
		}
		int indice= 0;
		int max = T[0];
		for(int i = 1; i<T.length; i++) {
			if (T[i]>max) {
				indice = i;
				max = T[i];
			}
		}
		switch (indice) {
		case 1:
			return 50;

		case 2:
			return 70;

		case 3:
			return 90;

		case 4:
			return 110;

		case 0:
			return 30;

		default:
			return -1;
		}

	}



	public static BufferedImage Mat2bufferedImage(Mat image) {
		MatOfByte bytemat = new MatOfByte();
		Highgui.imencode(".jpg", image, bytemat);
		byte[] bytes = bytemat.toArray();
		InputStream in = new ByteArrayInputStream(bytes);
		BufferedImage img = null;
		try {
			img = ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return img;
	}



	public static int identifiepanneau(Mat objetrond){
		double [] scores=new double [6];
		int indexmax=-1;
		if (objetrond!=null){
			scores[0]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref30.jpg");
			scores[1]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref50.jpg");
			scores[2]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref70.jpg");
			scores[3]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref90.jpg");
			scores[4]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref110.jpg");
			scores[5]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"refdouble.jpg");

			double scoremax=scores[0];

			for(int j=1;j<scores.length;j++){
				if (scores[j]>scoremax){scoremax=scores[j];indexmax=j;}}	


		}
		return indexmax;
	}


}