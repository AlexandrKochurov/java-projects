import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
            MongoClient mongoClient = new MongoClient( "127.0.0.1" , 27017 );

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        String command = "C:\\Program Files\\MongoDB\\Server\\4.2\\bin\\mongoimport.exe --db test --collection students --type csv --file /src/main/resources/mongo.csv --fields name,age,courses";
        try {
            process = runtime.exec(command);
            System.out.println("Reading csv into Database");

        } catch (Exception e){
            System.out.println("Error executing " + command + e.toString());
        }

        MongoDatabase database = mongoClient.getDatabase("test");
        MongoCollection<Document> students = database.getCollection("students");

        //Два варианта вывода общего кол-ва студентов
        System.out.println("Общее кол-во студентов: " + students.count());

        List<Document> studentsCount = students.find().into(new ArrayList<>());
        System.out.println("Общее кол-во студентов: " + studentsCount.size());

        //Два варианта вывода кол-ва студентов старше сорока лет
        int count = 0;
        for(Document doc: studentsCount){
            if(Integer.parseInt(doc.get("age").toString()) > 40){
                count++;
            }
        }
        System.out.println("Кол-во студентов старше 40: " + count);

        BsonDocument forty = BsonDocument.parse("{age : {$gt: 40}}");
        List<Document> fortyCount = students.find(forty).into(new ArrayList<>());
        System.out.println("Кол-во студентов старше 40: " + fortyCount.size());

        //Вывод имени самого молодого студента
        BsonDocument youngest = BsonDocument.parse("{age : 1}");
        Document youngestMan = students.find().sort(youngest).first();
        System.out.println("Самого молодого студента зовут: " + youngestMan.get("name"));

        //Список курсов самого старого студента
        BsonDocument courses = BsonDocument.parse("{age : -1}");
        Document oldestMan = students.find().sort(courses).first();
        System.out.println("Список курсов самого старого студента: " + oldestMan.get("courses"));
    }
}