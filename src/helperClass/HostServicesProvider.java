package helperClass;

import javafx.application.HostServices;

import java.io.File;

/**
 * This class is a modified implementation of a class attributed to https://stackoverflow.com/a/33100968
 */
public enum HostServicesProvider {

    INSTANCE ;

    private HostServices hostServices ;
    public void init(HostServices hostServices) {
        if (this.hostServices != null) {
            throw new IllegalStateException("Host services already initialized");
        }
        this.hostServices = hostServices ;
    }
    public void openUserManual() {
        File file = new File("User-Manual.pdf");
        hostServices.showDocument(file.getAbsolutePath());
    }
}
