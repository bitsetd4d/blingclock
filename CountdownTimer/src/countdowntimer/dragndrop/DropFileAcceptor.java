package countdowntimer.dragndrop;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

public class DropFileAcceptor implements DropTargetListener {
	
	private DropListener listener;
	
	public DropFileAcceptor(DropListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		 try {
		      Transferable tr = dtde.getTransferable();
		      DataFlavor[] flavors = tr.getTransferDataFlavors();
		      for (int i = 0; i < flavors.length; i++) {
		    	  System.out.println("Possible flavor: " + flavors[i].getMimeType());
		    	  if (flavors[i].isFlavorJavaFileListType()) {
		    		  // Great!  Accept copy drops...
		    		  dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
		    		  System.out.println("Successful accept drag enter");
		    		  listener.onDropEnter();
		    		  return;
		    	  }
		      }
		 } catch (Exception e) {
			 e.printStackTrace();
			 dtde.rejectDrag();
			 listener.onDropFinished();
		 }		      
	}

	@Override
	public void dragExit(DropTargetEvent arg0) {
		listener.onDropFinished();
	}

	@Override
	public void dragOver(DropTargetDragEvent arg0) {}

	@Override
	public void dropActionChanged(DropTargetDragEvent arg0) {}


	@Override
	public void drop(DropTargetDropEvent dtde) {
		System.out.println("DROP " + dtde);
		try {
			Transferable tr = dtde.getTransferable();
			DataFlavor[] flavors = tr.getTransferDataFlavors();
			for (int i = 0; i < flavors.length; i++) {
				System.out.println("Possible flavor: "+ flavors[i].getMimeType());
				if (flavors[i].isFlavorJavaFileListType()) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					System.out.println("Successful file list drop.");
					java.util.List list = (java.util.List) tr
							.getTransferData(flavors[i]);
					for (int j = 0; j < list.size(); j++) {
						System.out.println("-DROPPING-> " + list.get(j));
					}
					listener.onDropped(list);
					dtde.dropComplete(true);
					listener.onDropFinished();
					return;
				}
			}
			System.out.println("Drop failed: " + dtde);
			dtde.rejectDrop();
		} catch (Exception e) {
			e.printStackTrace();
			dtde.rejectDrop();
			listener.onDropFinished();
		}
	}

}
