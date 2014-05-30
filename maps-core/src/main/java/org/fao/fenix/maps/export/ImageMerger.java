/*
 */

package org.fao.fenix.maps.export;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.ImageIcon;

/**
 *
 * @author etj
 */
public class ImageMerger {

	public static BufferedImage merge(List images) {
//		List<Float> opacity = new ArrayList<Float>(images.size());
//		for(int i = 0; i < images.size(); i++) {
//			opacity.add(new Float(i==0? 1f : 0.9f));
//		}
		
		return merge(images, new ArrayList<Float>(images.size()));
	}
	
	/**
	 * Merges the image listed in <I>images</I>, each with the related <I>opacityList</I>.
	 * <I>images</I> items can be Images or String (representing the file path of the image).
	 * Images and Strings can also be mixed in the List.
	 * the higher in the image stack it will be printed.
	 */
	public static BufferedImage merge(List images, List<Float> opacityList) {
		BufferedImage dest = null;
		Graphics2D destG = null;
		int rule; // This is SRC for the top image, and DST_OVER for the other ones
		float alpha;
		// This is 1.0 for the bottom image, and 0.9 for the other ones

		for(int i = 0,  size = images.size(); i < size; i++) {
			Object o = images.get(i);
			Image image;
			if(o instanceof String) {
				String filename = (String) o;
				image = new ImageIcon(filename).getImage();
			} else if(o instanceof Image) {
				image = (Image) o;
			} else {
				throw new IllegalArgumentException(o + " is not an image");
			}

			rule = AlphaComposite.SRC_OVER; // Default value

//			System.out.println("ADDING " + o + " --> " + opacityList.get(i));

			// Set alpha
			Float opacity = opacityList.get(i);
			if(opacity == null)
				opacity = new Float(i==0? 1f : 0.9f); // no opacity requested: set the default
			
			alpha = opacity.floatValue();
			//			alpha = 0.9F; 	// Light transparence effect
			//			alpha = 1F; 	// Solid colors

			if(i == 0) {
				//- init
				dest = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
//				dest = new BufferedImage(300, 200, BufferedImage.TYPE_INT_ARGB);
				destG = dest.createGraphics();

				//- values for top image
				rule = AlphaComposite.SRC; // Rule for 1st image
			}

//			System.out.println("i: " + i + "; alpha: " + alpha); // DEBUG
			destG.setComposite(AlphaComposite.getInstance(rule, alpha));
			destG.drawImage(image, 0, 0, null);
		}

		return dest;
	}
	
	public static BufferedImage merge(List images, List<Float> opacityList, int width, int height) {
		
		System.out.println("ImageMerger @ 88 - " + images.size() + " images received");
		for (Object o : images)
			System.out.println("ImageMerger @ 88 - \t" + o.toString());
		System.out.println("ImageMerger @ 88 - " + opacityList.size() + " opacities received");
//		for (Float f : opacityList)
//			System.out.println("ImageMerger @ 88 - \t" + f.toString());
		
		BufferedImage dest = null;
		Graphics2D destG = null;
		int rule;
		float alpha;
		for(int i = 0,  size = images.size(); i < size; i++) {
			Object o = images.get(i);
			Image image;
			if(o instanceof String) {
				String filename = (String) o;
				image = new ImageIcon(filename).getImage();
			} else if(o instanceof Image) {
				image = (Image) o;
			} else {
				throw new IllegalArgumentException(o + " is not an image");
			}
			rule = AlphaComposite.SRC_OVER;
			Float opacity = opacityList.get(i);
			if(opacity == null)
				opacity = new Float(i==0? 1f : 0.9f);
			alpha = opacity.floatValue();
			if(i == 0) {
				dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				destG = dest.createGraphics();
				rule = AlphaComposite.SRC; // Rule for 1st image
			}
			destG.setComposite(AlphaComposite.getInstance(rule, alpha));
			destG.drawImage(image, 0, 0, null);
		}
		return dest;
	}

	public static BufferedImage merge(String base, String over, int x, int y) {
		Image ibase = new ImageIcon(base).getImage();
		Image iover = new ImageIcon(over).getImage();
		return merge(ibase, iover, x, y);
	}

	public static BufferedImage merge(String base, Image over, int x, int y) {
		return merge(base, over, x, y, 1.0f);
	}

	public static BufferedImage merge(String base, Image over, int x, int y, float alpha) {
		Image ibase = new ImageIcon(base).getImage();
		return merge(ibase, over, x, y, alpha);
	}

	public static BufferedImage merge(Image base, Image over, int x, int y) {
		return merge(base, over, x, y, 1.0f);
	}

	public static BufferedImage merge(Image base, Image over, int x, int y, float alpha) {
		int bw = base.getWidth(null);
		int bh = base.getHeight(null);
		int ow = over.getWidth(null);
		int oh = over.getHeight(null);

		BufferedImage dest = new BufferedImage(bw, bh, BufferedImage.TYPE_INT_ARGB);
		Graphics2D destG = dest.createGraphics();

		destG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, alpha));
		destG.drawImage(base, 0, 0, null);

		System.out.println("Compositing images (" + ow + "," + oh + ") over (" + bw + "," + bh + ") @" + x + "+" + y);

		// negative position starts from lower right corner, with -1 being aligned to the border
		if(x < 0) {
			x = bw - ow + x + 1;
		}

		if(y < 0) {
			y = bh - oh + y + 1;
		}

		System.out.println("                   @" + x + "+" + y);
		destG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		destG.drawImage(over, x, y, null);

		return dest;
	}

	/**
	 * You cannot convert to JPEG an image with an alpha channel.
	 */
	public static BufferedImage dealpha(BufferedImage bi) {
		return dealpha(bi, Color.white);
	}

	/**
	 * You cannot convert to JPEG an image with an alpha channel.
	 */
	public static BufferedImage dealpha(BufferedImage bi, Color background) {
		BufferedImage ret =
			new BufferedImage(
			bi.getWidth(),
			bi.getHeight(),
			BufferedImage.TYPE_INT_RGB);
		Graphics2D retG = ret.createGraphics();
		retG.setColor(background);
		retG.fillRect(0, 0, bi.getWidth(), bi.getHeight());
		retG.drawImage(bi, 0, 0, null);
		return ret;
	}


	public static void main(String args[]) {
		if(args.length < 2) {
			System.out.println("Usage: ImageMerger destFile srcFile1 ...\n");
			System.exit(1);
		}

		List inFiles = new Vector(args.length - 1);
		for(int i = 1; i < args.length; i++) {
			inFiles.add(args[i]);
		}

		//mergeAndSave(inFiles, null, args[0], GIF);
		System.exit(0);
	}
}
