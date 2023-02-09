package me.fopzl.doctor;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import me.neoblade298.neocore.bukkit.NeoCore;

public class IOManager {
	private static DataSource src;

	public static void loadConfig(FileConfiguration fileConfig) {
		HikariConfig config = new HikariConfig();
		config.setMaximumPoolSize(20);
		config.setDataSourceClassName("com.microsoft.sqlserver.jdbc.SQLServerDataSource");
		config.addDataSourceProperty("serverName", fileConfig.getString("ip"));
		config.addDataSourceProperty("user", fileConfig.getString("user"));
		config.addDataSourceProperty("password", fileConfig.getString("password"));
		config.addDataSourceProperty("portNumber", fileConfig.getString("port"));
		config.addDataSourceProperty("databaseName", "mlmc");
		config.addDataSourceProperty("encrypt", "false");
		
		src = new HikariDataSource(config);
	}

	public static void saveBlobs(boolean async, String classname, Map<String, Blob> blobs) {
		BukkitRunnable br = new BukkitRunnable() {
			@Override
			public void run() {
				try {
					Connection conn = src.getConnection();

					PreparedStatement deleteStmt = conn.prepareStatement("delete from fopzldoctor_blobs where instanceKey = ? and className = ? and name = ?;");
					PreparedStatement insertStmt = conn
							.prepareStatement("insert into fopzldoctor_blobs (instanceKey, className, name, blob) values (?, ?, ?, ?);");
					for (Entry<String, Blob> entry : blobs.entrySet()) {
						insertStmt.setString(1, NeoCore.getInstanceKey());
						insertStmt.setString(2, classname);
						insertStmt.setString(3, entry.getKey());
						insertStmt.setBlob(4, entry.getValue());
						insertStmt.addBatch();
						
						deleteStmt.setString(1, NeoCore.getInstanceKey());
						deleteStmt.setString(2, classname);
						deleteStmt.setString(3, entry.getKey());
						deleteStmt.addBatch();
					}
					
					deleteStmt.executeBatch();
					insertStmt.executeBatch();
					
					deleteStmt.close();
					insertStmt.close();
					conn.close();
				} catch (SQLException e) {
					Bukkit.getLogger().warning("[DOCTOR] SQLException saving BLOBs for " + classname + ":");
					e.printStackTrace();
				}
			}
		};

		if (async)
			br.runTaskAsynchronously(Doctor.getInstance());
		else
			br.run();
	}
	
	public static Map<String, Blob> loadBlobs(String classname) throws SQLException {
		Map<String, Blob> blobs = new HashMap<String, Blob>();

		Connection conn = src.getConnection();

		PreparedStatement stmt = conn.prepareStatement("select name, blob from fopzldoctor_blobs where instanceKey = ? and className = ?;");
		stmt.setString(1, NeoCore.getInstanceKey());
		stmt.setString(2, classname);
		ResultSet rs = stmt.executeQuery();
		
		while (rs.next()) {
			blobs.put(rs.getString("name"), rs.getBlob("blob"));
		}
		
		stmt.close();
		
		stmt = conn.prepareStatement("delete from fopzldoctor_blobs where instanceKey = ? and className = ?;");
		stmt.setString(1, NeoCore.getInstanceKey());
		stmt.setString(2, classname);
		stmt.execute();
		stmt.close();
		
		conn.close();

		return blobs;
	}
	
	public static void writeToSQL(List<String> sqlStatements) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					Connection conn = src.getConnection();
					Statement stmt = conn.createStatement();

					for (String s : sqlStatements) {
						stmt.addBatch(s);
					}
					stmt.executeBatch();
					
					stmt.close();
					conn.close();
				} catch (SQLException e) {
					Bukkit.getLogger().warning("[DOCTOR] SQLException writing data:");
					e.printStackTrace();
				}
			}
			
		}.runTaskAsynchronously(Doctor.getInstance());
	}
}
