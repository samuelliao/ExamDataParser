import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbUtil {
	Connection Conn;
	private Logger _log = Logger.getLogger(SysProperty.class.getName());

	public DbUtil() {
		try {
			// Register JDBC
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Conn = DriverManager.getConnection(SysProperty.DBConnectString);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			_log.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	public String getDataFromDb(String sql) {
		String result = "";
		try {
			Statement stmt = Conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				result = rs.getString("ce_no") + ";" + rs.getString("doc");
			}
			rs.close();
			stmt.close();
		} catch (Exception ex) {
			_log.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return result;
	}

	public void updateDataIntoDb(String sql) {
		try {
			Statement stmt = Conn.createStatement();
			stmt.executeUpdate(sql);
			if (!stmt.isClosed()) {
				stmt.close();
			}
		} catch (Exception ex) {
			_log.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	public void connectionClose() {
		try {
			if (!Conn.isClosed())
				Conn.close();
		} catch (Exception ex) {
			_log.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	public String genInsertStr(String[] attrs) {
		String sql = "Insert INTO ex_results VALUES(newid(), ";
		for (int index = 0; index < attrs.length; index++) {
			if (index == 7) {
				sql += "'" + (attrs[index].length() == 0 ? "0" : "1") + "',";
			} else {
				if (index == attrs.length - 1) {
					sql += "N'" + (attrs[index] == "999" ? "" : attrs[index]) + "')";
				} else {
					sql += "N'" + attrs[index] + "',";
				}
			}
		}
		return sql;
	}

	public String genInsertStr(String[] attrs, String tmp) {
		String sql = "Insert INTO ex_results VALUES(newid(), ";
		String[] tmp2 = tmp.split(";");
		String ceNo = "";
		String doc = "";
		if (tmp2.length >= 2) {
			ceNo = tmp2[0];
			doc = tmp2[1];
		} else if (tmp.startsWith(";")) {
			doc = tmp2[0];
		} else {
			ceNo = tmp2[0];
		}
		for (int index = 0; index < attrs.length; index++) {
			if (index == 7) {
				sql += "'" + (attrs[index].length() == 0 ? "0" : "1") + "',";
			} else {
				sql += "'" + attrs[index] + "',";
			}
		}
		// for (String attr : attrs) {
		// sql += "'" + attr + "',";
		// }
		sql += "'" + ceNo + "',";
		sql += "'" + doc + "')";
		return sql;
	}
}
