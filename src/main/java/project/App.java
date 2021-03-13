/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package project;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

public class App {
    public static String determineNote(int score,int limitation,int standard_deviation){
        if(score >= limitation + standard_deviation*6)return "AA";
        else if(score >= limitation + standard_deviation*5)return "BA";
        else if(score >= limitation + standard_deviation*4)return "BB";
        else if(score >= limitation + standard_deviation*3)return "CB";
        else if(score >= limitation + standard_deviation*2)return "CC";
        else if(score >= limitation + standard_deviation)return "DC";
        else if(score >= limitation)return "DD";
        else return "FF";
    }
    public static String orderAndScoreStudents(ArrayList<String> students,ArrayList<Integer> scores,Integer limitation, Integer standard_deviation) {
      if(students == null || scores == null)return "Please enter some student/score inputs";
      if(limitation <= 0 | limitation > 100)return "Please enter valid positive limitation";
      if(standard_deviation <= 0 | standard_deviation > 100)return "Please enter valid positive standard deviation";
      for(int i = 0;i < scores.size();i++){
        if(scores.get(i) < 0 | scores.get(i) > 100)return "Please enter valid positive scores";
      }
      int length = Math.min(students.size(), scores.size());
      Map<String,Integer> map = new HashMap<>();
      for(int i = 0;i < length;i++){
        map.put(students.get(i), scores.get(i));
      }
      map = map.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(e1,e2) -> e1, LinkedHashMap::new));
      String message = "";
      for(Map.Entry<String,Integer> entry : map.entrySet()){
          String note = determineNote(entry.getValue(), limitation, standard_deviation);
          if(!note.equals("FF")){
            message += entry.getKey()+" got "+note+" and passed the lesson\n";
          }
          else message += entry.getKey()+" got "+note+" and failed the lesson\n";
      }
      if(students.size() > length)message += "\nNote:\n";
      for(int i = length;i < students.size();i++){
        message += "There is not score information about the student : "+students.get(i)+"\n";
      }
      if(students.size() == length & scores.size() > length){
        message += "\nNote:\n";
        for(int i = length;i < scores.size();i++){
          message += "There is not student who takes : "+scores.get(i)+"\n";
        }
      }
      return message;
    }

    public static void main(String[] args) {
        Logger logger = LogManager.getLogger(App.class);
        int port = Integer.parseInt(System.getenv("PORT"));
        port(port);
        logger.error("Current port number:" + port);
        //int number = getHerokuAssignedPort();
        //port(number);
        
        get("/", (req, res) -> "Welcome To My Website !\nPlease add compute to URL to access the application");

        MustacheTemplateEngine engine = new MustacheTemplateEngine();
        get("/compute",
        (rq, rs) -> {
          Map<String, String> map = new HashMap<String, String>();
          map.put("result", "not computed yet!");
          return new ModelAndView(map, "compute.mustache");
        }, engine);
        
        post("/compute", (req, res) -> {
          String input1 = req.queryParams("input1");
          java.util.Scanner sc1 = new java.util.Scanner(input1);
          sc1.useDelimiter("[;\r\n]+");
          java.util.ArrayList<String> inputList = new java.util.ArrayList<>();
          while (sc1.hasNext())
          {
            String student = sc1.next();
            inputList.add(student);
          }
          sc1.close();
          System.out.println(inputList);

          String input2 = req.queryParams("input2");
          java.util.Scanner sc2 = new java.util.Scanner(input2);
          sc2.useDelimiter("[;\r\n]+");
          java.util.ArrayList<Integer> inputList2 = new java.util.ArrayList<>();
          while (sc2.hasNext())
          {
            int value = Integer.parseInt(sc2.next().replaceAll("\\s",""));
            inputList2.add(value);
          }
          sc2.close();
          System.out.println(inputList2);

          String input3 = req.queryParams("input3").replaceAll("\\s","");
          int limitation = Integer.parseInt(input3);
          String input4 = req.queryParams("input4").replaceAll("\\s","");
          int standard_deviation = Integer.parseInt(input4);
         
          // get result
          String result = orderAndScoreStudents(inputList, inputList2, limitation, standard_deviation);
          Map<String, String> map = new HashMap<String, String>();
          map.put("result", result);
          return new ModelAndView(map, "compute.mustache");
        }, new MustacheTemplateEngine());

    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; 
    }

      
    
}
