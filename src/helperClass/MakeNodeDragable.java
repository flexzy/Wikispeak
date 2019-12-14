package helperClass;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class MakeNodeDragable {

	class DragPosition {
		double x;
        double y;
    }

    DragPosition dragContext = new DragPosition();

    public void makeDraggable( Node node) {
            node.setOnDragDetected(onMouseDragDetectedEventHandler);
    }
    
    /**
     * Code contributes to ORACLE 
     * https://docs.oracle.com/javafx/2/drag_drop/HelloDragAndDrop.java.html
     */
    EventHandler<MouseEvent> onMouseDragDetectedEventHandler = event -> {
    	 Node node = ((Node) (event.getSource()));
     
         /* allow any transfer mode */
         Dragboard db = node.startDragAndDrop(TransferMode.ANY);
         
         /* put a string on dragboard */
         ClipboardContent content = new ClipboardContent();
         content.putString(node.getId());
         db.setContent(content);
         
         event.consume();
    };
   
}
