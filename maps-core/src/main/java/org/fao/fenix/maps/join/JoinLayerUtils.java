package org.fao.fenix.maps.join;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.fao.fenix.maps.bean.map.join.JoinLayer;
import org.fao.fenix.maps.constants.CLASSIFICATION;
import org.fao.fenix.maps.sld.SLDColors;
import org.fao.fenix.maps.sld.SLDCreator;
import org.fao.fenix.maps.sld.legend.LegendCreator;
import org.fao.fenix.maps.util.MapUtils;

public class JoinLayerUtils {

	private static final Logger LOGGER = Logger.getLogger(JoinLayerUtils.class);

	
	public String createJoinSLD(String layername, String stylesPath, JoinLayer joinLayer, String sldStyleName) {
		LOGGER.info("createJoinSLD");

        CLASSIFICATION c = CLASSIFICATION.valueOf(joinLayer.getClassification());

//		LOGGER.info("intervals: " + joinLayer.getIntervals());
		Boolean customEqual = false;
		switch (c) {
			case quantile: joinLayer.setRanges(SLDCreator.getQuantiles(joinLayer.getJoindata(), joinLayer.getIntervals(), joinLayer.getDecimalNumbers())); break;
			case equalinterval: joinLayer.setRanges(SLDCreator.getEqualIntervals(joinLayer.getJoindata(), joinLayer.getIntervals(), joinLayer.getDecimalNumbers())); break;
			case equalarea: joinLayer.setRanges(SLDCreator.getEqualAreas(joinLayer.getJoindata(), joinLayer.getAreas(), joinLayer.getIntervals(), joinLayer.getDecimalNumbers())); break;
			case custom: /** the ranges are already set **/ break;
			case customequal: /** the ranges are already set **/customEqual = true; break;
		}

		// here do the colors with the right ranges
		if ( joinLayer.getColors() == null) {
			if ( joinLayer.getRanges().size() == 1)
				joinLayer.setColors(getColors(joinLayer, 1));
			else {
				joinLayer.setColors(getColors(joinLayer, (joinLayer.getRanges().size()+ 1)));
			}
		}
			
//		LOGGER.info("COLORS: " + joinLayer.getColors());
        if ( sldStyleName == null )
		    sldStyleName = MapUtils.getRandomName();
		
		String sld = "";
		/**  TODO: fix for the FSD **/
//		LOGGER.info("customEqual: " + customEqual);
		if ( !customEqual ) {
			sld = SLDCreator.createSLD(layername, sldStyleName, sldStyleName,joinLayer.getJoincolumn(), joinLayer.getJoindata(), joinLayer.getRanges(), joinLayer.getColors(), joinLayer.isAddBorders(), joinLayer.getBordersColor(), joinLayer.getBordersStroke(), joinLayer.getBordersOpacity(), joinLayer.getThousandSeparator(), joinLayer.getDecimalSeparator());
			joinLayer.getLegend().setHtml(LegendCreator.createVerticalLegend(joinLayer.getLegend().getTitle(), joinLayer.getRanges(), joinLayer.getColors(), joinLayer.getLegend().getPosition(), joinLayer.getThousandSeparator(), joinLayer.getDecimalSeparator(), joinLayer.getJoindata().size(), joinLayer.getLegend().getLegendInfo()));
		}
		else {
			sld = SLDCreator.createSLDCustomEqual(layername, sldStyleName, sldStyleName,joinLayer.getJoincolumn(), joinLayer.getJoindata(), joinLayer.getRanges(), joinLayer.getColors(), joinLayer.isAddBorders(), joinLayer.getBordersColor(), joinLayer.getBordersStroke(), joinLayer.getBordersOpacity());
			joinLayer.getLegend().setHtml(LegendCreator.createVerticalLegendEqual(joinLayer.getLegend().getTitle(), joinLayer.getRanges(), joinLayer.getColors(), joinLayer.getLegend().getPosition(), joinLayer.getThousandSeparator(), joinLayer.getDecimalSeparator(), joinLayer.getJoindata().size()));
		}
		
		String sldFilename = sldStyleName + ".sld";
		createSLDFile(sldFilename, stylesPath, sld);
		
		return sldFilename;
	} 
	
	/**
	 * This methos set the radious of the points
	 *
	 * @param joinLayer
	 * @return
	 */
	public void createPoints(JoinLayer joinLayer) {
		CLASSIFICATION c = CLASSIFICATION.valueOf(joinLayer.getClassification());
		// setting colors

		switch (c) {
			case quantile: joinLayer.setRanges(SLDCreator.getQuantiles(joinLayer.getJoindata(), joinLayer.getIntervals(), joinLayer.getDecimalNumbers())); break;
			case equalinterval: joinLayer.setRanges(SLDCreator.getEqualIntervals(joinLayer.getJoindata(), joinLayer.getIntervals(), joinLayer.getDecimalNumbers())); break;
			case equalarea: joinLayer.setRanges(SLDCreator.getEqualAreas(joinLayer.getJoindata(), joinLayer.getAreas(), joinLayer.getIntervals(), joinLayer.getDecimalNumbers())); break;
			case custom: /** the ranges are already set **/ break;
			case customequal: /** the ranges are already set **/ break;
		}
		
		if ( joinLayer.getColors() == null) {
			joinLayer.setColors(getColors(joinLayer));
		}
		createPointRadius(joinLayer);
	} 
	
