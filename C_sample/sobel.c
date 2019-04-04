#include <stdio.h>                          /* Sobel.c */
#include <math.h>
#include <stdlib.h>

        int pic[256][256];
        int outpicx[256][256];
        int outpicy[256][256];
        int maskx[3][3] = {{-1,0,1},{-2,0,2},{-1,0,1}};
        int masky[3][3] = {{1,2,1},{0,0,0},{-1,-2,-1}};
        double ival[256][256],maxival;

int main(int argc, char **argv)
{
    int i,j,p,q,mr,sum1,sum2;
    double uppThresh, lowThresh;
    FILE *fo1, *fo2, *fo3, *fo4, *fo5, *fp1, *fopen();
    char *foobar;

    //open the output pgms
    fo1 = fopen("horizontl.pgm", "wb");
    fo2 = fopen("verticals.pgm", "wb");
    fo3 = fopen("magnitude.pgm", "wb");
    fo4 = fopen("lowThresh.pgm", "wb");
    fo5 = fopen("uppThresh.pgm", "wb");

    // Adds the header needed for pgm files
    fprintf(fo1, "P5\n%d %d\n255\n", 256, 256);
    fprintf(fo2, "P5\n%d %d\n255\n", 256, 256);
    fprintf(fo3, "P5\n%d %d\n255\n", 256, 256);
    fprintf(fo4, "P5\n%d %d\n255\n", 256, 256);
    fprintf(fo5, "P5\n%d %d\n255\n", 256, 256);

    //Does the input file scanning
    argc--; argv++;
    foobar = *argv;
    fp1=fopen(foobar,"rb");

    //Does the small threshold scanning
	argc--; argv++;
	foobar = *argv;
	uppThresh = atof(foobar);

    //Does the bigg threshold scanning
    argc--; argv++;
	foobar = *argv;
	lowThresh = atof(foobar);

    // Removes pgm info specs (first 15 chars, causes image to loop around if not used :( ))
    for (i = 0; i < 15; i++)
        getc(fp1);

    //Scans the values of each pixel for the picture array
    for (i=0;i<256;i++)
    { 
        for (j=0;j<256;j++)
            {
              pic[i][j]  =  getc (fp1);
              pic[i][j]  &= 0377; //need to remove?
            }
    }

    //Uses convo in x/y directions using mask radi
    mr = 1;
    for (i=mr;i<256-mr;i++)
    { 
        for (j=mr;j<256-mr;j++)
      {
         sum1 = 0;
         sum2 = 0;
         for (p=-mr;p<=mr;p++)
         {
            for (q=-mr;q<=mr;q++)
            {
               sum1 += pic[i+p][j+q] * maskx[p+mr][q+mr];
               sum2 += pic[i+p][j+q] * masky[p+mr][q+mr];
            }
         }
         outpicx[i][j] = sum1;
         outpicy[i][j] = sum2;
      }
    }

    //Combines the x/y convos
    maxival = 0;
    for (i=mr;i<256-mr;i++)
    { 
        for (j=mr;j<256-mr;j++)
      {
         ival[i][j]=sqrt((double)((outpicx[i][j]*outpicx[i][j]) +
                                  (outpicy[i][j]*outpicy[i][j])));
         if (ival[i][j] > maxival)
            maxival = ival[i][j];

       }
    }

    //outputs (prints) values to the output pgm files
    for (i=0;i<256;i++)
      { 
        for (j=0;j<256;j++)
        {
            //output x gradient
             fprintf(fo1,"%c",(char)((int)((outpicx[i][j])/maxival * 255)));

             //output y gradient
             fprintf(fo2,"%c",(char)((int)((outpicy[i][j])/maxival * 255)));

             //to output (no thresholds) for magnitude
             ival[i][j] = (ival[i][j] / maxival) * 255;            
             fprintf(fo3,"%c",(char)((int)(ival[i][j])));

            //Upper/Lower threshold limit prints
            if(ival[i][j] > lowThresh)
                fprintf(fo4, "%c", (char)255);
            else
                fprintf(fo4, "%c", (char)0);

            if(ival[i][j] > uppThresh)
                fprintf(fo5, "%c", (char)255);
            else
                fprintf(fo5, "%c", (char)0);
         
        }
      }

    fclose(fo1);
    fclose(fo2);
    fclose(fo3);
    fclose(fo4);
    fclose(fo5);
    fclose(fp1);

    return 0;

}
