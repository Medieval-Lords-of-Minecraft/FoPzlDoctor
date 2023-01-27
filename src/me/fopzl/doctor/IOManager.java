package me.fopzl.doctor;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

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
		config.addDataSourceProperty("port", fileConfig.getString("port"));
		config.addDataSourceProperty("databaseName", "mlmc");
		config.addDataSourceProperty("encrypt", "false");

		src = new HikariDataSource(config);
	}

	public static void saveBlobs(String classname, Map<String, Blob> blobs) throws SQLException {
		Connection conn = src.getConnection();

		PreparedStatement stmt = conn.prepareStatement("insert into fopzldoctor_blobs (instanceKey, className, name, blob) values (?, ?, ?, ?);");
		for (Entry<String, Blob> entry : blobs.entrySet()) {
			stmt.setString(1, NeoCore.getInstanceKey());
			stmt.setString(2, classname);
			stmt.setString(3, entry.getKey());
			stmt.setBlob(4, entry.getValue());
			stmt.execute();
		}
		
		stmt.close();
		conn.close();
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
		conn.close();

		return blobs;
	}
	
	public static void writeToSQL(String sqlStatement) {
		try {
			Connection conn = src.getConnection();
			Statement stmt = conn.createStatement();
			
			stmt.execute(sqlStatement);
			
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			Bukkit.getLogger().warning("[DOCTOR] Exception writing SQL: " + sqlStatement);
			e.printStackTrace();
		}
	}
}