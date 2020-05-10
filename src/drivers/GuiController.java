package drivers;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import dataGenerator.WeaponStatsGenerator;
import drillerWeapons.EPC_ChargeShot;
import drillerWeapons.EPC_RegularShot;
import drillerWeapons.Flamethrower;
import drillerWeapons.Subata;
import engineerWeapons.GrenadeLauncher;
import engineerWeapons.SMG;
import engineerWeapons.Shotgun;
import guiPieces.HoverText;
import guiPieces.View;
import gunnerWeapons.Autocannon;
import gunnerWeapons.BurstPistol;
import gunnerWeapons.Minigun;
import gunnerWeapons.Revolver_FullRoF;
import gunnerWeapons.Revolver_Snipe;
import modelPieces.EnemyInformation;
import modelPieces.Weapon;
import scoutWeapons.Boomstick;
import scoutWeapons.Classic_FocusShot;
import scoutWeapons.Classic_Hipfire;
import scoutWeapons.AssaultRifle;
import scoutWeapons.Zhukov;

/*
	Benchmarks: 
		150 Ideal Burst DPS
		100 Ideal Sustained DPS
		125 Sustained + Weakpoint
		8000 Total Damage
*/

public class GuiController implements ActionListener {
	
	private Weapon[] drillerWeapons;
	private Weapon[] engineerWeapons;
	private Weapon[] gunnerWeapons;
	private Weapon[] scoutWeapons;
	private View gui;
	private WeaponStatsGenerator calculator;
	private JFileChooser folderChooser;
	
	public static void main(String[] args) {
		Weapon[] drillerWeapons = new Weapon[] {new Flamethrower(), new Subata(), new EPC_RegularShot(), new EPC_ChargeShot()};
		Weapon[] engineerWeapons = new Weapon[] {new Shotgun(), new SMG(), new GrenadeLauncher()};
		Weapon[] gunnerWeapons = new Weapon[] {new Minigun(), new Autocannon(), new Revolver_Snipe(), new Revolver_FullRoF(), new BurstPistol()};
		Weapon[] scoutWeapons = new Weapon[] {new AssaultRifle(), new Classic_Hipfire(), new Classic_FocusShot(), new Boomstick(), new Zhukov()};
		View gui = new View(drillerWeapons, engineerWeapons, gunnerWeapons, scoutWeapons);
		new GuiController(drillerWeapons, engineerWeapons, gunnerWeapons, scoutWeapons, gui);
	}
	
	public GuiController(Weapon[] dWeapons, Weapon[] eWeapons, Weapon[] gWeapons, Weapon[] sWeapons, View inputGui) {
		drillerWeapons = dWeapons;
		engineerWeapons = eWeapons;
		gunnerWeapons = gWeapons;
		scoutWeapons = sWeapons;
		gui = inputGui;
		gui.activateButtonsAndMenus(this);
		Weapon weaponSelected;
		if (drillerWeapons.length > 0) {
			weaponSelected = drillerWeapons[0];
		}
		else if (engineerWeapons.length > 0) {
			weaponSelected = engineerWeapons[0];
		}
		else if (gunnerWeapons.length > 0) {
			weaponSelected = gunnerWeapons[0];
		}
		else if (scoutWeapons.length > 0) {
			weaponSelected = scoutWeapons[0];
		}
		else {
			System.out.println("Error: no weapons in GuiController's arrays");
			weaponSelected = new Minigun();
		}
		calculator = new WeaponStatsGenerator(weaponSelected);
		folderChooser = new JFileChooser();
		folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}
	
