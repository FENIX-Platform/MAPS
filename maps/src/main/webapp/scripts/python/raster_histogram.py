#!/usr/bin/python

from osgeo import gdal, ogr, osr
import matplotlib.pyplot as plt # not used yet
import numpy
import sys, getopt


def calculate_histogram( input_value_raster):
    ds = gdal.Open( input_value_raster )

    hist = ds.GetRasterBand(1).GetDefaultHistogram()

    histjson = {}

    '''print('Min: ', hist[0])
    print('Max: ', hist[1])
    print('Buckets: ', hist[2])
    print('Histogram: ', hist[3])'''

    dfMin = hist[0]
    dfMax = hist[1]
    nBucketCount = hist[2]
    panHistogram = hist[3]

    print( "  %d buckets from %g to %g:" % ( nBucketCount, dfMin, dfMax ))
    line = '  '
    panHistogramLenght = len(panHistogram)
    print panHistogram[290]

    #for i in range(0,256):
    #    print panHistogram[i]


    #for bucket in panHistogram:
    for bucket in panHistogram:
        line = line + ("%d " % bucket)
        #print("%d " % bucket)


    print(line)

    return hist


def main(argv):
    src_ds = ''
    try:
        opt, args = getopt.getopt(argv,"hi:o:")
        # print 'args file is "', args
    except getopt.GetoptError:
        sys.exit(2)

    return calculate_histogram(args[0])

if __name__ == "__main__":
   main(sys.argv[1:])