package drgtools.dpscalc.utilities;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import net.ifok.image.image4j.codec.ico.ICODecoder;

public class ResourceLoader {
	
	// Generic method to load any resource
	// Sourced from https://www.youtube.com/watch?v=rCoed3MKpEA
	public static InputStream load(String path) {
		InputStream input = ResourceLoader.class.getResourceAsStream(path);
		if (input == null) {
			input = ResourceLoader.class.getResourceAsStream("/" + path);
		}
		return input;
	}
	
	// Use this method for .ico files specifically
	public static List<BufferedImage> loadIcoFile(String relativeFilepath){
		List<BufferedImage> toReturn = null;
		try {
			toReturn = ICODecoder.read(load(relativeFilepath));
		}
		catch (IOException e) {
			
		}
		return toReturn;
	}
	
	// Use this method for virtually every other image type
	public static BufferedImage loadImage(String relativeFilepath) {
		BufferedImage toReturn = null;
		try {
			toReturn = ImageIO.read(load(relativeFilepath));
		}
		catch (IOException e) {
			
		}
		return toReturn;
	}
}
