import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FileUtil {
	private static Logger _log = Logger.getLogger(SysProperty.class.getName());

	public List<File> listFiles(String folderPath) {
		//_log.info("List files in folder. The folder path =" + folderPath);
		List<File> filesInFolder = new ArrayList<File>();
		try {
			if (new File(folderPath).exists()) {
//				_log.info("Folder path exist");
				filesInFolder = Files.walk(Paths.get(folderPath)).filter(Files::isRegularFile).map(Path::toFile)
						.collect(Collectors.toList());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			_log.log(Level.SEVERE, e.getMessage(), e);
		}
//		_log.info("List files in folder done. Get files number=" + filesInFolder.size());
		return filesInFolder;
	}

	public List<String> genDbOutputValue(File fileInfo) {
		// if(fileInfo.lastModified())
		List<String> strs = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileInfo));
			try {
				String line = br.readLine();

				while (line != null) {
					strs.add(line);
					line = br.readLine();
				}
			} finally {
				br.close();
			}
		} catch (IOException ex) {
			_log.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return strs;
	}

	public void moveFiles(List<File> files, String destPath) {
		for (File file : files) {
			moveFile(file, destPath);
		}
	}

	public void moveFile(File file, String destPath) {
		try {
			String newFile = destPath + file.getName();
			if (file.exists()) {
				// _log.info("Move file from " + file.getPath());
				// _log.info("Move file to " + newFile);
				File desFile = new File(newFile);
				if (desFile.exists()) {
					desFile.delete();
				}
				file.renameTo(desFile);
			}
		} catch (Exception ex) {
			_log.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	public boolean isFileReady(long modifiedTime) {
		long current = System.currentTimeMillis() - (SysProperty.FileWaitTime * 1000);
		return current >= modifiedTime;
	}
}
