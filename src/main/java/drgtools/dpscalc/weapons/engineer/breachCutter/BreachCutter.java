package drgtools.dpscalc.weapons.engineer.breachCutter;

import drgtools.dpscalc.dataGenerator.DatabaseConstants;
import drgtools.dpscalc.guiPieces.GuiConstants;
import drgtools.dpscalc.guiPieces.WeaponPictures;
import drgtools.dpscalc.guiPieces.customButtons.ButtonIcons.modIcons;
import drgtools.dpscalc.guiPieces.customButtons.ButtonIcons.overclockIcons;
import drgtools.dpscalc.modelPieces.DoTInformation;
import drgtools.dpscalc.modelPieces.EnemyInformation;
import drgtools.dpscalc.modelPieces.Mod;
import drgtools.dpscalc.modelPieces.Overclock;
import drgtools.dpscalc.modelPieces.StatsRow;
import drgtools.dpscalc.modelPieces.UtilityInformation;
import drgtools.dpscalc.utilities.MathUtils;
import drgtools.dpscalc.weapons.Weapon;

// Breach Cutter doesn't gain damage from Frozen, and only its Damage per Tick gets boosted by Weakpoint damage. Impact Damage is unaffected when hitting weakpoint.
public class BreachCutter extends Weapon {
	
	/****************************************************************************************
	* Class Variables
	****************************************************************************************/
	
	private double projectileVelocity;
	protected double burstDamageOnFirstImpact;
	protected double damageTickRate;
	private double damagePerTick;
	private double delayBeforeOpening;
	private double projectileLifetime;
	private double projectileWidth;
	private int magazineSize;
	private int carriedAmmo;
	private double rateOfFire;
	private double reloadTime;
	
	/****************************************************************************************
	* Constructors
	****************************************************************************************/
	
	// Shortcut constructor to get baseline data
	public BreachCutter() {
		this(-1, -1, -1, -1, -1, -1);
	}
	
	// Shortcut constructor to quickly get statistics about a specific build
	public BreachCutter(String combination) {
		this(-1, -1, -1, -1, -1, -1);
		buildFromCombination(combination);
	}
	
	public BreachCutter(int mod1, int mod2, int mod3, int mod4, int mod5, int overclock) {
		fullName = "Breach Cutter";
		weaponPic = WeaponPictures.breachCutter;
		
		// Base stats, before mods or overclocks alter them:
		projectileVelocity = 10;  // m/sec
		// In the game files this is listed as "Burn" damage, which translates to Fire Element damage in this program's terminology
		burstDamageOnFirstImpact = 50;
		damageTickRate = 50;  // ticks/sec
		damagePerTick = 11.5;
		delayBeforeOpening = 0.2;
		projectileLifetime = 1.5;
		projectileWidth = 1.5;
		magazineSize = 3;
		carriedAmmo = 12;
		rateOfFire = 1.5;
		reloadTime = 3.4;
		
		initializeModsAndOverclocks();
		// Grab initial values before customizing mods and overclocks
		setBaselineStats();
		
		// Selected Mods
		selectedTier1 = mod1;
		selectedTier2 = mod2;
		selectedTier3 = mod3;
		selectedTier4 = mod4;
		selectedTier5 = mod5;
		
		// Overclock slot
		selectedOverclock = overclock;
	}
	