	private void chooseFolder() {
		int returnVal = folderChooser.showOpenDialog(null);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File selectedFolder = folderChooser.getSelectedFile();
			calculator.setCSVFolderPath(selectedFolder.getAbsolutePath());
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object e = arg0.getSource();
		
		Weapon currentlySelectedWeapon;
		int classIndex = gui.getCurrentClassIndex();
		
		// Have these commands disabled when Information is at the front.
		if (classIndex > 3) {
			return;
		}
		
		int weaponIndex = gui.getCurrentWeaponIndex();
		if (classIndex == 0) {
			currentlySelectedWeapon = drillerWeapons[weaponIndex];
		}
		else if (classIndex == 1) {
			currentlySelectedWeapon = engineerWeapons[weaponIndex];
		}
		else if (classIndex == 2) {
			currentlySelectedWeapon = gunnerWeapons[weaponIndex];
		}
		else if (classIndex == 3) {
			currentlySelectedWeapon = scoutWeapons[weaponIndex];
		}
		else {
			System.out.println("Error: no weapons in GuiController's arrays");
			currentlySelectedWeapon = new Minigun();
		}
		calculator.changeWeapon(currentlySelectedWeapon);
		
		if (e == gui.getBcmIdealBurst()) {
			gui.activateThinkingCursor();
			currentlySelectedWeapon.buildFromCombination(calculator.getBestIdealBurstDPSCombination());
			gui.deactivateThinkingCursor();
		}
		else if (e == gui.getBcmIdealSustained()) {
			gui.activateThinkingCursor();
			currentlySelectedWeapon.buildFromCombination(calculator.getBestIdealSustainedDPSCombination());
			gui.deactivateThinkingCursor();
		}
		else if (e == gui.getBcmSustainedWeakpoint()) {
			gui.activateThinkingCursor();
			currentlySelectedWeapon.buildFromCombination(calculator.getBestSustainedWeakpointDPSCombination());
			gui.deactivateThinkingCursor();
		}
		else if (e == gui.getBcmSustainedWeakpointAccuracy()) {
			gui.activateThinkingCursor();
			currentlySelectedWeapon.buildFromCombination(calculator.getBestSustainedWeakpointAccuracyDPSCombination());
			gui.deactivateThinkingCursor();
		}
		else if (e == gui.getBcmIdealAdditional()) {
			gui.activateThinkingCursor();
			currentlySelectedWeapon.buildFromCombination(calculator.getBestIdealAdditionalTargetDPSCombination());
			gui.deactivateThinkingCursor();
		}
		else if (e == gui.getBcmMaxDmg()) {
			gui.activateThinkingCursor();
			currentlySelectedWeapon.buildFromCombination(calculator.getHighestMultiTargetDamageCombination());
			gui.deactivateThinkingCursor();
		}
		else if (e == gui.getBcmMaxNumTargets()) {
			gui.activateThinkingCursor();
			currentlySelectedWeapon.buildFromCombination(calculator.getMostNumTargetsCombination());
			gui.deactivateThinkingCursor();
		}
		else if (e == gui.getBcmDuration()) {
			gui.activateThinkingCursor();
			currentlySelectedWeapon.buildFromCombination(calculator.getLongestFiringDurationCombination());
			gui.deactivateThinkingCursor();
		}
		else if (e == gui.getBcmTTK()) {
			gui.activateThinkingCursor();
			currentlySelectedWeapon.buildFromCombination(calculator.getShortestTimeToKillCombination());
			gui.deactivateThinkingCursor();
		}
		else if (e == gui.getBcmOverkill()) {
			gui.activateThinkingCursor();
			currentlySelectedWeapon.buildFromCombination(calculator.getLowestOverkillCombination());
			gui.deactivateThinkingCursor();
		}
		else if (e == gui.getBcmAccuracy()) {
			gui.activateThinkingCursor();
			currentlySelectedWeapon.buildFromCombination(calculator.getHighestAccuracyCombination());
			gui.deactivateThinkingCursor();
		}
		else if (e == gui.getBcmUtility()) {
			gui.activateThinkingCursor();
			currentlySelectedWeapon.buildFromCombination(calculator.getMostUtilityCombination());
			gui.deactivateThinkingCursor();
		}
		
		else if (e == gui.getDSHaz1()) {
			EnemyInformation.setHazardLevel(1);
			gui.updateDifficultyScaling();
		}
		else if (e == gui.getDSHaz2()) {
			EnemyInformation.setHazardLevel(2);
			gui.updateDifficultyScaling();
		}
		else if (e == gui.getDSHaz3()) {
			EnemyInformation.setHazardLevel(3);
			gui.updateDifficultyScaling();
		}
		else if (e == gui.getDSHaz4()) {
			EnemyInformation.setHazardLevel(4);
			gui.updateDifficultyScaling();
		}
		else if (e == gui.getDSHaz5()) {
			EnemyInformation.setHazardLevel(5);
			gui.updateDifficultyScaling();
		}
		else if (e == gui.getDSPC1()) {
			EnemyInformation.setPlayerCount(1);
			gui.updateDifficultyScaling();
		}
		else if (e == gui.getDSPC2()) {
			EnemyInformation.setPlayerCount(2);
			gui.updateDifficultyScaling();
		}
		else if (e == gui.getDSPC3()) {
			EnemyInformation.setPlayerCount(3);
			gui.updateDifficultyScaling();
		}
		else if (e == gui.getDSPC4()) {
			EnemyInformation.setPlayerCount(4);
			gui.updateDifficultyScaling();
		}
		
		else if (e == gui.getExportCurrent()) {
			chooseFolder();
			calculator.runTest(false, true);
		}
		else if (e == gui.getExportAll()) {
			chooseFolder();
			int i;
			for (i = 0; i < drillerWeapons.length; i++) {
				calculator.changeWeapon(drillerWeapons[i]);
				calculator.runTest(false, true);
			}
			for (i = 0; i < engineerWeapons.length; i++) {
				calculator.changeWeapon(engineerWeapons[i]);
				calculator.runTest(false, true);
			}
			for (i = 0; i < gunnerWeapons.length; i++) {
				calculator.changeWeapon(gunnerWeapons[i]);
				calculator.runTest(false, true);
			}
			for (i = 0; i < scoutWeapons.length; i++) {
				calculator.changeWeapon(scoutWeapons[i]);
				calculator.runTest(false, true);
			}
		}
		
		else if (e == gui.getMiscScreenshot()) {
			chooseFolder();
			String weaponPackage = currentlySelectedWeapon.getDwarfClass();
			String weaponClassName = currentlySelectedWeapon.getSimpleName();
			String filePath = calculator.getCSVFolderPath() + "\\" + weaponPackage + "_" + weaponClassName + "_" + currentlySelectedWeapon.getCombination() +".png";
			
			// Sourced from https://stackoverflow.com/a/44019372
			BufferedImage screenshot = gui.getScreenshot();
			try {
				ImageIO.write(screenshot, "png", new File(filePath));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		else if (e == gui.getMiscExport()) {
			String combination = currentlySelectedWeapon.getCombination();
			JTextField output = new JTextField(combination);
			output.setFont(new Font("Monospaced", Font.PLAIN, 18));
			
			// Adapted from https://stackoverflow.com/a/13760416 and https://www.tutorialspoint.com/how-to-display-a-jframe-to-the-center-of-a-screen-in-java
			JOptionPane a = new JOptionPane(output, JOptionPane.INFORMATION_MESSAGE);
			JDialog d = a.createDialog(null, "Current weapon combination:");
			d.setLocationRelativeTo(gui);
			d.setVisible(true);
		}
		else if (e == gui.getMiscLoad()) {
			String instructions = "Enter the combination you want to load for this weapon. It should consist of 5 capital letters, A-C, and 1 number, 1-7. Each capital letter "
					+ "corresponds to a mod tier and the number corresponds to the desired overclock. If you do not want to use a mod tier or overclock, substitute the "
					+ "corresponding character with a hyphen.";
			instructions = HoverText.breakLongToolTipString(instructions, 90);
			String newCombination = JOptionPane.showInputDialog(gui, instructions);
			currentlySelectedWeapon.buildFromCombination(newCombination);
		}
		else if (e == gui.getMiscSuggestion()) {
			openWebpage("https://github.com/phg49389/drg-weapons-calculator/issues/new/choose");
		}
	}
	
	// These methods sourced from https://stackoverflow.com/a/10967469
	private static boolean openWebpage(URI uri) {
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(uri);
	            return true;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    return false;
	}
	private static boolean openWebpage(URL url) {
	    try {
	        return openWebpage(url.toURI());
	    } catch (URISyntaxException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	private static boolean openWebpage(String url) {
		try {
			return openWebpage(new URL(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
