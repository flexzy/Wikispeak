package helperClass;

public class AudioChunk {
    private final String _text;
    private final String _accent;
    private final Double _speed;
    private final int _fileNumber;

    /**
     * The class that represents the abstraction of a chunk of audio that is part of the creation
     * @param text text to be converted
     * @param accent the accent of the speech
     * @param speed the speed of the speech
     * @param fileNumber the identification number associated with this audio chunk
     */
    public AudioChunk(String text, String accent, double speed, int fileNumber) {
        _text = text;
        _accent = accent;
        _speed = speed;
        _fileNumber = fileNumber;
    }

    public String get_text() {
        return _text;
    }

    public String get_accent() {
        return _accent;
    }

    public Double get_speed() {
        return _speed;
    }

    public int get_fileNumber() {
        return _fileNumber;
    }

}