	private void createPointRadius(JoinLayer joinLayer) {
		List<Double> values = joinLayer.getRanges();
		for(String key: joinLayer.getJoindata().keySet()) {
			Double value = joinLayer.getJoindata().get(key);
			Integer classRange = null;
			for (int i = 0; i < values.size(); i++) {
				if ( i == 0 ) {
					if (value < values.get(i)) {
						classRange = i;
						break;
					}
				}
				else {
					if (value <= values.get(i)) {
						classRange = i;
						break;
					}
				}
			}
			if (classRange == null) {
				classRange = values.size();
			}
			classRange++;
			
			try {
				joinLayer.getJoininfo().get(key).setRadius(Double.valueOf(2 + (classRange* 2.5)));		
			}catch (Exception e) {}
		}
	}
	
	public List<Double> getQuantilesIntervals(Map<String, Double> joindata, Integer intervals, Integer decimalNumbers) {
		return SLDCreator.getQuantiles(joindata, intervals, decimalNumbers);
	}
	
	private List<String> getColors(JoinLayer joinLayer, Integer intervals) {
		List<String> values = SLDColors.getColorsPalette(joinLayer.getColorramp(), intervals, joinLayer.isColorreverse());
		return values;
	}

	private List<String> getColors(JoinLayer joinLayer) {
		List<String> values = new ArrayList<String>();
		if ( joinLayer.getIntervals() != null ) {
			values = SLDColors.getColorsPalette(joinLayer.getColorramp(), joinLayer.getIntervals(), joinLayer.isColorreverse());
		}
		if ( joinLayer.getRanges() != null ){
			// +1 it's because it's needed one more color per interval
			values = SLDColors.getColorsPalette(joinLayer.getColorramp(), joinLayer.getRanges().size() + 1, joinLayer.isColorreverse());
		}
		else{
			System.out.println("error on getting colors: no intervals and no ranges set");
		}
		
		return values;
	}
	
	private void createSLDFile(String filename, String stylesPath, String sld) {
		String fullpath = stylesPath + File.separator + filename;
		// Create file
		try {
			FileWriter fstream = new FileWriter(fullpath);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(sld);
			// Close the output stream
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public String createJoinSLDREST(String layername, String stylesPath, JoinLayer joinLayer, String sldStyleName) {
        LOGGER.info("createJoinSLD");

        CLASSIFICATION c = CLASSIFICATION.valueOf(joinLayer.getClassification());

//		LOGGER.info("intervals: " + joinLayer.getIntervals());
        Boolean customEqual = false;
        switch (c) {
            case quantile: joinLayer.setRanges(SLDCreator.getQuantiles(joinLayer.getJoindata(), joinLayer.getIntervals(), joinLayer.getDecimalNumbers())); break;
            case equalinterval: joinLayer.setRanges(SLDCreator.getEqualIntervals(joinLayer.getJoindata(), joinLayer.getIntervals(), joinLayer.getDecimalNumbers())); break;
            case equalarea: joinLayer.setRanges(SLDCreator.getEqualAreas(joinLayer.getJoindata(), joinLayer.getAreas(), joinLayer.getIntervals(), joinLayer.getDecimalNumbers())); break;
            case custom: /** the ranges are already set **/ break;
            case customequal: /** the ranges are already set **/
                customEqual = true;
                break;
        }

        // here do the colors with the right ranges
        if ( joinLayer.getColors() == null) {
            if ( joinLayer.getRanges().size() == 1)
                joinLayer.setColors(getColors(joinLayer, 1));
            else {
                joinLayer.setColors(getColors(joinLayer, (joinLayer.getRanges().size()+ 1)));
            }
        }

        // check if sldStyleName is null
        sldStyleName  = ( sldStyleName == null )? MapUtils.getRandomName(): sldStyleName;

        String sld = "";
        /**  TODO: fix for the FSD **/
        if ( !customEqual ) {
            sld = SLDCreator.createSLD(layername, sldStyleName, sldStyleName,joinLayer.getJoincolumn(), joinLayer.getJoindata(), joinLayer.getRanges(), joinLayer.getColors(), joinLayer.isAddBorders(), joinLayer.getBordersColor(), joinLayer.getBordersStroke(), joinLayer.getBordersOpacity(), joinLayer.getThousandSeparator(), joinLayer.getDecimalSeparator());
        }
        else {
            sld = SLDCreator.createSLDCustomEqual(layername, sldStyleName, sldStyleName,joinLayer.getJoincolumn(), joinLayer.getJoindata(), joinLayer.getRanges(), joinLayer.getColors(), joinLayer.isAddBorders(), joinLayer.getBordersColor(), joinLayer.getBordersStroke(), joinLayer.getBordersOpacity());
        }

        String sldFilename = sldStyleName + ".sld";
        createSLDFile(sldFilename, stylesPath, sld);

        return sldFilename;
    }


    /**
     * This methos set the radious of the points
     *
     * @param joinLayer
     * @return
     */
    public void createPointsREST(JoinLayer joinLayer) {
        CLASSIFICATION c = CLASSIFICATION.valueOf(joinLayer.getClassification());
        // setting colors

        switch (c) {
            case quantile: joinLayer.setRanges(SLDCreator.getQuantiles(joinLayer.getJoindata(), joinLayer.getIntervals(), joinLayer.getDecimalNumbers())); break;
            case equalinterval: joinLayer.setRanges(SLDCreator.getEqualIntervals(joinLayer.getJoindata(), joinLayer.getIntervals(), joinLayer.getDecimalNumbers())); break;
            case equalarea: joinLayer.setRanges(SLDCreator.getEqualAreas(joinLayer.getJoindata(), joinLayer.getAreas(), joinLayer.getIntervals(), joinLayer.getDecimalNumbers())); break;
            case custom: /** the ranges are already set **/ break;
            case customequal: /** the ranges are already set **/ break;
        }

        if ( joinLayer.getColors() == null) {
            joinLayer.setColors(getColors(joinLayer));
        }
        createPointRadius(joinLayer);
    }
}
