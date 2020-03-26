package krusty;

import spark.Request;
import spark.Response;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import static krusty.Jsonizer.toJson;

public class Database {
	/**
	 * Modify it to fit your environment and then use this string when connecting to
	 * your database!
	 */
	private static final String jdbcString = "jdbc:sqlite://C:/Users/André/Desktop/edaf20projectDB/krusty.db";

	// For use with MySQL or PostgreSQL
	private static final String jdbcUsername = "localhost";
	private static final String jdbcPassword = "localhost";
	private Connection conn;

	public void connect() {
		// Connect to database here
		try {
			// Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(jdbcString);
			System.out.println(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// TODO: Implement and change output in all methods below!

	public String getCustomers(Request req, Response res) {

		String json = "";
		String sql = "SELECT customerName AS name, address FROM Customers";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			json = Jsonizer.toJson(rs, "customers");
		} catch (SQLException e) {

			e.printStackTrace();
		}

		return json;
	}

	public String getRawMaterials(Request req, Response res) {

		String json = "";
		String sql = "SELECT ingredient AS name, amountInStorage AS amount, unit" + " FROM StoredIngredients";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			json = Jsonizer.toJson(rs, "raw-materials");

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return json;
	}

	public String getCookies(Request req, Response res) {

		String json = "";
		String sql = "SELECT cookieName AS name FROM Cookies";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			json = Jsonizer.toJson(rs, "cookies");

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return json;
	}

	public String getRecipes(Request req, Response res) {

		String json = "";
		String sql = "SELECT cookieName AS cookie, IngredientAmount.ingredient AS raw_material, amount, unit\n"
				+ "FROM IngredientAmount\n"
				+ "INNER JOIN StoredIngredients on IngredientAmount.ingredient = StoredIngredients.ingredient";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			json = Jsonizer.toJson(rs, "recipes");

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return json;
	}

	public String getPallets(Request req, Response res) {
		
		String json = "";
		String sql = "SELECT palletId AS id, cookieName AS cookie, producedAt AS production_date, location AS customer, isBlocked AS blocked "
				+ "FROM Pallet "
				+ "WHERE ";
		
		String from = req.queryParams("from");
		String to = req.queryParams("to");
		String cookie = req.queryParams("cookie");
		String blocked = req.queryParams("blocked");
		
		ArrayList<String> values = new ArrayList<String>();
		
		if(from != null) {
			sql += "production_date >= ? ";
			values.add(from);
		}
		
		if(to != null) {
			if(values.size() > 0 )
			sql += "AND ";
			else {
				sql += "production_date <= ? ";
			}
			values.add(to);
		}
		
		if(cookie != null) {			
			if(values.size() > 0 )
				sql += "AND ";
				else {
					sql += "cookie = ? ";
				}
				values.add(cookie);
			
		}

		if (blocked != null) {
			if (values.size() > 0)
				sql += "AND ";
			else
				sql += "blocked = ? ";

			if (blocked.equals("yes"))
				values.add("1");
			else
				values.add("0");
		}
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			for (int i = 0; i < values.size(); i++) {
				
				stmt.setString(i+1, values.get(i));				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
		
		return json;
	}

	public String reset(Request req, Response res) {
		try {

			Statement stmt = conn.createStatement();

			InputStream resourceAsStream = getClass().getResource("/reset.sql").openStream();
			if (resourceAsStream == null)
				throw new IOError(new IOException("Could not find reset.sql"));

			BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8));
			String sql = "";
					
			for(String line = reader.readLine(); line != null; line=reader.readLine()) {
				if(!"".equals(line.trim()))
					sql+= line + " ";
			}
			
			for(String statement : sql.split(";")) { //Dela upp i SQL frågor
				System.out.println("*" + statement);
				stmt.addBatch(statement);
			}

			stmt.executeBatch();
			stmt.close();
		} catch (IOException e) {
			throw new RuntimeException("Could not open reset file.");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "{}";
	}

	public String createPallet(Request req, Response res) {

		String cookieName = req.queryParams("cookie");
		String creationTime = LocalDate.now().toString();
		int palletId = -1;

		if (cookieExists(cookieName)) { // Create pallet

			try {

				String sql = "INSERT INTO Pallet (producedAt, isBlocked, cookieName) VALUES(?, ?, ?)"; // Create pallet
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, creationTime);
				ps.setBoolean(2, false);
				ps.setString(3, cookieName);
				ps.executeUpdate();
				ps.close();

				subtractIngredients(cookieName);

				sql = "SELECT MAX(palletId) as palletId from Pallet"; //Find biggest palletId, biggestId = newest Pallet
				ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					palletId = rs.getInt("palletId");
				}
				ps.close();

			} catch (SQLException e) {

				e.printStackTrace();
				return Jsonizer.anythingToJson("error", "status");
			}

		}

		else {
			return Jsonizer.anythingToJson("unknown cookie", "status");
		}

		return Jsonizer.anythingToJson(palletId, "id");
	}

	private boolean cookieExists(String cookieName) {

		String sql = "SELECT cookieName FROM Cookies WHERE cookieName = ?";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, cookieName);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				ps.close();
				return true;
			}
			ps.close();
			

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return false;

	}

	private void subtractIngredients(String cookieName) {

		HashMap<String, Integer> ingredients = new HashMap<String, Integer>();

		try {
			conn.setAutoCommit(false); //Transaction

			// Find how much we have of each ingredient used in this cookie
			String sql = "SELECT StoredIngredients.ingredient, amountInStorage\n" + "FROM StoredIngredients\n"
					+ "INNER JOIN IngredientAmount on IngredientAmount.ingredient = StoredIngredients.ingredient\n"
					+ "WHERE cookieName = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, cookieName);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String ingredient = rs.getString("ingredient"); // add ingredient and total in storage into map															
				int amount = rs.getInt("amountInStorage");
				ingredients.put(ingredient, amount); //Map with total amount of each ingredient
			}
			ps.close();

			sql = "SELECT ingredient, amount FROM IngredientAmount WHERE cookieName = ?"; // find ingredients and amounts
			ps = conn.prepareStatement(sql);
			ps.setString(1, cookieName);
			rs = ps.executeQuery();

			while (rs.next()) {
				
				String ingredient = rs.getString("ingredient");														
				int amount = rs.getInt("amount");
				
				int newAmount = ingredients.get(ingredient) - amount*54; //Subtract total with the amount used to produce cookies
				ingredients.put(ingredient, newAmount); //Put in new total amount
			}
			ps.close();
			
			for (Map.Entry<String, Integer> entry : ingredients.entrySet()) { //Update each ingredient

				sql = "UPDATE StoredIngredients SET amountInStorage = ? WHERE ingredient = ?"; 
				ps = conn.prepareStatement(sql);
				ps.setInt(1, entry.getValue());
				ps.setString(2, entry.getKey());
				ps.executeUpdate();
				ps.close();

			}
			
			conn.commit();

		} catch (SQLException e) {

			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
		try {
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