	@Override
	protected void initializeModsAndOverclocks() {
		tier1 = new Mod[2];
		tier1[0] = new Mod("Prolonged Power Generation", "+1.5 Projectile Lifetime", modIcons.hourglass, 1, 0);
		tier1[1] = new Mod("High Capacity Magazine", "+3 Magazine Size", modIcons.magSize, 1, 1);
		
		tier2 = new Mod[3];
		tier2[0] = new Mod("Expanded Ammo Bags", "+6 Max Ammo", modIcons.carriedAmmo, 2, 0);
		tier2[1] = new Mod("Condensed Plasma", "+3.5 Damage per Tick", modIcons.directDamage, 2, 1);
		tier2[2] = new Mod("Loosened Node Cohesion", "+1.5m Plasma Beam Width", modIcons.aoeRadius, 2, 2);
		
		tier3 = new Mod[2];
		// Although getStats() shows this change, it has no effect on any numbers in this model. As such, I'm marking as "not modeled".
		tier3[0] = new Mod("Quick Deploy", "-0.2 Plasma Expansion Delay", modIcons.duration, 3, 0, false);
		tier3[1] = new Mod("Improved Case Ejector", "-0.6 Reload Time", modIcons.reloadSpeed, 3, 1);
		
		tier4 = new Mod[2];
		tier4[0] = new Mod("Armor Breaking", "+200% Armor Breaking", modIcons.armorBreaking, 4, 0);
		tier4[1] = new Mod("Disruptive Frequency Tuning", "+100% Stun Chance, 3 sec Stun duration", modIcons.stun, 4, 1);
		
		tier5 = new Mod[3];
		tier5[0] = new Mod("Explosive Goodbye", "After firing a line and a 0.4 second delay, the player can press the fire button again to manually detonate the line dealing 40 Explosive element Area Damage in a 3.5m radius "
				+ "and leaving behind a 3.25m radius sphere of Persistent Plasma that does an average of " + MathUtils.round(DoTInformation.Plasma_EPC_DPS, GuiConstants.numDecimalPlaces) + " Fire Damage per second and slows enemies by 20% for 4.6 seconds. "
				+ "If the player doesn't detonate it manually, the line explodes at the end of its lifetime.", modIcons.addedExplosion, 5, 0);
		tier5[1] = new Mod("Plasma Trail", "Leaves behind a Persistent Plasma field that does an average of " + MathUtils.round(DoTInformation.Plasma_Trail_DPS, GuiConstants.numDecimalPlaces) + " Fire Damage per second for 4.6 seconds "
				+ "along the entire length of the line's path", modIcons.areaDamage, 5, 1);
		// Since the additional lines neither increase targets hit nor DPS per target, I'm marking it as "not modeled"
		tier5[2] = new Mod("Triple Split Line", "Adds a line above and below the primary projectile (multiple lines hitting doesn't increase DPS)", modIcons.aoeRadius, 5, 2, false);
		
		overclocks = new Overclock[7];
		overclocks[0] = new Overclock(Overclock.classification.clean, "Light-Weight Cases", "+3 Max Ammo, -0.2 Reload Time", overclockIcons.carriedAmmo, 0);
		// Roll Control has no effect on DPS stats, so it gets marked as "not modeled"
		overclocks[1] = new Overclock(Overclock.classification.clean, "Roll Control", "Holding down the trigger after the line leaves the gun causes the line to start rolling at 300 degrees per second. On release of the trigger, the line stops rolling.", overclockIcons.rollControl, 1, false);
		overclocks[2] = new Overclock(Overclock.classification.clean, "Stronger Plasma Current", "+1 Damage per Tick, +0.5 Projectile Lifetime", overclockIcons.directDamage, 2);
		overclocks[3] = new Overclock(Overclock.classification.balanced, "Return to Sender", "Holding down the trigger after line leaves the gun activates a remote connection, which on release of the trigger causes "
				+ "the line to change direction and move back towards the gun. In exchange, -6 Max Ammo", overclockIcons.returnToSender, 3);
		overclocks[4] = new Overclock(Overclock.classification.balanced, "High Voltage Crossover", "100% chance to electrocute enemies, which deals an average of " + MathUtils.round(4.0 * DoTInformation.Electro_TicksPerSec, GuiConstants.numDecimalPlaces) + " Electric Damage per "
				+ "Second for 4 seconds. In exchange, x0.67 Magazine Size.", overclockIcons.electricity, 4);
		overclocks[5] = new Overclock(Overclock.classification.unstable, "Spinning Death", "Instead of flying in a straight line, the projectile now rotates 2 times per second about the Yaw axis. Additionally: x0.05 Projectile Velocity, x0 Impact Damage, "
				+ "x2.5 Projectile Lifetime, x0.24 Damage per Tick, +1.5m Plasma Beam Width, x0.5 Max Ammo, and x0.33 Magazine Size", overclockIcons.special, 5);
		overclocks[6] = new Overclock(Overclock.classification.unstable, "Inferno", "The first time the beam hits an enemy, it inflicts 75 Heat and applies a DoT that does 7 Fire Damage and 7 Heat at a rate of 2 ticks/sec for 5 seconds (does 11 ticks total). "
				+ "Additionally, it converts 90% of the Damage per Tick from Electric element to Fire element and adds the amount converted as Heat per tick. In exchange: -3.5 Damage per Tick and x0.25 Armor Breaking", overclockIcons.heatDamage, 6);
		
		// This boolean flag has to be set to True in order for Weapon.isCombinationValid() and Weapon.buildFromCombination() to work.
		modsAndOCsInitialized = true;
	}
	
	@Override
	public BreachCutter clone() {
		return new BreachCutter(selectedTier1, selectedTier2, selectedTier3, selectedTier4, selectedTier5, selectedOverclock);
	}
	
