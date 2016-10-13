import java.util.Scanner;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainParser {

	private static Logger _log = Logger.getLogger(SysProperty.class.getName());
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub		
		Scanner scan = new Scanner(System.in);
		String input = "";
		System.out.println("Parser Start");
		
		SysProperty.loadConfig();
		SysProperty.logInitial();;
		SysProperty.initialDbUtil();
		
//		_log.info("Parser Start");
		Timer timer = new Timer();
		try{
		boolean flag = true;
		timer.schedule(new TimerCheck(), 5*1000, SysProperty.TimerPeriod*1000);
		while(flag){
			input = scan.next().trim().toLowerCase();
			switch(input){
			case "exit":
			case "0":
			case "quit":
				flag = false;
				break;
			case "reload":	
				System.out.println("Reload config...");
				SysProperty.loadConfig();
			default:
				break;
			}
			
		}
		System.out.println("Parser Close");
		}catch(Exception ex){
			System.out.println(ex.getMessage());
			_log.log(Level.SEVERE, ex.getMessage(), ex);
		}finally{
			timer.cancel();
			timer.purge();
			scan.close();
		}
	}
}
