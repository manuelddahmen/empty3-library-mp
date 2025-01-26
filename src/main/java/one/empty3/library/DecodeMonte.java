/*package one.empty3.library;


import one.empty3.libs.Image;
import ru.sbtqa.monte.media.*;
import ru.sbtqa.monte.media.image.Images;
import ru.sbtqa.monte.media.FormatKeys.*;
import java.io.*;
import one.empty3.libs.Image;

import one.empty3.libs.*;
import java.awt.geom.Line2D;
import one.empty3.libs.Image;
import java.awt.image.IndexColorModel;
import java.io.*;
import java.util.Random;
import ru.sbtqa.monte.media.Format;
import ru.sbtqa.monte.media.FormatKeys.MediaType;
import static ru.sbtqa.monte.media.VideoFormatKeys.*;
import ru.sbtqa.monte.media.VideoFormatKeys.PixelFormat;
import ru.sbtqa.monte.media.avi.AVIReader;
import ru.sbtqa.monte.media.avi.AVIWriter;
import ru.sbtqa.monte.media.math.Rational;
public class DecodeMonte extends VideoDecoder {
 
 public DecodeMonte(File file, TextureMov refTextureMov) {
        super(file, refTextureMov);
    }

 
 
 
 public void run() {
try{

         testReading(file);
 } catch(IOException ex) {
      ex.printStackTrace();
}
 /*

 try {
    if(!file.exists())
       throw new NullPointerException("file not found exception"+file.getCanonicalPath());
 MovieReader in = Registry.getInstance().getReader(file);
if(in==null)
      throw new NullPointerException("in moviereader"+file.getCanonicalPath());
 //ArrayList<Image> frames=new ArrayList<Image> ();
 
 Format format = new Format(VideoFormatKeys.DataClassKey, Image.class);
 int track = in.findTrack(0, new Format(FormatKeys.MediaTypeKey,MediaType.VIDEO));
 Codec codec=Registry.getInstance().getCodec(in.getFormat(track), format);
 
 Buffer inbuf = new Buffer();
 Buffer codecbuf = new Buffer();
 do {
  if(size()>MAXSIZE)
   try {
    Thread.sleep(100);
    } catch(InterruptedException ex) {
   
   }
 in.read(track, inbuf);
 codec.process(inbuf, codecbuf);
 if (!codecbuf.isFlag(BufferFlag.DISCARD)) {
 imgBuf.add(new one.empty3.libs.Image(Images.cloneImage((Image)codecbuf.data))) ;
 
 }
 
 } while (!inbuf.isFlag(BufferFlag.END_OF_MEDIA));
 } catch(IOException ex) {
  ex.printStackTrace();
}36
/* finally {
 in.close();
 }
// return frames.toArray(new Image[frames.size()]);
 
}





    public void tests(String[] args) {
        //Logger.getAnonymousLogger().log(Level.INFO, "AVIDemo " + DecodeMonte.class.getPackage().getImplementationVersion());
        Logger.getAnonymousLogger().log(Level.INFO, "This is a demo of the Monte Media library.");
        Logger.getAnonymousLogger().log(Level.INFO, "Copyright © Werner Randelshofer. All Rights Reserved.");
        Logger.getAnonymousLogger().log(Level.INFO, "License: Creative Commons Attribution 3.0.");
        

        try {
            test(new File("avidemo-jpg.avi"), new Format(EncodingKey, ENCODING_AVI_MJPG, DepthKey, 24, QualityKey, 1f));
            test(new File("avidemo-jpg-q0.5.avi"), new Format(EncodingKey, ENCODING_AVI_MJPG, DepthKey, 24, QualityKey, 0.5f));
            test(new File("avidemo-png.avi"), new Format(EncodingKey, ENCODING_AVI_PNG, DepthKey, 24));
            test(new File("avidemo-raw24.avi"), new Format(EncodingKey, ENCODING_AVI_DIB, DepthKey, 24));
            test(new File("avidemo-raw8.avi"), new Format(EncodingKey, ENCODING_AVI_DIB, DepthKey, 8));
            test(new File("avidemo-rle8.avi"), new Format(EncodingKey, ENCODING_AVI_RLE8, DepthKey, 8));
            test(new File("avidemo-tscc8.avi"), new Format(EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 8));
            test(new File("avidemo-raw8gray.avi"), new Format(EncodingKey, ENCODING_AVI_DIB, DepthKey, 8, PixelFormatKey, PixelFormat.GRAY));
            test(new File("avidemo-rle8gray.avi"), new Format(EncodingKey, ENCODING_AVI_RLE8, DepthKey, 8, PixelFormatKey, PixelFormat.GRAY));
            test(new File("avidemo-tscc8gray.avi"), new Format(EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 8, PixelFormatKey, PixelFormat.GRAY));
            test(new File("avidemo-tscc24.avi"), new Format(EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24));
            //test(new File("avidemo-rle4.avi"), AVIOutputStreamOLD.AVIVideoFormat.RLE, 4, 1f);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    

    private void test(File file, Format format) throws IOException {
        testWriting(file, format);
        try {
            testReading(file);
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
    }

    private void testWriting(File file, Format format) throws IOException {
        Logger.getAnonymousLogger().log(Level.INFO, "Writing " + file);

        // Make the format more specific
        format = format.prepend(MediaTypeKey, MediaType.VIDEO, //
              FrameRateKey, new Rational(30, 1),//
              WidthKey, 400, //
              HeightKey, 400);
     
     // Create a buffered image for this format
        Image img = createImage(format);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AVIWriter out = null;
        try {
            // Create the writer
            out = new AVIWriter(file);

            // Add a track to the writer
            out.addTrack(format);
            out.setPalette(0, img.getColorModel());

            // initialize the animation
            g.setBackground(Color.WHITE);
            g.setColor(Color.BLACK);
            int rhour = Math.min(img.getWidth(), img.getHeight()) / 2 - 50;
            int rminute = Math.min(img.getWidth(), img.getHeight()) / 2 - 30;
            int cx = img.getWidth() / 2;
            int cy = img.getHeight() / 2;
            Stroke sfine = new BasicStroke(1.0f);
            Stroke shour = new BasicStroke(7.0f);
            Stroke sminute = new BasicStroke(5.0f);

            for (int i = 0, n = 200; i < n; i++) {
                double tminute = (double) i / (n - 1);
                double thour = tminute / 60.0;

                // Create an animation frame
                g.clearRect(0, 0, img.getWidth(), img.getHeight());
                Line2D.Double lhour = new Line2D.Double(cx, cy, cx + Math.sin(thour * Math.PI * 2) * rhour, cy - Math.cos(thour * Math.PI * 2) * rhour);
                g.setColor(Color.BLACK);
                g.setStroke(shour);
                g.draw(lhour);
                Line2D.Double lminute = new Line2D.Double(cx, cy, cx + Math.sin(tminute * Math.PI * 2) * rminute, cy - Math.cos(tminute * Math.PI * 2) * rminute);
                g.setStroke(sminute);
                g.draw(lminute);

                // write it to the writer
                out.write(0, img, 1);
            }

        } finally {
            // Close the writer
            if (out != null) {
                out.close();
            }

            // Dispose the graphics object
            g.dispose();
        }
    }

    private void testReading(File file) throws IOException {
        Logger.getAnonymousLogger().log(Level.INFO, "Reading " + file);
        AVIReader in = null;

        try {
            // Create the reader
            in = new AVIReader(file);

            // Look for the first video track
            int track = 0;
            while (track < in.getTrackCount()
                  && in.getFormat(track).get(MediaTypeKey) != MediaType.VIDEO) {
                track++;
            }

            // Read images from the track
            Image img = createImage(in.getFormat(track));
            
            do {
             in.read(track, img);
                imgBuf.add(new one.empty3.libs.Image(img));
if(imgBuf.size()>MAXSIZE)
   try {Thread.sleep(50);}catch(Exception ex){ex.printStackTrace();}
in.read(track, img);
                //...to do: do something with the image...
            } while (img != null);
        } catch (UnsupportedOperationException e) {
            Logger.getAnonymousLogger().log(Level.INFO, "Reading failed " + file);
            throw e;
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(Level.INFO, "Reading failed " + file);
            throw e;
        } finally {
            // Close the rader
            if (in != null) {
                in.close();
            }
        }
}
 
 
             
        
    

    private static Image createImage(Format format) {
        int depth = format.get(DepthKey);
        int width = format.get(WidthKey);
        int height = format.get(HeightKey);
        PixelFormat pixelFormat = format.get(PixelFormatKey);

        Random rnd = new Random(0); // use seed 0 to get reproducable output
        Image img;
        switch (depth) {
            case 24:
            default: {
                img = new one.empty3.libs.Image(width, height, Image.TYPE_INT_RGB);
                break;
            }
            case 8:
                if (pixelFormat == PixelFormat.GRAY) {
                    img = new one.empty3.libs.Image(width, height, Image.TYPE_BYTE_GRAY);
                    break;
                } else {
                    byte[] red = new byte[256];
                    byte[] green = new byte[256];
                    byte[] blue = new byte[256];
                    for (int i = 0; i < 255; i++) {
                        red[i] = (byte) rnd.nextInt(256);
                        green[i] = (byte) rnd.nextInt(256);
                        blue[i] = (byte) rnd.nextInt(256);
                    }
                    rnd.setSeed(0); // set back to 0 for reproducable output
                    IndexColorModel palette = new IndexColorModel(8, 256, red, green, blue);
                    img = new one.empty3.libs.Image(width, height, Image.TYPE_BYTE_INDEXED, palette);
                    break;
                }
            case 4:
                if (pixelFormat == PixelFormat.GRAY) {
                    img = new one.empty3.libs.Image(width, height, Image.TYPE_BYTE_GRAY);
                    break;
                } else {
                    byte[] red = new byte[16];
                    byte[] green = new byte[16];
                    byte[] blue = new byte[16];
                    for (int i = 0; i < 15; i++) {
                        red[i] = (byte) rnd.nextInt(16);
                        green[i] = (byte) rnd.nextInt(16);
                        blue[i] = (byte) rnd.nextInt(16);
                    }
                    rnd.setSeed(0); // set back to 0 for reproducable output
                    IndexColorModel palette = new IndexColorModel(4, 16, red, green, blue);
                    img = new one.empty3.libs.Image(width, height, Image.TYPE_BYTE_INDEXED, palette);
                    break;
                }
        }
        return img;
    }
}
*/