	public String getDwarfClass() {
		return "Engineer";
	}
	public String getSimpleName() {
		return "BreachCutter";
	}
	public int getDwarfClassID() {
		return DatabaseConstants.engineerCharacterID;
	}
	public int getWeaponID() {
		return DatabaseConstants.breachCutterGunsID;
	}
	
	/****************************************************************************************
	* Setters and Getters
	****************************************************************************************/
	
	protected double getProjectileVelocity() {
		double toReturn = projectileVelocity;
		
		// Spinning Death makes it move a lot slower
		if (selectedOverclock == 5) {
			toReturn *= 0.05;
		}
		
		return toReturn;
	}
	protected double getImpactDamage() {
		if (selectedOverclock == 5) {
			return 0.0;
		}
		else {
			return burstDamageOnFirstImpact;
		}
	}
	protected double getDamagePerTick() {
		double toReturn = damagePerTick;
		
		if (selectedTier2 == 1) {
			toReturn += 3.5;
		}
		
		if (selectedOverclock == 2) {
			toReturn += 1.0;
		}
		else if (selectedOverclock == 5) {
			toReturn *= 0.24;
		}
		else if (selectedOverclock == 6) {
			toReturn -= 3.5;
		}
		
		return toReturn;
	}
	protected double getDelayBeforeOpening() {
		double toReturn = delayBeforeOpening;
		
		if (selectedTier3 == 0) {
			toReturn -= 0.2;
		}
		
		return toReturn;
	}
	protected double getProjectileLifetime() {
		double toReturn = projectileLifetime;
		
		if (selectedTier1 == 0) {
			toReturn += 1.5;
		}
		
		if (selectedOverclock == 2) {
			toReturn += 0.5;
		}
		else if (selectedOverclock == 5) {
			toReturn *= 2.5;
		}
		
		return toReturn;
	}
	protected double getProjectileWidth() {
		double toReturn = projectileWidth;
		
		if (selectedTier2 == 2) {
			toReturn += 1.5;
		}
		if (selectedOverclock == 5) {
			toReturn += 1.5;
		}
		
		return toReturn;
	}
	protected int getMagazineSize() {
		int toReturn = magazineSize;
		
		if (selectedTier1 == 1) {
			toReturn += 3;
		}
		
		if (selectedOverclock == 4) {
			toReturn = (int) Math.round(toReturn * 2.0 / 3.0);
		}
		else if (selectedOverclock == 5) {
			toReturn /= 3;
		}
		
		return toReturn;
	}
	protected int getCarriedAmmo() {
		int toReturn = carriedAmmo;
		
		if (selectedTier2 == 0) {
			toReturn += 6;
		}
		
		if (selectedOverclock == 0) {
			toReturn += 3;
		}
		else if (selectedOverclock == 3) {
			toReturn -= 6;
		}
		else if (selectedOverclock == 5) {
			toReturn /= 2;
		}
		
		return toReturn;
	}
	public double getRateOfFire() {
		// OC "Return to Sender" changes max RoF from 1.5 to 1/(2/3 * Lifetime)
		if (selectedOverclock == 3) {
			// This assumes that people let go of the trigger at the two-thirds distance
			return 3.0 / (2.0 * getProjectileLifetime());
		}
		else {
			return rateOfFire;
		}
	}
	protected double getReloadTime() {
		double toReturn = reloadTime;
		
		if (selectedTier3 == 1) {
			toReturn -= 0.6;
		}
		if (selectedOverclock == 0) {
			toReturn -= 0.2;
		}
		
		return toReturn;
	}
	protected double getArmorBreaking() {
		double toReturn = 1.0;
		
		if (selectedTier4 == 0) {
			toReturn += 2.0;
		}
		
		if (selectedOverclock == 6) {
			toReturn /= 4.0;
		}
		
		return toReturn;
	}
	
