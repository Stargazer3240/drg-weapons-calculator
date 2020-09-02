package guiPieces;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import modelPieces.DoTInformation;
import modelPieces.Mod;
import modelPieces.Overclock;
import modelPieces.StatsRow;
import modelPieces.Weapon;
import utilities.MathUtils;

public class WeaponTab extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private Weapon myWeapon;
	
	public WeaponTab(Weapon inputWeapon) {
		// Start by initializing the parent JPanel
		super();
		
		myWeapon = inputWeapon;
		
		this.setOpaque(false);
		
		// Set up the percentage-based layout
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		this.setLayout(gbl);
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 24;
		gbc.weightx = 1.0/7.0;
		gbc.weighty = 24.0/31.0;
		JPanel weaponStats = constructWeaponStatsPanel();
		gbl.setConstraints(weaponStats, gbc);
		this.add(weaponStats);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 6;
		gbc.gridheight = 15;
		gbc.weightx = 6.0/7.0;
		gbc.weighty = 15.0/31.0;
		JPanel weaponMods = constructModsPanel();
		gbl.setConstraints(weaponMods, gbc);
		this.add(weaponMods);
		
		gbc.gridx = 1;
		gbc.gridy = 15;
		gbc.gridwidth = 6;
		gbc.gridheight = 7;
		gbc.weightx = 6.0/7.0;
		gbc.weighty = 7.0/31.0;
		JPanel weaponOverclocks = constructOverclocksPanel();
		gbl.setConstraints(weaponOverclocks, gbc);
		this.add(weaponOverclocks);
		
		gbc.gridx = 1;
		gbc.gridy = 22;
		gbc.gridwidth = 6;
		gbc.gridheight = 2;
		gbc.weightx = 6.0/7.0;
		gbc.weighty = 2.0/31.0;
		JPanel statusEffectButtons = constructStatusEffectsPanel();
		gbl.setConstraints(statusEffectButtons, gbc);
		this.add(statusEffectButtons);
		
		// Place the calculated values in the bottom one-quarter
		gbc.gridx = 0;
		gbc.gridy = 24;
		gbc.gridwidth = 7;
		gbc.gridheight = 6;
		gbc.weightx = 1.0;
		gbc.weighty = 7.0/31.0;
		JPanel weaponCalculations = constructCalculationsPanel();
		gbl.setConstraints(weaponCalculations, gbc);
		this.add(weaponCalculations);
	}
	
	// TODO: at some point down the line I'd like to add the picture for each weapon somewhere, but for now I'm leaving this snippet unused.
	private JPanel constructWeaponPicturePanel() {
		JPanel toReturn = new JPanel();
		toReturn.setLayout(new GridLayout(0, 1));
		toReturn.setBackground(GuiConstants.drgBackgroundBrown);
		toReturn.setBorder(GuiConstants.blackLine);
		
		toReturn.add(new WeaponImagePanel(myWeapon.getPicture()));
		
		return toReturn;
	}
	
	private JPanel constructStatusEffectsPanel() {
		boolean[] currentStatusEffects = myWeapon.getCurrentStatusEffects();
		
		JPanel toReturn = new JPanel();
		toReturn.setBackground(GuiConstants.drgBackgroundBrown);
		toReturn.setBorder(GuiConstants.blackLine);
		toReturn.setLayout(new GridLayout(1, 5));
		
		JLabel title = new JLabel("Toggle Status Effects:", SwingConstants.CENTER);
		title.setFont(GuiConstants.customFontHeader);
		title.setForeground(GuiConstants.drgRegularOrange);
		toReturn.add(title);
		
		StatusEffectButton burning = new StatusEffectButton(myWeapon, 0, "Burning", "Enemies that are Burning take an average of " + MathUtils.round(DoTInformation.Burn_DPS, GuiConstants.numDecimalPlaces) + " Fire Damage per second", 
				ButtonIcons.statusEffectIcons.fire, currentStatusEffects[0]);
		StatusEffectButton frozen = new StatusEffectButton(myWeapon, 1, "Frozen", "Enemies that are Frozen cannot move, take 3x Direct Damage, normal Area Damage, but no Weakpoint Bonuses can be applied.", 
				ButtonIcons.statusEffectIcons.frozen, currentStatusEffects[1]);
		StatusEffectButton electrocuted = new StatusEffectButton(myWeapon, 2, "Electrocuted", "Enemies that are Electrocuted take an average of " + MathUtils.round(DoTInformation.Electro_DPS, GuiConstants.numDecimalPlaces) + " Electric Damage per second and are slowed by 80%", 
				ButtonIcons.statusEffectIcons.electricity, currentStatusEffects[2]);
		StatusEffectButton IFG = new StatusEffectButton(myWeapon, 3, "IFG Grenade", "Scout's IFG Grenade slows all enemy movement by 75% and increases the damage dealt to enemies by 30%.", 
				ButtonIcons.statusEffectIcons.electricity, currentStatusEffects[3]);
		
		toReturn.add(burning);
		toReturn.add(frozen);
		toReturn.add(electrocuted);
		toReturn.add(IFG);
		
		return toReturn;
	}
	
	private JPanel constructWeaponStatsPanel() {
		StatsRow[] weaponStats = myWeapon.getStats();
		
		JPanel toReturn = new JPanel();
		toReturn.setBackground(GuiConstants.drgBackgroundBrown);
		toReturn.setBorder(GuiConstants.blackLine);
		toReturn.setLayout(new BoxLayout(toReturn, BoxLayout.Y_AXIS));
		toReturn.setPreferredSize(new Dimension(200, 280));
		
		JPanel row, statLabelIcon;
		JLabel statLabel, statValue;
		String statLabelText;
		for (int i = 0; i < weaponStats.length; i++) {
			if (weaponStats[i].shouldBeDisplayed()) {
				row = new JPanel();
				row.setOpaque(false);
				row.setLayout(new BorderLayout());
				
				statLabelIcon = new StatsRowIconPanel(ButtonIcons.getModIcon(weaponStats[i].getIcon(), false));
				row.add(statLabelIcon, BorderLayout.LINE_START);
				
				statLabelText = weaponStats[i].getName();
				statLabelText = HoverText.breakLongToolTipString(statLabelText, 28);
				statLabel = new JLabel(statLabelText);
				statLabel.setFont(GuiConstants.customFont);
				statLabel.setForeground(Color.white);
				row.add(statLabel, BorderLayout.CENTER);
				
				statValue = new JLabel(weaponStats[i].getValue());
				statValue.setFont(GuiConstants.customFontBold);
				if (weaponStats[i].shouldValueBeHighlighted()) {
					statValue.setForeground(GuiConstants.drgHighlightedYellow);
				}
				else {
					statValue.setForeground(GuiConstants.drgRegularOrange);
				}
				row.add(statValue, BorderLayout.LINE_END);
				
				row.setBorder(new EmptyBorder(0, GuiConstants.paddingPixels, 0, 2*GuiConstants.paddingPixels));
				
				toReturn.add(row);
			}
		}
		
		return toReturn;
	}
	
	private JPanel constructModsPanel() {
		JPanel toReturn = new JPanel();
		toReturn.setBackground(GuiConstants.drgBackgroundBiege);
		toReturn.setBorder(GuiConstants.blackLine);
		toReturn.setLayout(new GridLayout(5, 3));
		
		Mod[] weaponMods;
		ModButton mb;
		int i, j;
		for (i = 1; i < 6; i++) {
			weaponMods = myWeapon.getModsAtTier(i);
			for (j = 0; j < weaponMods.length; j++) {
				mb = new ModButton(myWeapon, weaponMods[j]);
				toReturn.add(mb);
			}
			// Check to see if there are only two mods at this tier. If so, add an empty JLabel
			if (weaponMods.length == 2) {
				toReturn.add(new JLabel());
			}
		}
		
		return toReturn;
	}
	
	private JPanel constructOverclocksPanel() {
		Overclock[] weaponOverclocks = myWeapon.getOverclocks();
		
		JPanel toReturn = new JPanel();
		toReturn.setBackground(GuiConstants.drgBackgroundBiege);
		toReturn.setBorder(GuiConstants.blackLine);
		
		// Separate the overclocks into two rows
		int rowWidth = (int) Math.ceil(weaponOverclocks.length / 2.0);
		toReturn.setLayout(new GridLayout(2, rowWidth));
		
		OverclockButton ocb;
		for (int i = 0; i < weaponOverclocks.length; i++) {
			ocb = new OverclockButton(myWeapon, weaponOverclocks[i]);
			toReturn.add(ocb);
		}
		
		// It's ok if there's an odd number of overclocks; the GridLayout will leave any slot as a transparent blank.
		
		return toReturn;
	}
	
	private JPanel constructCalculationsPanel() {
		JPanel toReturn = new JPanel();
		toReturn.setBackground(GuiConstants.drgBackgroundBrown);
		toReturn.setBorder(GuiConstants.blackLine);
		toReturn.setLayout(new GridLayout(8, 5));
		
		// 70px height per row of stats
		
		String[] headers = new String[] {
			// Row 1
			"Burst DPS",
			"Sustained DPS",
			"Toggle Weakpoints in DPS",
			"Toggle Accuracy in DPS",
			"Toggle Armor in DPS",
			// Row 2
			"Additional Targets DPS",
			"Max Num Targets",
			"Max Multi-Target Dmg",
			"Ammo Efficiency",
			"Avg Damage Lost vs Armored Enemies",
			// Row 3
			"Accuracy Visualizer",
			"General Accuracy",
			"Weakpoint Accuracy",
			"Firing Duration (sec)",
			"Avg TTK (sec)",
			// Row 4
			"Avg Overkill",
			"Breakpoints",
			"Utility",
			"",
			""  // This last one, bottom-right, will eventually be "Haz5+ Ready"
		};
		
		int i;
		JLabel header, value;
		String roundedNumber;
		String leftPadSpaces = "  ";
		double[] originalStats = myWeapon.getBaselineStats();
		
		/******************************************
			Row 1
		******************************************/
		for (i = 0; i < headers.length/4; i++) {
			header = new JLabel(headers[i]);
			header.setFont(GuiConstants.customFont);
			header.setForeground(GuiConstants.drgRegularOrange);
			toReturn.add(header);
		}
		
		double idealBurstDPS = myWeapon.calculateIdealBurstDPS();
		roundedNumber = leftPadSpaces + MathUtils.round(idealBurstDPS, GuiConstants.numDecimalPlaces);
		value = new JLabel(roundedNumber);
		value.setFont(GuiConstants.customFontBold);
		if (idealBurstDPS < originalStats[0]) {
			value.setForeground(GuiConstants.drgNegativeChangeRed);
		}
		else if (idealBurstDPS > originalStats[0]) {
			value.setForeground(GuiConstants.drgOverclockCleanGreen);
		}
		else {
			// Implicitly means that they're equal
			value.setForeground(GuiConstants.drgHighlightedYellow);
		}
		toReturn.add(value);
		
		double idealSustainedDPS = myWeapon.calculateIdealSustainedDPS();
		roundedNumber = leftPadSpaces + MathUtils.round(idealSustainedDPS, GuiConstants.numDecimalPlaces);
		value = new JLabel(roundedNumber);
		value.setFont(GuiConstants.customFontBold);
		if (idealSustainedDPS < originalStats[1]) {
			value.setForeground(GuiConstants.drgNegativeChangeRed);
		}
		else if (idealSustainedDPS > originalStats[1]) {
			value.setForeground(GuiConstants.drgOverclockCleanGreen);
		}
		else {
			// Implicitly means that they're equal
			value.setForeground(GuiConstants.drgHighlightedYellow);
		}
		toReturn.add(value);
		
		double sustainedWeakpointDPS = myWeapon.sustainedWeakpointDPS();
		roundedNumber = leftPadSpaces + MathUtils.round(sustainedWeakpointDPS, GuiConstants.numDecimalPlaces);
		value = new JLabel(roundedNumber);
		value.setFont(GuiConstants.customFontBold);
		if (sustainedWeakpointDPS < originalStats[2]) {
			value.setForeground(GuiConstants.drgNegativeChangeRed);
		}
		else if (sustainedWeakpointDPS > originalStats[2]) {
			value.setForeground(GuiConstants.drgOverclockCleanGreen);
		}
		else {
			// Implicitly means that they're equal
			value.setForeground(GuiConstants.drgHighlightedYellow);
		}
		toReturn.add(value);
		
		double sustainedWeakpointAccuracyDPS = myWeapon.sustainedWeakpointAccuracyDPS();
		roundedNumber = leftPadSpaces + MathUtils.round(sustainedWeakpointAccuracyDPS, GuiConstants.numDecimalPlaces);
		value = new JLabel(roundedNumber);
		value.setFont(GuiConstants.customFontBold);
		if (sustainedWeakpointAccuracyDPS < originalStats[3]) {
			value.setForeground(GuiConstants.drgNegativeChangeRed);
		}
		else if (sustainedWeakpointAccuracyDPS > originalStats[3]) {
			value.setForeground(GuiConstants.drgOverclockCleanGreen);
		}
		else {
			// Implicitly means that they're equal
			value.setForeground(GuiConstants.drgHighlightedYellow);
		}
		toReturn.add(value);
		
		double additionalTargetDPS = myWeapon.calculateAdditionalTargetDPS();
		roundedNumber = leftPadSpaces + MathUtils.round(additionalTargetDPS, GuiConstants.numDecimalPlaces);
		value = new JLabel(roundedNumber);
		value.setFont(GuiConstants.customFontBold);
		if (additionalTargetDPS < originalStats[4]) {
			value.setForeground(GuiConstants.drgNegativeChangeRed);
		}
		else if (additionalTargetDPS > originalStats[4]) {
			value.setForeground(GuiConstants.drgOverclockCleanGreen);
		}
		else {
			// Implicitly means that they're equal
			value.setForeground(GuiConstants.drgHighlightedYellow);
		}
		toReturn.add(value);
		
		/******************************************
			Row 2
		******************************************/
		for (i = headers.length/4; i < 2*headers.length/4; i++) {
			header = new JLabel(headers[i]);
			header.setFont(GuiConstants.customFont);
			header.setForeground(GuiConstants.drgRegularOrange);
			toReturn.add(header);
		}
		
		int maxNumTargets = myWeapon.calculateMaxNumTargets();
		int originalNumTargets = (int) originalStats[5];
		if (myWeapon.currentlyDealsSplashDamage()) {
			AoEVisualizerButton valButton = new AoEVisualizerButton(this, leftPadSpaces + maxNumTargets, myWeapon);
			if (maxNumTargets < originalNumTargets) {
				valButton.setForeground(GuiConstants.drgNegativeChangeRed);
			}
			else if (maxNumTargets > originalNumTargets) {
				valButton.setForeground(GuiConstants.drgOverclockCleanGreen);
			}
			else {
				// Implicitly means that they're equal
				valButton.setForeground(GuiConstants.drgHighlightedYellow);
			}
			toReturn.add(valButton);
		}
		else {
			value = new JLabel(leftPadSpaces + maxNumTargets);
			value.setFont(GuiConstants.customFontBold);
			if (maxNumTargets < originalNumTargets) {
				value.setForeground(GuiConstants.drgNegativeChangeRed);
			}
			else if (maxNumTargets > originalNumTargets) {
				value.setForeground(GuiConstants.drgOverclockCleanGreen);
			}
			else {
				// Implicitly means that they're equal
				value.setForeground(GuiConstants.drgHighlightedYellow);
			}
			toReturn.add(value);
		}
		
		double maxMultiDmg = myWeapon.calculateMaxMultiTargetDamage();
		roundedNumber = leftPadSpaces + MathUtils.round(maxMultiDmg, GuiConstants.numDecimalPlaces);
		value = new JLabel(roundedNumber);
		value.setFont(GuiConstants.customFontBold);
		if (maxMultiDmg < originalStats[6]) {
			value.setForeground(GuiConstants.drgNegativeChangeRed);
		}
		else if (maxMultiDmg > originalStats[6]) {
			value.setForeground(GuiConstants.drgOverclockCleanGreen);
		}
		else {
			// Implicitly means that they're equal
			value.setForeground(GuiConstants.drgHighlightedYellow);
		}
		toReturn.add(value);
		
		double ammoEfficiency = myWeapon.ammoEfficiency();
		roundedNumber = leftPadSpaces + MathUtils.round(ammoEfficiency, GuiConstants.numDecimalPlaces);
		value = new JLabel(roundedNumber);
		value.setFont(GuiConstants.customFontBold);
		if (ammoEfficiency < originalStats[7]) {
			value.setForeground(GuiConstants.drgNegativeChangeRed);
		}
		else if (ammoEfficiency > originalStats[7]) {
			value.setForeground(GuiConstants.drgOverclockCleanGreen);
		}
		else {
			// Implicitly means that they're equal
			value.setForeground(GuiConstants.drgHighlightedYellow);
		}
		toReturn.add(value);
		
		double damageWastedByArmor = myWeapon.damageWastedByArmor();
		roundedNumber = leftPadSpaces + MathUtils.round(damageWastedByArmor, GuiConstants.numDecimalPlaces) + "%";
		value = new JLabel(roundedNumber);
		value.setFont(GuiConstants.customFontBold);
		if (damageWastedByArmor > originalStats[15]) {
			value.setForeground(GuiConstants.drgNegativeChangeRed);
		}
		else if (damageWastedByArmor < originalStats[15]) {
			value.setForeground(GuiConstants.drgOverclockCleanGreen);
		}
		else {
			// Implicitly means that they're equal
			value.setForeground(GuiConstants.drgHighlightedYellow);
		}
		toReturn.add(value);
		
		// Placeholder blank JLabel until a new metric gets placed here
		value = new JLabel();
		toReturn.add(value);
		
		/******************************************
			Row 3
		******************************************/
		for (i = 2*headers.length/4; i < 3*headers.length/4; i++) {
			header = new JLabel(headers[i]);
			header.setFont(GuiConstants.customFont);
			header.setForeground(GuiConstants.drgRegularOrange);
			toReturn.add(header);
		}
		
		double generalAccuracy = myWeapon.estimatedAccuracy(false);
		if (generalAccuracy < 0) {
			value = new JLabel(leftPadSpaces + "Manually Aimed");
			value.setFont(GuiConstants.customFontBold);
			value.setForeground(GuiConstants.drgHighlightedYellow);
			toReturn.add(value);
		}
		else {
			// TODO: this is where the visualizer button will go, but that's going to be a whole other thing to do first.
			value = new JLabel(leftPadSpaces + "See how it works");
			value.setFont(GuiConstants.customFontBold);
			value.setForeground(GuiConstants.drgHighlightedYellow);
			toReturn.add(value);
		}
		
		if (generalAccuracy < 0) {
			value = new JLabel(leftPadSpaces + "Manually Aimed");
			value.setFont(GuiConstants.customFontBold);
			value.setForeground(GuiConstants.drgHighlightedYellow);
			toReturn.add(value);
		}
		else {
			roundedNumber = leftPadSpaces + MathUtils.round(generalAccuracy, GuiConstants.numDecimalPlaces) + "%";
			
			AccuracySliderButton accSlideButton = new AccuracySliderButton(this, roundedNumber, myWeapon);
			if (generalAccuracy < originalStats[8]) {
				accSlideButton.setForeground(GuiConstants.drgNegativeChangeRed);
			}
			else if (generalAccuracy > originalStats[8]) {
				accSlideButton.setForeground(GuiConstants.drgOverclockCleanGreen);
			}
			else {
				// Implicitly means that they're equal
				accSlideButton.setForeground(GuiConstants.drgHighlightedYellow);
			}
			toReturn.add(accSlideButton);
		}
		
		double weakpointAccuracy = myWeapon.estimatedAccuracy(true);
		if (weakpointAccuracy < 0) {
			value = new JLabel(leftPadSpaces + "Manually Aimed");
			value.setFont(GuiConstants.customFontBold);
			value.setForeground(GuiConstants.drgHighlightedYellow);
		}
		else {
			roundedNumber = leftPadSpaces + MathUtils.round(weakpointAccuracy, GuiConstants.numDecimalPlaces) + "%";
			value = new JLabel(roundedNumber);
			value.setFont(GuiConstants.customFontBold);
			if (weakpointAccuracy < originalStats[9]) {
				value.setForeground(GuiConstants.drgNegativeChangeRed);
			}
			else if (weakpointAccuracy > originalStats[9]) {
				value.setForeground(GuiConstants.drgOverclockCleanGreen);
			}
			else {
				// Implicitly means that they're equal
				value.setForeground(GuiConstants.drgHighlightedYellow);
			}
		}
		toReturn.add(value);
		
		double firingDuration = myWeapon.calculateFiringDuration();
		roundedNumber = leftPadSpaces + MathUtils.round(firingDuration, GuiConstants.numDecimalPlaces);
		value = new JLabel(roundedNumber);
		value.setFont(GuiConstants.customFontBold);
		if (firingDuration < originalStats[10]) {
			value.setForeground(GuiConstants.drgNegativeChangeRed);
		}
		else if (firingDuration > originalStats[10]) {
			value.setForeground(GuiConstants.drgOverclockCleanGreen);
		}
		else {
			// Implicitly means that they're equal
			value.setForeground(GuiConstants.drgHighlightedYellow);
		}
		toReturn.add(value);
		
		double timeToKill = myWeapon.averageTimeToKill();
		roundedNumber = leftPadSpaces + MathUtils.round(timeToKill, GuiConstants.numDecimalPlaces);
		value = new JLabel(roundedNumber);
		value.setFont(GuiConstants.customFontBold);
		if (timeToKill > originalStats[12]) {
			value.setForeground(GuiConstants.drgNegativeChangeRed);
		}
		else if (timeToKill < originalStats[12]) {
			value.setForeground(GuiConstants.drgOverclockCleanGreen);
		}
		else {
			// Implicitly means that they're equal
			value.setForeground(GuiConstants.drgHighlightedYellow);
		}
		toReturn.add(value);
		
		/******************************************
			Row 4
		******************************************/
		for (i = 3*headers.length/4; i < headers.length; i++) {
			header = new JLabel(headers[i]);
			header.setFont(GuiConstants.customFont);
			header.setForeground(GuiConstants.drgRegularOrange);
			toReturn.add(header);
		}
		
		double overkill = myWeapon.averageOverkill();
		roundedNumber = leftPadSpaces + MathUtils.round(overkill, GuiConstants.numDecimalPlaces) + "%";
		value = new JLabel(roundedNumber);
		value.setFont(GuiConstants.customFontBold);
		if (overkill > originalStats[11]) {
			value.setForeground(GuiConstants.drgNegativeChangeRed);
		}
		else if (overkill < originalStats[11]) {
			value.setForeground(GuiConstants.drgOverclockCleanGreen);
		}
		else {
			// Implicitly means that they're equal
			value.setForeground(GuiConstants.drgHighlightedYellow);
		}
		toReturn.add(value);
		
		int breakpoints = myWeapon.breakpoints();
		roundedNumber = leftPadSpaces + breakpoints;
		if (breakpoints > 0) {
			BreakpointsButton breakButton = new BreakpointsButton(this, roundedNumber, myWeapon);
			if (breakpoints > originalStats[13]) {
				breakButton.setForeground(GuiConstants.drgOverclockUnstableRed);
			}
			else if (breakpoints < originalStats[13]) {
				breakButton.setForeground(GuiConstants.drgOverclockCleanGreen);
			}
			else {
				// Implicitly means that they're equal
				breakButton.setForeground(GuiConstants.drgHighlightedYellow);
			}
			toReturn.add(breakButton);
		}
		else {
			// Flamethrower and Cryo Cannon don't have the Breakpoint metric calculated
			value = new JLabel(leftPadSpaces + "Not needed");
			value.setFont(GuiConstants.customFontBold);
			value.setForeground(GuiConstants.drgHighlightedYellow);
			toReturn.add(value);
		}
		
		double utility = myWeapon.utilityScore();
		roundedNumber = leftPadSpaces + MathUtils.round(utility, GuiConstants.numDecimalPlaces);
		UtilityBreakdownButton utilButton = new UtilityBreakdownButton(this, roundedNumber, myWeapon);
		if (utility < originalStats[14]) {
			utilButton.setForeground(GuiConstants.drgOverclockUnstableRed);
		}
		else if (utility > originalStats[14]) {
			utilButton.setForeground(GuiConstants.drgOverclockCleanGreen);
		}
		else {
			// Implicitly means that they're equal
			utilButton.setForeground(GuiConstants.drgHighlightedYellow);
		}
		toReturn.add(utilButton);
		
		// Placeholder blank JLabel until a new metric gets placed here
		value = new JLabel();
		toReturn.add(value);
		
		// Placeholder blank JLabel until a new metric gets placed here
		value = new JLabel();
		toReturn.add(value);
		
		return toReturn;
	}
}
