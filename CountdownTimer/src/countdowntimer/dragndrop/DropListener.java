package countdowntimer.dragndrop;

import java.io.File;
import java.util.List;

public interface DropListener {
	
	void onDropEnter();
	void onDropped(List<File> files);
	void onDropFinished();

}
