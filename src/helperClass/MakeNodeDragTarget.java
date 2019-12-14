package helperClass;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;

/**
 * Code contributes to ORACLE
 * https://docs.oracle.com/javafx/2/drag_drop/HelloDragAndDrop.java.html
 * @author zyan225
 */
public class MakeNodeDragTarget {
	
	/**
	 * add all the required listener to the node 
	 * make this node the target for a drag and drop input 
	 * @param node
	 */
	public void makeDragTarget( Node node) {
		node.setOnDragOver(this.onMouseDragOverEventHandler);
		node.setOnDragDropped(this.onDragDroppedEventHandler);
		node.setOnDragDone(this.onDragDoneEventHandler);
	}
	
	EventHandler<DragEvent> onMouseDragOverEventHandler = event -> {
		Node node = (Node) event.getGestureTarget();
        
        /* accept it only if it is  not dragged from the same node 
         * and if it has a string data */
        if (event.getGestureSource() != node &&
                event.getDragboard().hasString()) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        
        event.consume();
	};
	
	EventHandler<DragEvent> onDragDroppedEventHandler = event -> {
		
        /* if there is a string data on dragboard, read it and use it */
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
        	HBox dropBox = (HBox)event.getGestureTarget();
        	Label source = (Label)event.getGestureSource();
        	
        	
        	if ((dropBox.getChildren().size() == 0) || ((dropBox.getId() != null) && (!dropBox.getChildren().contains(source)))) {
        		// only add the label as a child if there is no other children inside Hbox 
        		dropBox.getChildren().add(source);
        	}
	        success = true;
        }
        /* let the source know whether the string was successfully 
         * transferred and used */
        event.setDropCompleted(success);
        event.consume();
	};
	
	
	EventHandler<DragEvent> onDragDoneEventHandler = event -> {
        /* if the data was successfully moved, clear it */
        if (event.getTransferMode() == TransferMode.MOVE) {
            ((Label)event.getGestureSource()).setText("done");
        }
        
        event.consume();
	};
	

}	
