import Models.Nationality;
import Models.RaceBarLine;
import Models.RankingObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RaceBar {

  public static String start = "2021-10-19";
  public static String end = "2022-04-06";
  public static SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
  public static String league = "nba";
  public static String stat = "pie";
  public static String mode = "all";
  public static int nbPlayer = 10;
  public static String nat = "europe";

  public static String CSV_FILE_NAME = "race_bar_" + stat + "_" + league + "_" + nat + ".csv";

  public String convertToCSV(RaceBarLine bar) {
    return bar.toString();
  }

  private void writeCsv(List<RaceBarLine> bars){
    File csvOutputFile = new File(CSV_FILE_NAME);
    try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
      pw.println("name,value,year,lastValue,rank,tid");
      bars.stream()
          .map(this::convertToCSV)
          .forEach(pw::println);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {

    String nationalityArgs = nat;
    if(nat.equals("europe")) nationalityArgs = String.join("-",new Nationality().europe);
    if(nat.equals("all")) nationalityArgs = String.join("-",new Nationality().all);
    if(!nationalityArgs.isEmpty())
      nationalityArgs = "/"+nationalityArgs;

    Map<String,Float> lastValues = new HashMap<>();
    List<RaceBarLine> bars = new ArrayList<>();

    try {
      Date currentDate = dt.parse(start);
      currentDate = new Date(currentDate.getTime() + (1000 * 60 * 60 * 24 * 7)); // start with the first week to include players with 2-3 games
      Date endDate = dt.parse(end);
      do{
        System.out.println(currentDate);
        currentDate = new Date(currentDate.getTime() + (1000 * 60 * 60 * 24));

        // call
        HttpClient httpclient = HttpClients.createDefault();
        try {
          HttpResponse response = httpclient.execute(new HttpGet(
              // TODO : set a minimum games (ratio of the number of iterations)
              String.format("https://api.datanostra.app/ranking/player/%s/%s/%s/%s/1/%s/avg/DESC/0/48/0/100%s",
                  league,
                  start.replace("-",""),
                  dt.format(currentDate).replace("-",""),
                  stat,
                  nbPlayer,
                  nationalityArgs)
          ));
          String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

          final JSONArray array = new JSONArray(responseBody);
          final int n = array.length();
          for (int i = 0; i < n; ++i) {
            ObjectMapper mapper = new ObjectMapper();
            RankingObject obj = mapper.readValue(array.getJSONObject(i).toString(), RankingObject.class);
            String name = obj.getFn()+" "+obj.getLn();
            Float value = obj.getPie();
            bars.add(new RaceBarLine(
              name, value, dt.format(currentDate),
              lastValues.containsKey(name) ? lastValues.get(name) : value,
              i+1,
                obj.getHcl()
            ));
            lastValues.put(name, value);
          }
        } catch (ClientProtocolException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        } catch (JSONException e) {
          e.printStackTrace();
        }

      } while( currentDate.getTime() < endDate.getTime());

      RaceBar rb = new RaceBar();
      rb.writeCsv(bars);

    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

}
