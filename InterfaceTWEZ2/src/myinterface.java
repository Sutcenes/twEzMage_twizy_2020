import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.event.ActionEvent;

public class myinterface extends JFrame {

	private JPanel contentPane;
	private JTextField nomImage;
	private JTextField vitesse;

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
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JButton btnChargerImage = new JButton("charger Image");
		btnChargerImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				Mat m=Highgui.imread(nomImage.getText(),Highgui.CV_LOAD_IMAGE_COLOR);
				MaBibliothequeTraitementImageEtendue.afficheImage("Image testée", m);
				
			}
		});
		btnChargerImage.setBounds(10, 11, 136, 23);
		panel.add(btnChargerImage);
		
		nomImage = new JTextField();
		nomImage.setBounds(235, 12, 86, 20);
		panel.add(nomImage);
		nomImage.setColumns(10);
		
		JButton btnNiveauxDeGris = new JButton("niveaux de gris");
		btnNiveauxDeGris.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				Mat m=Highgui.imread(nomImage.getText(),Highgui.CV_LOAD_IMAGE_COLOR);
				//Conversion du panneau extrait de l'image en gris et normalisation et redimensionnement à la taille du panneau de réference
				Mat grayObject = new Mat(m.rows(), m.cols(), m.type());
				//afficheImage("Panneau extrait de l'image",object);
				Imgproc.cvtColor(m, grayObject, Imgproc.COLOR_BGRA2GRAY);
				MaBibliothequeTraitementImageEtendue.afficheImage("Image gris", grayObject);
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
				MaBibliothequeTraitementImageEtendue.afficheImage("Image HSV", imageTransformee);
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
				MaBibliothequeTraitementImageEtendue.afficheImage("Image saturée", imageSaturee);
			}
		});
		btnSaturationRouge.setBounds(10, 113, 136, 23);
		panel.add(btnSaturationRouge);
		
		JButton btnContours = new JButton("contours");
		btnContours.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
				MaBibliothequeTraitementImageEtendue.afficheImage("Contours",drawing);

			}
		});
		btnContours.setBounds(10, 147, 136, 23);
		panel.add(btnContours);
		
		JButton btnMatching = new JButton("matching");
		btnMatching.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Ouverture le l'image et saturation des rouges
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				Mat m=Highgui.imread(nomImage.getText(),Highgui.CV_LOAD_IMAGE_COLOR);
				//MaBibliothequeTraitementImageEtendue.afficheImage("Image testée", m);
				Mat transformee=MaBibliothequeTraitementImageEtendue.transformeBGRversHSV(m);
				//la methode seuillage est ici extraite de l'archivage jar du meme nom 
				Mat saturee=MaBibliothequeTraitementImage.seuillage(transformee, 6, 170, 110);
				Mat objetrond = null;

				//Création d'une liste des contours à partir de l'image saturée
				List<MatOfPoint> ListeContours= MaBibliothequeTraitementImageEtendue.ExtractContours(saturee);
				int i=0;
				double [] scores=new double [6];
				//Pour tous les contours de la liste
				System.out.println("Nb de contours: "+ListeContours.size());
				for (MatOfPoint contour: ListeContours  ){
					i++;
					objetrond=MaBibliothequeTraitementImageEtendue.DetectForm(m,contour);

					if (objetrond!=null){
						MaBibliothequeTraitementImage.afficheImage("Objet rond detécté", objetrond);
						scores[0]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref30.jpg");
						scores[1]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref50.jpg");
						scores[2]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref70.jpg");
						scores[3]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref90.jpg");
						scores[4]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref110.jpg");
						scores[5]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"refdouble.jpg");


						//recherche de l'index du maximum et affichage du panneau detecté
						double scoremax=-1;
						int indexmax=0;
						for(int j=0;j<scores.length;j++){
							if (scores[j]>scoremax){scoremax=scores[j];indexmax=j;}}	
						if(scoremax<0){System.out.println("Aucun Panneau détécté");}
						else{switch(indexmax){
						case -1:;break;
						case 0:vitesse.setText("Panneau 30 détécté");break;
						case 1:vitesse.setText("Panneau 50 détécté");break;
						case 2:vitesse.setText("Panneau 70 détécté");break;
						case 3:vitesse.setText("Panneau 90 détécté");break;
						case 4:vitesse.setText("Panneau 110 détécté");break;
						case 5:vitesse.setText("Panneau interdiction de dépasser détécté");break;
						}}

					}
				}
			}
		});
		btnMatching.setBounds(10, 181, 136, 23);
		panel.add(btnMatching);
		
		vitesse = new JTextField();
		vitesse.setBounds(10, 220, 404, 20);
		panel.add(vitesse);
		vitesse.setColumns(10);
	}

}
