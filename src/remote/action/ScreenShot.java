package remote.action;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
/**
 * Classe qui red�finit la classe de l'interface RemoteActions. Utilise le robot pour creer une capture d'ecran.  
 * @author lh
 *
 */
public class ScreenShot implements RemoteActions {
	
/**********************************************************************************************************************************************/
/*													   ARGUMENT																	   			   /	
/**********************************************************************************************************************************************/
	private static byte[] size = null;

	
	
	
	
/**********************************************************************************************************************************************/
/*													   CONSTRUCTEUR																	   			   /	
/**********************************************************************************************************************************************/
	@Override
	public Object executer(Robot robot) throws IOException {
		// TODO Auto-generated method stub
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		BufferedImage screenshot = robot.createScreenCapture(new Rectangle(
				dimension.width, dimension.height));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(screenshot, "png", baos);
		size = ByteBuffer.allocate(4).putInt(baos.size()).array();
		System.out.println("Executer");
		return baos.toByteArray();
	}

	
	
/**********************************************************************************************************************************************/
/*													   METHODES																	   			   /	
/**********************************************************************************************************************************************/
	public static byte[] getsize() {

		return size;
	}

}
