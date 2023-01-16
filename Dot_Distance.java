import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.WaitForUserDialog;
import ij.io.FileInfo;
import ij.measure.Calibration;
import ij.plugin.*;
import ij.process.ImageProcessor;

public class Dot_Distance implements PlugIn, MouseListener{
	ImagePlus ProjectedWindow;
	int ProjectedWindowID;
	ImagePlus Greeny;
	ImagePlus Reddy;
	ImagePlus ImSave;
	int[] x = new int[2];
	int[] y = new int[2];
	int[]  SaveImID = new int[2];
	ImageCanvas canvas;
	int count;
	String theparentDirectory;
	String filename;
	int xID;
	int yID;
	int LowValG;
	int HighValG;
	int LowValR;
	int HighValR;
	ImagePlus BlueImage;
	int BlueImageID;
	ImagePlus GreenImage;
	int GreenImageID;
	
	public void run(String arg) {

		new WaitForUserDialog("Open Image", "Open Airyscan Image.").show();
		IJ.run("Bio-Formats Importer");		
		//GET THE FILE NAME
		ImagePlus imp =  WindowManager.getCurrentImage();
		filename = imp.getShortTitle();//Get file name
		FileInfo theDirectory = imp.getOriginalFileInfo();
		theparentDirectory = theDirectory.directory;
		new WaitForUserDialog("DAPI Image", "Click Dapi Image.").show();
		BlueImage = WindowManager.getCurrentImage();
		BlueImageID = BlueImage.getID();
		IJ.run(BlueImage, "Enhance Contrast", "saturated=0.35"); //Autoscale image
		
		new WaitForUserDialog("GFP Image", "Click GFP Image.").show();
		GreenImage = WindowManager.getCurrentImage();
		GreenImageID = GreenImage.getID();
		IJ.run(GreenImage, "Enhance Contrast", "saturated=0.35"); //Autoscale image
		
		new WaitForUserDialog("Red Image", "Click Red Image.").show();
		ProjectedWindow = WindowManager.getCurrentImage();
		ProjectedWindowID = ProjectedWindow.getID();
		IJ.run(ProjectedWindow, "Enhance Contrast", "saturated=0.35"); //Autoscale image
		
		//Get Calibration for measurements
		IJ.selectWindow(ProjectedWindowID);
		Calibration ImCal = ProjectedWindow.getCalibration();
		double xVal = ImCal.pixelWidth;
		IJ.run(ProjectedWindow, "Set Scale...", "distance=0 known=0 unit=pixel");
		
		MergeImages();
		MeasureSPB(xVal);
		
		new WaitForUserDialog("Finished", "All done.").show();
		IJ.run("Close All", "");
	}
	
	public void MergeImages() {
		ImagePlus[] ImArray = new ImagePlus[2];
		ImArray[0] = BlueImage;
		ImArray[1] = GreenImage;
		boolean keepSourceImages = false;
		ImagePlus tempImage = RGBStackMerge.mergeChannels(ImArray, keepSourceImages);
		tempImage.show();
	}
	 
	 public void MeasureSPB(double xVal){
		 String response;
		 IJ.selectWindow(ProjectedWindowID);
		 int Counter = 1;
		
		 response = "y";
		 IJ.setTool("multipoint");

		 do{
			for (int a = 1; a<2; a++){
				canvas = ProjectedWindow.getWindow().getCanvas(); 
				canvas.addMouseListener(this);	
				new WaitForUserDialog("Click", "Click 2 Points then OK").show();
				if (count == 2){
					double Xt = Math.abs(x[0]-x[1]);
					double Yt = Math.abs(y[0]-y[1]);
					double distance = (Math.sqrt((Xt*Xt)+((Yt*Yt))))*xVal;
					xID = x[0];
					yID = y[0];
					int zPos = ProjectedWindow.getZ();
					outputinfo(distance, xID, yID, zPos, Counter);
					setImageNumbers(Counter);
					count=0;
					Counter++;
					//Clear mouse listener
					canvas.removeMouseListener(this);
					//Clear selected points
					IJ.run(ProjectedWindow, "Select None", "");
				
				}
			}			
			response = JOptionPane.showInputDialog("Another y/n");
		 }while(response.equals("y"));
			
	 }
	 
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}		
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {
		x[count] =canvas.offScreenX(e.getX());
		y[count] = canvas.offScreenY(e.getY());
		count = count + 1;
		if (count == 2){
			return;
		}
	}
		
	public void mouseEntered(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}
			
	public void outputinfo(double distance, int xID, int yID,int zPos, int Counter){
		
		String CreateName = theparentDirectory + filename + ".txt";
		String FILE_NAME = CreateName;
		
		try{
			FileWriter fileWriter = new FileWriter(FILE_NAME,true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			if(Counter==1) {
				bufferedWriter.newLine();
				bufferedWriter.write("File= " + filename);
				bufferedWriter.newLine();
			}
			bufferedWriter.write("Z Position= " + zPos + " Distance= " + distance + " Z Position= " + zPos + " X= " + xID + " Y= " + yID);
			bufferedWriter.newLine();
			bufferedWriter.close();

		}
		catch(IOException ex) {
	        System.out.println(
	        "Error writing to file '"
	        + FILE_NAME + "'");
	    }
	} 
	
	
	public void setImageNumbers(int Counter){
		IJ.setForegroundColor(255, 255, 255);
		ImageProcessor ip = ProjectedWindow.getProcessor();
		Font font = new Font("SansSerif", Font.BOLD, 38);
		ip.setFont(font);
		ip.setColor(new Color(255, 255, 0));
		String cellnumber = String.valueOf(Counter);
		int xpos = (int) xID;
		int ypos = (int) yID;
		ip.drawString(cellnumber, xpos, ypos);
		ProjectedWindow.updateAndDraw();
	}

}