	@Override
	public StatsRow[] getStats() {
		StatsRow[] toReturn = new StatsRow[15];
		
		toReturn[0] = new StatsRow("Burst Damage on First Impact:", getImpactDamage(), modIcons.areaDamage, selectedOverclock == 5);
		
		boolean dmgPerTickModified = selectedTier2 == 1 || selectedOverclock == 2 || selectedOverclock == 5 || selectedOverclock == 6;
		toReturn[1] = new StatsRow("Damage per Tick:", getDamagePerTick(), modIcons.directDamage, dmgPerTickModified);
		
		toReturn[2] = new StatsRow("Damage Ticks per Second:", damageTickRate, modIcons.blank, false);
		
		toReturn[3] = new StatsRow("Projectile Width:", getProjectileWidth(), modIcons.aoeRadius, selectedTier2 == 2 || selectedOverclock == 5);
		
		toReturn[4] = new StatsRow("Projectile Velocity (m/sec):", getProjectileVelocity(), modIcons.projectileVelocity, selectedOverclock == 5);
		
		toReturn[5] = new StatsRow("Delay Before Opening:", getDelayBeforeOpening(), modIcons.duration, selectedTier3 == 0);
		
		boolean lifetimeModified = selectedTier1 == 0 || selectedOverclock == 2 || selectedOverclock == 5;
		toReturn[6] = new StatsRow("Projectile Lifetime (sec):", getProjectileLifetime(), modIcons.hourglass, lifetimeModified);
		
		toReturn[7] = new StatsRow("Avg Damage per Projectile to Single Grunt:", calculateAverageDamagePerGrunt(true, true, false, true), modIcons.special, false);
		
		boolean magSizeModified = selectedTier1 == 1 || selectedOverclock == 4 || selectedOverclock == 5;
		toReturn[8] = new StatsRow("Magazine Size:", getMagazineSize(), modIcons.magSize, magSizeModified);
		
		boolean carriedAmmoModified = selectedTier2 == 0 || selectedOverclock == 0 || selectedOverclock == 3 || selectedOverclock == 5;
		toReturn[9] = new StatsRow("Max Ammo:", getCarriedAmmo(), modIcons.carriedAmmo, carriedAmmoModified);
		
		toReturn[10] = new StatsRow("Rate of Fire:", getRateOfFire(), modIcons.rateOfFire, selectedOverclock == 3);
		
		toReturn[11] = new StatsRow("Reload Time:", getReloadTime(), modIcons.reloadSpeed, selectedTier3 == 1 || selectedOverclock == 0);
		
		boolean armorBreakingModified = selectedTier4 == 0 || selectedOverclock == 6;
		toReturn[12] = new StatsRow("Armor Breaking:", convertDoubleToPercentage(getArmorBreaking()), modIcons.armorBreaking, armorBreakingModified, armorBreakingModified);
		
		boolean stunEquipped = selectedTier4 == 1;
		toReturn[13] = new StatsRow("Stun Chance:", convertDoubleToPercentage(1.0), modIcons.homebrewPowder, stunEquipped, stunEquipped);
		
		toReturn[14] = new StatsRow("Stun Duration:", 3, modIcons.stun, stunEquipped, stunEquipped);
		
		return toReturn;
	}
	
	/****************************************************************************************
	* Other Methods
	****************************************************************************************/
	
	protected double calculateGruntIntersectionTimePerRegularProjectile() {
		double secondsOfIntersection = (2.0 * EnemyInformation.GlyphidGruntBodyAndLegsRadius) / getProjectileVelocity();
		if (selectedOverclock == 3) {
			// OC "Return to Sender" doubles how long a single projectile can intersect a single target
			secondsOfIntersection *= 2.0;
		}
		
		return secondsOfIntersection;
	}
	
