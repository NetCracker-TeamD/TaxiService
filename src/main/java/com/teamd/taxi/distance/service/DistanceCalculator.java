package com.teamd.taxi.distance.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public final class DistanceCalculator {

	public static int calculateDistance(String a, String b) {
		String url = null;
		String json = null;

		try {
			url = "http://maps.googleapis.com/maps/api/directions/json?origin="
					+ URLEncoder.encode(a, "UTF-8") + "&destination="
					+ URLEncoder.encode(b, "UTF-8") + "&region=ua&sensor=false";
		} catch (UnsupportedEncodingException e2) {
			System.out.println("something strange with encoding");
			e2.printStackTrace();
		}

		try {
			json = getJson(url);
		} catch (Exception e1) {
			System.out.println("something strange with your json");
			e1.printStackTrace();
		}

		JSONParser parser = new JSONParser();

		Object obj = null;
		try {
			obj = parser.parse(json);
		} catch (ParseException e) {
			System.out.println("error with parsing");
			e.printStackTrace();
		}
		JSONObject jsonObj = (JSONObject) obj;
		JSONArray ja = (JSONArray) jsonObj.get("routes");
		jsonObj = (JSONObject) ja.get(0);
		ja = (JSONArray) jsonObj.get("legs");
		jsonObj = (JSONObject) ja.get(0);
		jsonObj = (JSONObject) jsonObj.get("distance");
		return Integer.parseInt(jsonObj.get("value").toString());
	}

	public static String getJson(String pageAddress) throws Exception {
		StringBuilder sb = new StringBuilder();
		URL pageURL = new URL(pageAddress);
		URLConnection uc = pageURL.openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				uc.getInputStream()));
		try {
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}
		} finally {
			br.close();
		}
		return sb.toString();
	}

}