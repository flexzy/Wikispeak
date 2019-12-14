package helperClass;

import javafx.collections.ObservableList;

import java.io.File;

public class CreateAudio {

    /**
     * Method that creates the audio file of the specified text chunk
     * @param audioChunk is chunk that is to be converted into an audio file
     */
    public static void textToAudioFile(AudioChunk audioChunk) {
        // Get attributes of the audio chunk to be converted
        double speed = audioChunk.get_speed();
        // edit the text so that quotation marks within the text will be correctly interpreted in the bash command
        String text = audioChunk.get_text().replace("\"", "\\\"");
        String accent = audioChunk.get_accent();
        int fileNumber = audioChunk.get_fileNumber();

        // Execute bash command to create audio file from text
        Thread worker = new Thread(() -> {
            double wpm = speed * 175;
            try {
                String audioFile = "." + File.separator + ".temp" + File.separator + "audio" + fileNumber + ".wav";
                ProcessBuilder pb = new ProcessBuilder();
                switch (accent) {
                    case "British":
                        pb.command("bash", "-c", "espeak -ven \"" + text + "\" -s " + wpm + " -w \"" + audioFile + "\"");
                        break;
                    case "American":
                        pb.command("bash", "-c", "espeak -ven-us \"" + text + "\" -s " + wpm + " -w \"" + audioFile + "\"");
                        break;
                    default:
                        pb.command("bash", "-c", "espeak -ven-sc \"" + text + "\" -s " + wpm + " -w \"" + audioFile + "\"");
                        break;
                }
                pb.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        worker.start();
    }

    /**
     * Method to preview the selected text
     * @param text text to be previewed
     * @param accent the accent to speak in
     * @param speed the speed of speech desired
     */
    public static void preview(String text, String accent, double speed) {
        // edit the text so that quotation marks within the text will be correctly interpreted in the bash command
        String finalText = text.replace("\"", "\\\"");

        // Execute bash command to play audio from text
        Thread worker = new Thread(() -> {
            double wpm = speed * 175;
            try {
                ProcessBuilder pb = new ProcessBuilder();
                switch (accent) {
                    case "British":
                        pb.command("bash", "-c", "espeak -ven \"" + finalText + "\" -s " + wpm);
                        break;
                    case "American":
                        pb.command("bash", "-c", "espeak -ven-us \"" + finalText + "\" -s " + wpm);
                        break;
                    default:
                        pb.command("bash", "-c", "espeak -ven-sc \"" + finalText + "\" -s " + wpm);
                        break;
                }
                pb.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        worker.start();
    }

    /**
     * Bash command for concatenating audio attributes to https://superuser.com/a/587553
     * Method to merge multiple audio files together into one audio file
     * @param audioChunksCollection the collection of all the audio chunks to be merged
     */
    public static void mergeAudioFiles(ObservableList<AudioChunk> audioChunksCollection) {
        try {
            String command = "ffmpeg";
            for (AudioChunk audioChunk : audioChunksCollection) {
                command += " -i ." + File.separator + ".temp" + File.separator + "audio" + audioChunk.get_fileNumber() + ".wav";
            }
            command += " -filter_complex '";
            for (int i = 0; i < audioChunksCollection.size(); i++) {
                command += "[" + i + ":0]";
            }
            command += "concat=n=" + audioChunksCollection.size() + ":v=0:a=1[out]' -map '[out]' ." + File.separator + ".temp" + File.separator + "speech.wav";

            ProcessBuilder pb = new ProcessBuilder();
            pb.command("bash", "-c", command);
            Process process = pb.start();
            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Code for the bash audio merge bash command attributes to https://stackoverflow.com/a/57054541
     * Method to add background music to the speech audio
     * @param music the name of the type of music to add
     */
    public static void addMusic(String music) {
        try {
            ProcessBuilder pb = new ProcessBuilder();
            switch (music) {
                case "Relaxing":
                    pb.command("bash", "-c", "ffmpeg -y -i ./.temp/speech.wav -i ./.resources/music/Relaxing.mp3 -filter_complex \"[0:0]volume=1[a];[1:0]volume=0.3[b];[a][b]amix=inputs=2:duration=first\" ./.temp/audio.wav");
                    break;
                case "Rock":
                    pb.command("bash", "-c", "ffmpeg -y -i ./.temp/speech.wav -i ./.resources/music/Rock.mp3 -filter_complex \"[0:0]volume=1[a];[1:0]volume=0.1[b];[a][b]amix=inputs=2:duration=first\" ./.temp/audio.wav");
                    break;
                case "Electronic":
                    pb.command("bash", "-c", "ffmpeg -y -i ./.temp/speech.wav -i ./.resources/music/Electronic.mp3 -filter_complex \"[0:0]volume=1[a];[1:0]volume=0.1[b];[a][b]amix=inputs=2:duration=first\" ./.temp/audio.wav");
                    break;
                default:
                    pb.command("bash", "-c", "mv ./.temp/speech.wav ./.temp/audio.wav");
                    break;
            }
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to delete an audio file
     * @param num the specific number of audio file to delete
     */
    public static void delete(int num) {
        BashCommandProcess.runBashCommand("rm -f ." + File.separator + ".temp" + File.separator + "audio" + num + ".wav");
    }
}