	// This method isn't perfect but it's a good start. It should eventually model how the enemies move instead of stand still and work out a couple of math/logic overlaps that I'm choosing to neglect for right now.
	protected double calculateAverageGruntIntersectionTimePerSpinningDeathProjectile() {
		double sdRotationSpeed = 4 * Math.PI;  // Equals 2 full circles per second
		double sdProjectileVelocity = getProjectileVelocity();
		double sdWidth = getProjectileWidth();
		double sdLifetime = getProjectileLifetime();
		
		double R = sdWidth / 2.0;
		double r = EnemyInformation.GlyphidGruntBodyAndLegsRadius;
		
		double maxNumHitsDownDiameter = ((sdRotationSpeed / Math.PI) / sdProjectileVelocity) * sdWidth;  // 8*w
		double avgNumHitsDownChords = ((sdRotationSpeed / Math.PI) / sdProjectileVelocity) * ((Math.PI * Math.pow(R, 2)) / sdWidth);  // 2Pi*w
		
		// I'm choosing to model this as if the Spinning Death projectile is centered on (0, 0) and doesn't move, and a Grunt is moving through its damage area at the Projectile Velocity. It helps simplify the math a little bit.
		double horizontalOffsetFromCenterForRepresentativeChord = (Math.sqrt(Math.pow(maxNumHitsDownDiameter, 2) - Math.pow(avgNumHitsDownChords, 2)) / maxNumHitsDownDiameter) * R; // 0.3095*w
		double representativeChordLength = 2.0 * Math.sqrt(Math.pow(R, 2));
		double verticalOffsetForCenterOfGrunt = Math.sqrt(Math.pow(R + r, 2) - Math.pow(horizontalOffsetFromCenterForRepresentativeChord, 2));
		
		double totalNumSecondsThatSpinningDeathIntersectsGrunt = 0.0;
		double distanceBetweenCirclesCenters, radiansAngleOfIntersection, lensChordLength, lengthOfTangentSegment;
		
		double timeElapsed = 0.0;
		double timeInterval = 1.0 / (sdRotationSpeed / Math.PI);
		double totalDistanceTraveledVertically = 0.0;
		double distanceMovedPerInterval = sdProjectileVelocity * timeInterval;
		while (timeElapsed < sdLifetime && totalDistanceTraveledVertically < (representativeChordLength + 2 * r)) {
			/*
				As the Grunt moves through the Spinning Death projectile, there are 4 states of intersection: 
					1. If the two centers are further apart than their combined radii, then there's no overlap.
					2. When the center of Grunt is still outside the SD circle, the area intersected is a Lens (like AccuracyEstimator) and the angle of rotation intersected is 
						proportional to the chord length across the Lens. Find the chord, translate it to arc length of the SD projectile, and find the radians. Divide radians by rotational speed.
					3. When the center of the Grunt is inside the SD circle but it's far enough away that the center of SD isn't yet inside the Grunt's circle. The angle of intersection is the angle
						between the two lines that intersect at SD's center and are both tangent to Grunt's circle. Divide radians by rotational speed.
					4. When the center of SD is inside Grunt's circle, then angle of intersection is technically infinite. For this case, just add the full timeInterval and move on to the next loop.
			*/
			distanceBetweenCirclesCenters = Math.sqrt(Math.pow(horizontalOffsetFromCenterForRepresentativeChord, 2) + Math.pow(verticalOffsetForCenterOfGrunt, 2));
			
			// Case 1: No overlap
			if (distanceBetweenCirclesCenters >= R + r) {
				// Do nothing, just move onto next loop
			}
			
			// Case 2: Lens
			else if (distanceBetweenCirclesCenters >= R && distanceBetweenCirclesCenters < R + r) {
				/*
					This is by far the most complicated case to calculate. Because we know that this case is a Lens, there's a formula to find the length of the chord shared by the two circles
					inside the Lens. Using that chord length and some more geometry, we can calculate the angle of intersection
				*/
				// Sourced from https://mathworld.wolfram.com/Circle-CircleIntersection.html
				lensChordLength = (1.0 / distanceBetweenCirclesCenters) * Math.sqrt((-distanceBetweenCirclesCenters + r - R) * (-distanceBetweenCirclesCenters - r + R) * (-distanceBetweenCirclesCenters + r + R) * (distanceBetweenCirclesCenters + r + R));
				radiansAngleOfIntersection = 2.0 * Math.asin(lensChordLength / (2.0 * R));
				totalNumSecondsThatSpinningDeathIntersectsGrunt += radiansAngleOfIntersection / sdRotationSpeed;
			}
			
			// Case 3: Tangents
			else if (distanceBetweenCirclesCenters >= r && distanceBetweenCirclesCenters < R) {
				/*
					Because Tangents are by definition at right-angles to the center of the circle, and we know the lengths of the two radii, we can use simple trigonometry to calculate
					the angle of intersection.
				*/
				lengthOfTangentSegment = Math.sqrt(Math.pow(distanceBetweenCirclesCenters, 2) - Math.pow(r, 2));
				radiansAngleOfIntersection = 2.0 * Math.atan(r / lengthOfTangentSegment);
				totalNumSecondsThatSpinningDeathIntersectsGrunt += radiansAngleOfIntersection / sdRotationSpeed;
			}
			
			// Case 4: Complete overlap
			else if (distanceBetweenCirclesCenters < r) {
				totalNumSecondsThatSpinningDeathIntersectsGrunt += timeInterval;
			}
			
			timeElapsed += timeInterval;
			totalDistanceTraveledVertically += distanceMovedPerInterval;
			verticalOffsetForCenterOfGrunt -= distanceMovedPerInterval;
		}
		
		return totalNumSecondsThatSpinningDeathIntersectsGrunt;
	}
	
