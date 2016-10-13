import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TimerCheck extends TimerTask {
	private Logger _log = Logger.getLogger(SysProperty.class.getName());

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// System.out.println("Task Timer Start");
		FileUtil fileUtil = new FileUtil();
		List<File> files = fileUtil.listFiles(SysProperty.SourceFolderPath);
		List<String> lines = new ArrayList<String>();
		if (files != null) {
			for (File file : files) {
				if (fileUtil.isFileReady(file.lastModified())) {
					lines = fileUtil.genDbOutputValue(file);
					// Write back the lines of data.
					// checkLineOfData(lines);
					dataParser(lines);
					fileUtil.moveFile(file, SysProperty.DestinationFolderPath);
				}
			}
		}
		// System.out.println("Task Timer End");
	}

	private void dataParser(List<String> lines) {
		List<String[]> data = convertLine(lines);
		data = sortListByDocAttr(data);
		String sql = "";
		for (String[] attr : data) {
			try {
				sql = SysProperty._dbUtil.genInsertStr(attr);
				SysProperty._dbUtil.updateDataIntoDb(sql);
			} catch (Exception ex) {
				_log.log(Level.SEVERE, ex.getMessage(), ex);
			}
		}

	}

	private List<String[]> convertLine(List<String> lines) {
		List<String[]> result = new ArrayList<>();
		String[] attrs = null;
		String tmp = "";

		for (String line : lines) {
			try {
				if (line.contains(",")) {
					attrs = line.split(",");
					if (attrs.length == 9) {
						// Check value attribute
						if (attrs[6].equals(".") || attrs[6].length() == 0)
							continue;
						tmp = SysProperty._dbUtil
								.getDataFromDb("Select * From check_items Where ce_name  = '" + attrs[5] + "'");
						if (tmp.length() == 0)
							continue;
						result.add(setCeNoAndDoc(attrs, tmp));
					}
				}
			} catch (Exception ex) {
				_log.log(Level.SEVERE, ex.getMessage(), ex);
				continue;
			}
		}
		return result;
	}

	private List<String[]> sortListByDocAttr(List<String[]> oriLst) {
		List<String[]> result = new ArrayList<>();
		List<String[]> tmpLst = new ArrayList<>();
		for (String[] attrs : oriLst) {
			if (tmpLst.size() == 0) {
				tmpLst.add(attrs);
			} else {
				if (attrs[0].equals(tmpLst.get(0)[0])) {
					tmpLst.add(attrs);
				} else {
					result.addAll(sortArray(tmpLst));
					tmpLst = new ArrayList<>();
					tmpLst.add(attrs);
				}
			}
		}

		if (tmpLst.size() > 0) {
			result.addAll(sortArray(tmpLst));
		}
		return result;
	}

	private List<String[]> sortArray(List<String[]> lst) {
		Collections.sort(lst, new Comparator<String[]>() {
			public int compare(String[] o1, String[] o2) {
				return (Integer.parseInt(o2[o2.length - 1]) - Integer.parseInt(o1[o1.length - 1])) * -1;
			}
		});
		return lst;
	}

	private String[] setCeNoAndDoc(String[] attrs, String tmp) {
		String[] strs = Arrays.copyOf(attrs, attrs.length + 2);
		// System.arraycopy(strs, attrs, arg2, arg3, arg4);
		String[] tmp2 = tmp.split(";");
		if (tmp2.length >= 2) {
			strs[strs.length - 2] = tmp2[0];
			strs[strs.length - 1] = tmp2[1];
		} else if (tmp.startsWith(";")) {
			strs[strs.length - 1] = tmp2[0];
		} else {
			strs[strs.length - 1] = "999";
			strs[strs.length - 2] = tmp2[0];
		}
		return strs;
	}

	private void checkLineOfData(List<String> lines) {
		String[] attrs = null;
		String tmp = "";

		for (String line : lines) {
			try {
				if (line.contains(",")) {
					attrs = line.split(",");
					if (attrs.length == 9) {
						// Check value attribute
						if (attrs[6].equals(".") || attrs[6].length() == 0)
							continue;
						tmp = SysProperty._dbUtil
								.getDataFromDb("Select * From check_items Where ce_name  = '" + attrs[5] + "'");
						if (tmp.length() == 0)
							continue;
						tmp = SysProperty._dbUtil.genInsertStr(attrs, tmp);
						SysProperty._dbUtil.updateDataIntoDb(tmp);
					}
				}
			} catch (Exception ex) {
				_log.log(Level.SEVERE, ex.getMessage(), ex);
				continue;
			}
		}
	}

}
