package helperClass;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.*;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Code in getAPIKey and downloadImage attributed to Nasser 
 * @author zyan225
 *
 */
public class CreateVideo {
    /**
     * amount of image that user selects 
     */
    private int _imageAmount;
    
    /**
     * name of the word searched 
     */
    private String _searchTerm;
    
	public CreateVideo(int imageAmount, String searchName) {
		// create a new file to store all images download
		File dirTemp = new File(".temp/imageDownload");
		dirTemp.mkdir();
	
		_searchTerm = searchName;
		_imageAmount = imageAmount;
	}
	
	
	/**
	 * create a video file by putting all the download images into a slide show
	 */
	public void createVideo() {
		
		ProcessBuilder processBuilder = new ProcessBuilder();
		
		float lengthPerPhoto = 0;
		
		// calculate the display time for each photo
		lengthPerPhoto = this.getDurationOfAudioFile() / _imageAmount;
		
		// make the slide show 
		processBuilder.command("bash", "-c", "cat ./.temp/imageDownload/*.jpg | ffmpeg -f image2pipe -framerate 1/"+lengthPerPhoto+" -i - -c:v libx264 -pix_fmt yuv420p -vf \"scale=w=1080:h=720:force_original_aspect_ratio=1,pad=1080:720:(ow-iw)/2:(oh-ih)/2\" -r 25 -y ./.temp/output.mp4");

		Process process;
		try {
			process = processBuilder.start();
			process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// add the searched words on the top of the video file 
		ProcessBuilder processBuilder2 = new ProcessBuilder();
		processBuilder2.command("bash", "-c", "ffmpeg -i \"./.temp/output.mp4\" -vf \"drawtext=fontfile=./.resources/DancingScript-Regular.otf:text='"+_searchTerm+"':x=(w-text_w)/2: y=(h-text_h)/2:fontsize=80:fontcolor=black:shadowcolor=white:shadowx=5:shadowy=5\" ./.temp/output2.mp4");

		Process process2;
		try {
			process2 = processBuilder2.start();
			process2.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
	}
	
	/**
	 * find the total duration of the audio file in order to find out the length of display for each image in the video file 
	 * @return duration of the audio file 
	 */
	private float getDurationOfAudioFile() {
		String path = "./.temp/audio.wav";
		File file = new File(path);
		AudioInputStream audioInputStream;
		try {
			// calculate the duration of audio file 
			audioInputStream = AudioSystem.getAudioInputStream(file);
			AudioFormat format = audioInputStream.getFormat();
			long audioFileLength = file.length();
			int frameSize = format.getFrameSize();
			float frameRate = format.getFrameRate();
			float durationInSeconds = (audioFileLength / (frameSize * frameRate));
			return durationInSeconds;
			
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;

	}


	public static String getAPIKey(String key) throws Exception {
		String config = System.getProperty("user.dir") 
				+ System.getProperty("file.separator") + ".resources/flickr-api-keys.txt";

		File file = new File(config); 
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		
		String line;
		while ( (line = br.readLine()) != null ) {
			if (line.trim().startsWith(key)) {
				br.close();
				return line.substring(line.indexOf("=")+1).trim();
			}
		}
		br.close();
		throw new RuntimeException("Couldn't find " + key +" in config file "+file.getName());
	}

	/**
	 * Download the images from flickr and store them in a file called "imageDownload"
	 */
	public void downloadImage() {
		
		try {
			String apiKey = getAPIKey("apiKey");
			String sharedSecret = getAPIKey("sharedSecret");

			Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());

			String query = _searchTerm;
			int resultsPerPage = _imageAmount;
			int page = 0;
			
			// get all the photos from flickr
			PhotosInterface photos = flickr.getPhotosInterface();
			SearchParameters params = new SearchParameters();
			params.setSort(SearchParameters.RELEVANCE);
			params.setMedia("photos");
			params.setText(query);

			PhotoList<Photo> results = photos.search(params, resultsPerPage, page);

			int count = 0;
			for (Photo photo: results) {
				try {
					BufferedImage image = photos.getImage(photo,Size.LARGE);

					// give each image a name followed the format: 0.jpg, 1.jpg, 3.jpg, etc.
					String filename = count+".jpg";
					File outputfile = new File(".temp/imageDownload",filename);
					ImageIO.write(image, "jpg", outputfile);
					count++;
				} catch (FlickrException fe) {
					fe.printStackTrace();
				}

			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}