	/*
		I want this method to model the DPS of the projectile as it passes through the entirety of a single grunt. This means
		modeling the impact damage, the listed DPS, the DoTs, and the explosion from Explosive Goodbye.
	*/
	protected double calculateAverageDamagePerGrunt(boolean extendDoTsBeyondIntersection, boolean primaryTarget, boolean weakpoint, boolean ignoreStatusEffects) {
		double intersectionTime;
		if (selectedOverclock == 5) {
			intersectionTime = calculateAverageGruntIntersectionTimePerSpinningDeathProjectile();
		}
		else {
			intersectionTime = calculateGruntIntersectionTimePerRegularProjectile();
		}
		
		double impactDamage = getImpactDamage();
		double dmgPerTick = getDamagePerTick();
		double explosiveGoodbyeDmg = 0;
		if (selectedTier5 == 0) {
			explosiveGoodbyeDmg = 40.0;
			if (!primaryTarget) {
				explosiveGoodbyeDmg *= aoeEfficiency[1];
			}
		}
		
		if (!ignoreStatusEffects) {
			// None of Breach Cutter's damage benefits from enemies being Frozen.
			
			// IFG Grenade
			if (statusEffects[3]) {
				dmgPerTick *= UtilityInformation.IFG_Damage_Multiplier;
				impactDamage *= UtilityInformation.IFG_Damage_Multiplier;
				explosiveGoodbyeDmg *= UtilityInformation.IFG_Damage_Multiplier;
			}
			
			// Weakpoint doesn't apply when enemies are Frozen
			if (weakpoint && !statusEffects[1]) {
				// Only the Dmg/Tick benefits from Weakpoints
				dmgPerTick *= EnemyInformation.averageWeakpointDamageIncrease();
			}
		}
		else {
			if (weakpoint) {
				dmgPerTick *= EnemyInformation.averageWeakpointDamageIncrease();
			}
		}
		
		double baseDamage = impactDamage + intersectionTime * damageTickRate * dmgPerTick + explosiveGoodbyeDmg;
		
		double burnDamage = 0;
		// If Frozen, then they can't Burn. However, the logic gets tricky when trying to ignore Status Effects like Frozen for max damage calculations.
		if ((selectedOverclock == 6 && ignoreStatusEffects) || (selectedOverclock == 6 && !ignoreStatusEffects && !statusEffects[1])) {
			/* 
				OC "Inferno" adds 3 different Heat sources:
				1. 75 Heat Damage in a single burst
				2. A hidden DoT that does 7 Fire + 7 Heat per tick, 2 ticks/sec, 5 sec duration (11 ticks)
				3. 90% of the Dmg/Tick as Heat while intersecting enemies
				
				As a result of this, for every enemy except Oppressor the Burn DoT is extended by 5 sec (because 14 heat/sec > cooling rate) and they take 14 additional DPS during those 5 sec
			*/
			
			double ignitionTime = averageTimeToCauterize();
			double burnDoTDuration;
			if (extendDoTsBeyondIntersection) {
				burnDoTDuration = DoTInformation.Burn_SecsDuration + 5.0;
				burnDamage = DoTInformation.Burn_DPS * burnDoTDuration + 11.0 * 7.0;  // Add the 11 ticks of 7 Fire Damage
			}
			else {
				burnDoTDuration = intersectionTime - ignitionTime;
				burnDamage = burnDoTDuration * DoTInformation.Burn_DPS + intersectionTime * 14.0;
			}
		}
		
		double electrocuteDamage = 0;
		if (selectedOverclock == 4) {
			double electrocuteDoTDuration;
			if (extendDoTsBeyondIntersection) {
				// OC "High Voltage Crossover" has an increased duration of 4 sec
				electrocuteDoTDuration = 4.0;
			}
			else {
				electrocuteDoTDuration = intersectionTime;
			}
			
			// OC "High Voltage Crossover" also has an increased damage of 4 Damage/tick
			electrocuteDamage = 4 * DoTInformation.Electro_TicksPerSec * electrocuteDoTDuration;
		}
		
		double plasmaDamage = 0;
		if (selectedTier5 == 0 || selectedTier5 == 1) {
			double plasmaDoTDuration, plasmaDPS;
			if (selectedTier5 == 0) {
				// 3.25m radius, Grunts move at 2.9 m/sec, and U34 Persistent Plasma slows by 20%
				plasmaDoTDuration = 3.25 / (2.9 * 0.8);
				plasmaDPS = DoTInformation.Plasma_EPC_DPS;
			}
			else if (selectedTier5 == 1) {
				// I'm estimating that Grunts will walk out of the Persistent Plasma trail in about 2 seconds
				plasmaDoTDuration = 2.0;
				plasmaDPS = DoTInformation.Plasma_Trail_DPS;
			}
			else {
				plasmaDoTDuration = 0;
				plasmaDPS = 0;
			}
			
			if (!extendDoTsBeyondIntersection) {
				// Because intersectionTime takes into account both Spinning Death and Return to Sender, I shouldn't have to worry about them here.
				plasmaDoTDuration = intersectionTime;
			}
			
			plasmaDamage = plasmaDPS * plasmaDoTDuration;
		}
		
		return baseDamage + burnDamage + electrocuteDamage + plasmaDamage;
	}
	
