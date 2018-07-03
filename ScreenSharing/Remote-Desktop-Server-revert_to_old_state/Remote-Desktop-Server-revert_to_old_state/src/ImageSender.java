import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ImageSender implements Runnable{
	
	private InetAddress clientAddr;
	private int clientPort;
	private DatagramSocket socket;
	public boolean connected = false;
	
	public static final float SIZETHRESHOLD = 100f;
	
	private BufferedImage img;
	
	public ImageSender(String ip, int port){
		try{
			clientAddr = InetAddress.getByName(ip);
		}
		catch (Exception e){
			System.out.print("Exception setting ip address");
		}
		
		clientPort = port;
		
		try {
	           socket = new DatagramSocket();	           
	           connected = true;
	           
	       }
	       catch (Exception e) {
	           System.out.print("Could not bind to a port");
	       }
	}
	
	public ImageSender(InetAddress ip, int port){
		clientAddr = ip;
		clientPort = port;
		
		try {
	           socket = new DatagramSocket();	           
	           connected = true;
	           
	       }
	       catch (Exception e) {
	           System.out.print("Could not bind to a port");
	       }
	}
	
	public void setImage(BufferedImage image)
	{
		try {
		String fileName="C:\\Dattatray\\mypic.jpg";
		File f = new File(fileName); 
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
			ImageIO.write( image, "jpg", baos );
		
		baos.flush();
		byte[] fileContent = baos.toByteArray();

		Path path = Paths.get(f.getAbsolutePath());
		try {
		    Files.write(path, fileContent);
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		img = image;
	}
	
	public void setPort(int port)
	{
		clientPort = port;
	}
	
	public void run(){
		ByteArrayOutputStream buffer;
		
		/*try {
			Socket socket1 = new Socket("localhost", 13085);
			OutputStream outputStream = socket1.getOutputStream();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	        ImageIO.write(img, "jpg", byteArrayOutputStream);

	        byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
	        outputStream.write(size);
	        outputStream.write(byteArrayOutputStream.toByteArray());
	        outputStream.flush();
	        socket1.close();
		}catch(Exception e) {
			
		}*/
		try{
			
			ByteArrayOutputStream tmp = compressImage(img, 1.0f);
		    ImageIO.write(img, "jpeg", tmp);
		    tmp.close();
		    
		    int contentLength = tmp.size();
		    float compress = 64000.0f/contentLength;
		    System.out.println("Compress size "+compress);
		    
		    if(compress > 1.0) {
		    	buffer = tmp;
		    } else {
		    	buffer = compressImage(img, (compress+0.08f));
		    }

		}catch(IOException e){
			System.out.println(e);
			return;
		}
		
		/*ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			ImageIO.write(img, "jpg", buffer);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
		byte[] data = buffer.toByteArray();
		
		try {
			String fileName="C:\\Dattatray\\mypic1.jpg";
			File f = new File(fileName); 
			
			

			Path path = Paths.get(f.getAbsolutePath());
			try {
			    Files.write(path, data);
			} catch (Exception ex) {
			    ex.printStackTrace();
			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
        DatagramPacket out = new DatagramPacket(data, data.length, clientAddr, clientPort);
        System.out.println("Data Length = "+data.length);
        try {
			socket.send(out);
			buffer.close();
		} catch (IOException e) {
			System.out.println("Data Length = "+data.length);
			e.printStackTrace();
		}
	}
	
	private BufferedImage scaleImage(BufferedImage image, int width, int height)
	{
		BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// Paint scaled version of image to new image
		Graphics2D graphics2D = scaledImage.createGraphics();
		
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		
		graphics2D.drawImage(image, 0, 0, width, height, null); 
		graphics2D.dispose();
		return scaledImage;
	}
	
	private BufferedImage scaleImage(BufferedImage image, int width, int height, float scale)
	{
		BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// Paint scaled version of image to new image
		Graphics2D graphics2D = scaledImage.createGraphics();
		AffineTransform xform = AffineTransform.getScaleInstance(scale, scale);
		
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		
		graphics2D.drawImage(image, xform, null);
		
		graphics2D.dispose();
		return scaledImage;
	}
	
	
	/*
	 * 
	 * Compression
	 * 
	 */
	private ByteArrayOutputStream compressImage(BufferedImage image, float quality) throws IOException 
	{
		// Get a ImageWriter for jpeg format.
		Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix("jpeg");
		if (!writers.hasNext()) throw new IllegalStateException("No writers found");
		ImageWriter writer = (ImageWriter) writers.next();
		
		while(!writer.getDefaultWriteParam().canWriteCompressed() && writers.next() != null)
		{
			writer = writers.next();
		}
		
		
		// Create the ImageWriteParam to compress the image.
		ImageWriteParam param = writer.getDefaultWriteParam();
		
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(quality);
		
		// The output will be a ByteArrayOutputStream (in memory)
		ByteArrayOutputStream bos = new ByteArrayOutputStream(32768);
		ImageOutputStream ios = ImageIO.createImageOutputStream(bos);
		writer.setOutput(ios);
		writer.write(null, new IIOImage(image, null, null), param);
		ios.flush();
		
		return bos;
		
		/*
		// otherwise the buffer size will be zero!
		// From the ByteArrayOutputStream create a RenderedImage.
		ByteArrayInputStream in = new ByteArrayInputStream(bos.toByteArray());
		RenderedImage out = ImageIO.read(in);
		int size = bos.toByteArray().length;
		showImage("Compressed to " + quality + ": " + size + " bytes", out); 
		*/
	}
	
	
}
