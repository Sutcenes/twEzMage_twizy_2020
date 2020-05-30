import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JScrollBar;
import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;

public class myinterface extends JFrame {

	private JPanel contentPane;
	private JTextField nomImage;
	private JTextField vitesse;
	private JPanel panel_1 ;
	private JPanel panel_2 ;
	private static JPanel panel_3 ;
	//static int choixSimilitude = 1;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					myinterface frame = new myinterface();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public myinterface() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(10, 11, 1164, 639);
		contentPane.add(panel);
		panel.setLayout(null);

		panel_1 = new JPanel();
		panel_1.setBounds(274, 46, 880, 582);
		panel.add(panel_1);
	    panel_2 = new JPanel();
		panel_2.setBounds(10, 215, 136, 117);
		panel.add(panel_2);
		panel_3 = new JPanel();
		panel_3.setBounds(10, 343, 254, 254);
		panel.add(panel_3);

		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(702, 11, 201, 23);
		comboBox.addItem("orb");
		comboBox.addItem("aire");
		comboBox.addItem("OCR");
		panel.add(comboBox);



		JButton btnChargerImage = new JButton("charger Image");
		btnChargerImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				Mat m=Highgui.imread(nomImage.getText(),Highgui.CV_LOAD_IMAGE_COLOR);
				//MaBibliothequeTraitementImageEtendue.afficheImage("Image test�e", m);
				panel_1.removeAll();
				panel_1.repaint();
				panel_1.add(new JLabel(new ImageIcon(MaBibliothequeTraitementImageEtendue.afficheImage1(m))));
				validate();
			}
		});

		btnChargerImage.setBounds(10, 11, 136, 23);
		panel.add(btnChargerImage);

		nomImage = new JTextField();
		nomImage.setBounds(580, 12, 86, 20);
		panel.add(nomImage);
		nomImage.setColumns(10);

		JButton btnNiveauxDeGris = new JButton("niveaux de gris");
		btnNiveauxDeGris.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				Mat m=Highgui.imread(nomImage.getText(),Highgui.CV_LOAD_IMAGE_COLOR);
				//Conversion du panneau extrait de l'image en gris et normalisation et redimensionnement � la taille du panneau de r�ference
				Mat grayObject = new Mat(m.rows(), m.cols(), m.type());
				//afficheImage("Panneau extrait de l'image",object);
				Imgproc.cvtColor(m, grayObject, Imgproc.COLOR_BGRA2GRAY);
				//MaBibliothequeTraitementImageEtendue.afficheImage("Image gris", grayObject);
				panel_1.removeAll();
				panel_1.repaint();
				panel_1.add(new JLabel(new ImageIcon(MaBibliothequeTraitementImageEtendue.afficheImage1(grayObject))));
				validate();
			}
		});
		btnNiveauxDeGris.setBounds(10, 45, 136, 23);
		panel.add(btnNiveauxDeGris);

		JButton btnButtonHSV = new JButton("HSV");
		btnButtonHSV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

				Mat imageOriginale=Highgui.imread(nomImage.getText(),Highgui.CV_LOAD_IMAGE_COLOR);
				Mat imageTransformee=MaBibliothequeTraitementImage.transformeBGRversHSV(imageOriginale);
				//Mat imageSatureExemple=MaBibliothequeTraitementImage.seuillage_exemple(imageTransformee, 170);	
				//Mat imageSaturee=MaBibliothequeTraitementImage.seuillage(imageTransformee, 6, 170, 110);
				//MaBibliothequeTraitementImageEtendue.afficheImage("Image HSV", imageTransformee);
				panel_1.removeAll();
				panel_1.repaint();
				panel_1.add(new JLabel(new ImageIcon(MaBibliothequeTraitementImageEtendue.afficheImage1(imageTransformee))));
				validate();
			}
		});
		btnButtonHSV.setBounds(10, 79, 136, 23);
		panel.add(btnButtonHSV);

		JButton btnSaturationRouge = new JButton("saturation Rouge");
		btnSaturationRouge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

				Mat imageOriginale=Highgui.imread(nomImage.getText(),Highgui.CV_LOAD_IMAGE_COLOR);
				Mat imageTransformee=MaBibliothequeTraitementImage.transformeBGRversHSV(imageOriginale);
				//Mat imageSatureExemple=MaBibliothequeTraitementImage.seuillage_exemple(imageTransformee, 170);	
				Mat imageSaturee=MaBibliothequeTraitementImage.seuillage(imageTransformee, 6, 170, 110);
				//MaBibliothequeTraitementImageEtendue.afficheImage("Image satur�e", imageSaturee);
				panel_1.removeAll();
				panel_1.repaint();
				panel_1.add(new JLabel(new ImageIcon(MaBibliothequeTraitementImageEtendue.afficheImage1(imageSaturee))));
				validate();
			}
		});
		btnSaturationRouge.setBounds(10, 113, 136, 23);
		panel.add(btnSaturationRouge);

		JButton btnContours = new JButton("contours");
		btnContours.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vitesse.setText("");
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

				Mat imageOriginale=Highgui.imread(nomImage.getText(),Highgui.CV_LOAD_IMAGE_COLOR);
				Mat imageTransformee=MaBibliothequeTraitementImage.transformeBGRversHSV(imageOriginale);
				//Mat imageSatureExemple=MaBibliothequeTraitementImage.seuillage_exemple(imageTransformee, 170);	
				Mat input=MaBibliothequeTraitementImage.seuillage(imageTransformee, 6, 170, 110);
				int thresh = 100;
				Mat canny_output = new Mat();
				List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
				MatOfInt4 hierarchy = new MatOfInt4();
				Imgproc.Canny( input, canny_output, thresh, thresh*2);


				/// Find extreme outer contours
				Imgproc.findContours( canny_output, contours, hierarchy,Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

				Mat drawing = Mat.zeros( canny_output.size(), CvType.CV_8UC3 );
				Random rand = new Random();
				for( int i = 0; i< contours.size(); i++ )
				{
					Scalar color = new Scalar( rand.nextInt(255 - 0 + 1) , rand.nextInt(255 - 0 + 1),rand.nextInt(255 - 0 + 1) );
					Imgproc.drawContours( drawing, contours, i, color, 1, 8, hierarchy, 0, new Point() );
				}
				//MaBibliothequeTraitementImageEtendue.afficheImage("Contours",drawing);
				panel_1.removeAll();
				panel_1.repaint();
				panel_1.add(new JLabel(new ImageIcon(MaBibliothequeTraitementImageEtendue.afficheImage1(drawing))));
				validate();

			}
		});
		btnContours.setBounds(10, 147, 136, 23);
		panel.add(btnContours);

		JButton btnMatching = new JButton("matching");
		btnMatching.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fileImg = "";
				//Ouverture le l'image et saturation des rouges
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				Mat m=Highgui.imread(nomImage.getText(),Highgui.CV_LOAD_IMAGE_COLOR);
				//MaBibliothequeTraitementImageEtendue.afficheImage("Image test�e", m);
				Mat transformee=MaBibliothequeTraitementImageEtendue.transformeBGRversHSV(m);
				//la methode seuillage est ici extraite de l'archivage jar du meme nom 
				Mat saturee=MaBibliothequeTraitementImage.seuillage(transformee, 6, 170, 110);
				Mat objetrond = null;

				//Cr�ation d'une liste des contours � partir de l'image satur�e
				List<MatOfPoint> ListeContours= MaBibliothequeTraitementImageEtendue.ExtractContours(saturee);
				int i=0;
				double [] scores=new double [6];
				//Pour tous les contours de la liste
				System.out.println("Nb de contours: "+ListeContours.size());
				for (MatOfPoint contour: ListeContours  ){
					i++;
					objetrond=MaBibliothequeTraitementImageEtendue.DetectForm(m,contour);

					if (objetrond!=null){
						if(comboBox.getSelectedItem().equals("orb"))
						{
							Principale.choixMethode=1;
							//choixSimilitude = 1;
						}
						if(comboBox.getSelectedItem().equals("aire"))
						{
							Principale.choixMethode=2;
							//choixSimilitude = 2;
						}
						if(comboBox.getSelectedItem().equals("OCR"))
						{
							Principale.choixMethode=3;
							//choixSimilitude = 3;
						}
						//MaBibliothequeTraitementImage.afficheImage("Objet rond det�ct�", objetrond);
						panel_2.removeAll();
						panel_2.repaint();
						panel_2.add(new JLabel(new ImageIcon(MaBibliothequeTraitementImageEtendue.afficheImage1(objetrond))));
						validate();
						scores[0]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref30.jpg");
						scores[1]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref50.jpg");
						scores[2]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref70.jpg");
						scores[3]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref90.jpg");
						scores[4]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref110.jpg");
						scores[5]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"refdouble.jpg");


						//recherche de l'index du maximum et affichage du panneau detect�
						double scoremax=-1;
						int indexmax=0;
						for(int j=0;j<scores.length;j++){
							if (scores[j]>scoremax){scoremax=scores[j];indexmax=j;}}	
						if(scoremax<0){System.out.println("Aucun Panneau d�t�ct�");}
						else{switch(indexmax){
						
						case -1:;break;
						case 0:vitesse.setText("Panneau 30 d�t�ct�");fileImg="ref30.jpg";break;
						case 1:vitesse.setText("Panneau 50 d�t�ct�");fileImg="ref50.jpg";break;
						case 2:vitesse.setText("Panneau 70 d�t�ct�");fileImg="ref70.jpg";break;
						case 3:vitesse.setText("Panneau 90 d�t�ct�");fileImg="ref90.jpg";break;
						case 4:vitesse.setText("Panneau 110 d�t�ct�");fileImg="ref110.jpg";break;
						case 5:vitesse.setText("Panneau interdiction de d�passer d�t�ct�");fileImg="refdouble.jpg";break;
						}}

						
					}
				}
				
				if(Principale.choixMethode==3) {
					fileImg = "gray2.png";
				}
				panel_3.removeAll();
				panel_3.repaint();
				panel_3.add(new JLabel(new ImageIcon(fileImg)));
				validate();	
				
				
			}
		});
		btnMatching.setBounds(10, 181, 136, 23);
		panel.add(btnMatching);

		vitesse = new JTextField();
		vitesse.setBounds(10, 608, 254, 20);
		panel.add(vitesse);
		vitesse.setColumns(10);
		
		
		
		JButton btnVideo = new JButton("play vid\u00E9o");	
		 btnVideo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				System.loadLibrary("opencv_ffmpeg2413_64"); //lecture video
	
				
				MyThread t1 = new MyThread(nomImage.getText(),vitesse,panel_1);
                t1.start();
			}
			
		});
		btnVideo.setBounds(149, 11, 136, 23);
		panel.add(btnVideo);
		
	}
	
	public static void LectureVideo(String nomVideo, JTextField vitesse, JPanel panel_1 ) {

		Mat frame = new Mat();
		VideoCapture camera = new VideoCapture(nomVideo);
		Mat PanneauAAnalyser = null;


		while (camera.read(frame)) {

			String fileImg = "";

			panel_1.removeAll();
			
			panel_1.add(new JLabel(new ImageIcon(Mat2bufferedImage(frame))));
			panel_1.repaint();
			panel_1.validate();
			

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
				case 0:vitesse.setText("Panneau 30 d�t�ct�");fileImg="ref30.jpg";break;
				case 1:vitesse.setText("Panneau 50 d�t�ct�");fileImg="ref50.jpg";break;
				case 2:vitesse.setText("Panneau 70 d�t�ct�");fileImg="ref70.jpg";break;
				case 3:vitesse.setText("Panneau 90 d�t�ct�");fileImg="ref90.jpg";break;
				case 4:vitesse.setText("Panneau 110 d�t�ct�");fileImg="ref110.jpg";break;
				case 5:vitesse.setText("Panneau interdiction de d�passer d�t�ct�");fileImg="refdouble.jpg";break;
				}

			}
			if(Principale.choixMethode==3) {
				fileImg = "gray2.png";
			}
			panel_3.removeAll();
			panel_3.repaint();
			panel_3.add(new JLabel(new ImageIcon(fileImg)));
			panel_3.validate();	
		}
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
}