	@Override
	public boolean currentlyDealsSplashDamage() {
		// Breach Cutter sometimes deals Splash damage for Explosive Goodbye
		return selectedTier5 == 0;
	}
	
	@Override
	protected void recalculateAoEEfficiency() {
		// According to GreyHound, Explosive Goodbye does 40 Explosive Damage in a 3.5m radius, 2.5m Full Damage radius, 50% Falloff.
		// This is only used in calculateAverageDamagePerGrunt(), when the 40 damage gets multiplied by the 0.8772 efficiency.
		aoeEfficiency = calculateAverageAreaDamage(3.5, 2.5, 0.5);
	}
	
	// Single-target calculations
	private double calculateSingleTargetDPS(boolean burst, boolean primaryTarget, boolean weakpoint) {
		double damagePerProjectileToSingleGrunt = calculateAverageDamagePerGrunt(false, primaryTarget, weakpoint, false);
		double dmgPerMag = damagePerProjectileToSingleGrunt * getMagazineSize();
		
		double duration;
		// Special case when OC "Spinning Death" is equipped and T1.B more mag size isn't equipped, the mag size is 1.
		if (selectedOverclock == 5 && selectedTier1 != 1) {
			duration = getReloadTime();
		}
		else if (burst) {
			duration = getMagazineSize() / getRateOfFire();
		}
		else {
			duration = getMagazineSize() / getRateOfFire() + getReloadTime();
		}
		
		double baseDPS = dmgPerMag / duration;
		
		double burnDPS = 0;
		// Frozen negates the Burn DoT
		if (selectedOverclock == 6 && !statusEffects[1]) {
			// Because OC "Inferno" ignites all enemies just so dang fast, I'm choosing to over-estimate the Burn DPS for bursts as if they ignite instantly.
			// Additionally, add the hidden DoT's 14 DPS for 5 seconds
			burnDPS = DoTInformation.Burn_DPS + 14.0;
		}
		
		double electroDPS = 0;
		if (selectedOverclock == 4) {
			// OC "High Voltage Crossover" has an increased damage of 4 dmg/tick
			electroDPS = 4.0 * DoTInformation.Electro_TicksPerSec;
		}
		
		double plasmaDPS = 0;
		if (selectedTier5 == 0) {
			plasmaDPS = DoTInformation.Plasma_EPC_DPS;
		}
		else if (selectedTier5 == 1) {
			plasmaDPS = DoTInformation.Plasma_Trail_DPS;
		}
		
		return baseDPS + burnDPS + electroDPS + plasmaDPS;
	}
	
	@Override
	public double calculateSingleTargetDPS(boolean burst, boolean weakpoint, boolean accuracy, boolean armorWasting) {
		return calculateSingleTargetDPS(burst, true, weakpoint);
	}

	// Multi-target calculations
	@Override
	public double calculateAdditionalTargetDPS() {
		return calculateSingleTargetDPS(false, false, false);
	}

	@Override
	public double calculateMaxMultiTargetDamage() {
		return calculateMaxNumTargets() * calculateAverageDamagePerGrunt(true, true, false, true) * (getMagazineSize() + getCarriedAmmo());
	}

	@Override
	public int calculateMaxNumTargets() {
		int numGruntsHitSimultaneouslyPerRow;
		double width = getProjectileWidth();
		double velocity = getProjectileVelocity();
		double lifetime = getProjectileLifetime();
		if (selectedOverclock == 5) {
			numGruntsHitSimultaneouslyPerRow = calculateNumGlyphidsInRadius(width / 2.0);
		}
		else {
			// ArcticEcho recommended (width + 1) as an estimate for average number of enemies hit by a line simultaneously
			numGruntsHitSimultaneouslyPerRow = (int) (width + 1);
		}
		
		int numRowsOfGruntsHitDuringProjectileLifetime = (int) Math.ceil((velocity / (4.0 * EnemyInformation.GlyphidGruntBodyAndLegsRadius)) * lifetime);
		
		// System.out.println("Num grunts per row: " + numGruntsHitSimultaneouslyPerRow + ", Num rows of grunts: " + numRowsOfGruntsHitDuringProjectileLifetime);
		
		return numGruntsHitSimultaneouslyPerRow * numRowsOfGruntsHitDuringProjectileLifetime;
	}

