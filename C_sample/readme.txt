# The Sobel Edge Detector

Sobel.c takes an input pgm file and computes the edges using Sobel. 

Input: PGM file
Output: 1) X Gradient (horizontl.pgm)
		2) Y Gradient (verticals.pgm)
		3) Magnitude  (magnitude.pgm
		4) lower threshold (uppThresh.pgm)
		5) upper threshold (lowThresh.pgm)

To run the program:
* Open a console at the location of "sobel.c"
* Compile the code with "gcc -o sobel sobel.c"
* Execute code with "./sobel (inputfile in PGM) (upper threshold in INT) (lower threshold in INT)

I used "./sobel input/face05.pgm 50 30" for testing.
