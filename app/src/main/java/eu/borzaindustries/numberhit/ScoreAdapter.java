package eu.borzaindustries.numberhit;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.provider.Settings.Secure;


public class ScoreAdapter {
	private static String SERVER = "http://borzaindustries.eu/scores/ws.php?";

	private static String getDeviceId(Context context) {
		return Secure
				.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}

	public static String downloadStringFromUrl(String address) {
		try {
			URL url = new URL(address);
			URLConnection conn = url.openConnection();
			conn.connect();
			int length = 2048;
			InputStream input = new BufferedInputStream(url.openStream());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte data[] = new byte[1024];
			int count;
			// Download file
			for (@SuppressWarnings("unused")
			int total = 0; (count = input.read(data, 0, 1024)) != -1; total += count) {
				bos.write(data, 0, count);
			}
			String str = new String(bos.toByteArray());
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String md5(String s) {
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			digest.update(s.getBytes("UTF-8"));
			final byte[] data = digest.digest();
			final StringBuilder sbMd5Hash = new StringBuilder();
			for (byte element : data) {
				sbMd5Hash.append(Character.forDigit((element >> 4) & 0xf, 16));
				sbMd5Hash.append(Character.forDigit(element & 0xf, 16));
			}
			return sbMd5Hash.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static StartData doStart(Context context) {
		// Log.d("TAG", "doStart start");
		String deviceID = getDeviceId(context);
		// HACK
		// deviceID = "axarydax";
		String url = SERVER + String.format("action=start&dev=%s", deviceID);
		String json = downloadStringFromUrl(url);
		// Log.d("TAG", "doStart got data");
		// here are 3 json objects:
		// total: contains "top:name, rank, value" and "my:name, rank, value"
		// top_score: array of "b,val"
		// my_score: array of "b,val,pos
		if (json == null)
			return null;
		try {
			StartData data = new StartData();
			ArrayList<ScoreInfo> scores = new ArrayList<ScoreInfo>();
			JSONObject jo = new JSONObject(json);
			// TOTAL
			JSONObject total = jo.getJSONObject("total");
			JSONObject top = total.getJSONObject("top");
			data.bestTotalScore = top.optInt("value");
			JSONObject my = total.getJSONObject("my");
			data.myTotalPosition = my.optInt("rank");
			data.myTotalScore = my.optInt("value");
			// TOP_SCORE
			JSONArray top_score = jo.getJSONArray("top_score");
			for (int i = 0; i < top_score.length(); i++) {
				JSONObject one_top_score = top_score.getJSONObject(i);
				data.bestScores.put(one_top_score.getInt("b"),
						one_top_score.getInt("val"));
			}
			// MY_SCORE
			JSONArray my_score = jo.getJSONArray("my_score");
			for (int i = 0; i < my_score.length(); i++) {
				JSONObject scoreObj = my_score.getJSONObject(i);
				ScoreInfo info = new ScoreInfo();
				info.value = scoreObj.getInt("val");
				info.position = scoreObj.getInt("pos");
				info.board = scoreObj.getInt("b");
				scores.add(info);
			}
			data.scores = scores;
			// Log.d("TAG", "doStart end");
			return data;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static EnterScoreResult enterScore(Context context, int score,
			int board, String stuff) {
		String deviceID = getDeviceId(context);
		char[] chars = { 0x21, 0x77, 0x74, 0x66, 0x53, 0x61, 0x6c, 0x74, 0x5e }; // !wtfSalt^
		String salt = new String(chars);
		String str = deviceID.length() + board + score
				+ (score % (board * 2 + 1)) + (score - (board * 17 - 9)) + salt
				+ deviceID;
		String hash = md5(str);
		if (hash.equals(null))
			return null;
		String url = SERVER
				+ String.format(
						"action=add&board=%d&val=%d&dev=%s&hash=%s&stuff=%s",
						board, score, deviceID, hash, stuff);
		// Log.e("ScoreAdapter","url:"+url);
		String response = downloadStringFromUrl(url);
		try {
			// Log.d("ScoreAdapter", "enterScore() response:" + response);
			JSONObject jo = new JSONObject(response);
			// example: {"old":1,"new":1,"better_score":false}
			EnterScoreResult res = new EnterScoreResult();
			res.better = jo.getBoolean("better_score");
			res.oldPosition = jo.getInt("old");
			res.newPosition = jo.getInt("new");
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}