	@Override
	public double calculateFiringDuration() {
		int magSize = getMagazineSize();
		int carriedAmmo = getCarriedAmmo();
		double timeToFireMagazine = ((double) magSize) / getRateOfFire();
		return numMagazines(carriedAmmo, magSize) * timeToFireMagazine + numReloads(carriedAmmo, magSize) * getReloadTime();
	}
	
	@Override
	protected double averageDamageToKillEnemy() {
		// Yes extend DoT durations, yes primary target, no weakpoint
		double dmgPerShot = calculateAverageDamagePerGrunt(true, true, false, true);
		return Math.ceil(EnemyInformation.averageHealthPool() / dmgPerShot) * dmgPerShot;
	}
	
	@Override
	public double averageOverkill() {
		overkillPercentages = EnemyInformation.overkillPerCreature(calculateAverageDamagePerGrunt(true, true, false, true));
		return MathUtils.vectorDotProduct(overkillPercentages[0], overkillPercentages[1]);
	}

	@Override
	public double estimatedAccuracy(boolean weakpointAccuracy) {
		// Breach Cutter can't be aimed like normal weapons
		return -1;
	}
	
	@Override
	public int breakpoints() {
		// I'm not sure if Breakpoints is needed for Breach Cutter or not... but as it stands it would be impossible to model without knowing the dimensions of all the other creatures
		return 0;
	}

	@Override
	public double utilityScore() {
		// Light Armor Breaking probability
		utilityScores[2] = calculateProbabilityToBreakLightArmor(getDamagePerTick(), getArmorBreaking()) * UtilityInformation.ArmorBreak_Utility;
		
		int maxNumTargets = calculateMaxNumTargets();
		
		// Slow
		// Baseline BC slows enemies by 70% while in contact.
		double intersectionTime;
		if (selectedOverclock == 5) {
			intersectionTime = calculateAverageGruntIntersectionTimePerSpinningDeathProjectile();
		}
		else {
			intersectionTime = calculateGruntIntersectionTimePerRegularProjectile();
		}
		utilityScores[3] = maxNumTargets * intersectionTime * 0.7;
		
		// T5.A "Explosive Goodbye"
		if (selectedTier5 == 0) {
			// U34 added a 20% Slow (x0.8 Movespeed) to the Persistent Plasma sphere. At 2.9 m/sec, it should take Grunts 3.25/(2.9*0.8) ~ 1.4 seconds to leave the sphere
			utilityScores[3] += calculateNumGlyphidsInRadius(3.25) * (3.25/(2.9*0.8)) * 0.2;
		}
		
		// OC "High Voltage Contact"
		if (selectedOverclock == 4) {
			// OC "High Voltage Crossover" applies an Electrocute DoT that slows movement by 80% for 4 seconds
			utilityScores[3] += maxNumTargets * 4.0 * UtilityInformation.Electrocute_Slow_Utility;
		}
		
		// Stun
		// T4.B has a 100% chance to stun for 3 seconds
		if (selectedTier4 == 1) {
			utilityScores[5] = maxNumTargets * 3.0 * UtilityInformation.Stun_Utility;
		}
		else {
			utilityScores[5] = 0;
		}
		
		return MathUtils.sum(utilityScores);
	}
	
	@Override
	public double averageTimeToCauterize() {
		if (selectedOverclock == 6) {
			/* 
				OC "Inferno" adds 3 different Heat sources:
				1. 75 Heat Damage in a single burst
				2. A hidden DoT that does 7 Fire + 7 Heat per tick, 2 ticks/sec, 5 sec duration (11 ticks)
				3. 90% of the Dmg/Tick as Heat while intersecting enemies
			*/
			return EnemyInformation.averageTimeToIgnite(75, 0.9 * getDamagePerTick(), damageTickRate, 7.0 * 2);
		}
		else {
			return -1;
		}
	}
	
	@Override
	public double damagePerMagazine() {
		return calculateMaxNumTargets() * calculateAverageDamagePerGrunt(true, true, false, true) * getMagazineSize();
	}
	
	@Override
	public double timeToFireMagazine() {
		int magSize = getMagazineSize();
		if (magSize > 1) {
			return magSize / getRateOfFire();
		}
		else {
			// Spinning Death without T2.B Mag Size only has one shot before reloading, so much like the Grenade Launcher its time to fire magazine would be zero.
			return 0;
		}
	}
	
	@Override
	public double damageWastedByArmor() {
		return 0;
	}
}
