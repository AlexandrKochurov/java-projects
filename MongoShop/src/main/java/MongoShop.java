import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;

import static com.mongodb.client.model.Updates.push;

public class MongoShop {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> shops;
    private static MongoCollection<Document> goods;

    public void initialization(){
        CodecRegistry pojoCodecRegistry = org.bson.codecs
                .configuration
                .CodecRegistries
                .fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), org.bson.codecs.configuration.CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        mongoClient = new MongoClient("127.0.0.1", 27017);
        database = mongoClient.getDatabase("test").withCodecRegistry(pojoCodecRegistry);
        shops = database.getCollection("MongoShop");
        goods = database.getCollection("Goods");
        //goods.drop();
        //shops.drop();
    }

    public void addShop(String name) {
        BasicDBObject findShop = new BasicDBObject();
        findShop.put("Name", name);
        if(shops.find(findShop).first() == null) {
            Document shop = new Document()
                    .append("Name", name)
                    .append("List", Collections.emptyList());
            shops.insertOne(shop);
        } else {
            System.out.println("This shop is already exists!");
        }
    }

    public void addGoods(String name, int price) {
        BasicDBObject findGoods = new BasicDBObject();
        findGoods.put("Name", name);
        if(goods.find(findGoods).first() == null) {
            Document good = new Document()
                    .append("Name", name)
                    .append("Price", price);
            goods.insertOne(good);
        } else {
            System.out.println("This goods is already exists!");
        }
    }

    public void addGoodsToShop(String goodsName, String shopName) {
        BasicDBObject checkGoods = new BasicDBObject();
        BasicDBObject checkShops = new BasicDBObject();
        checkGoods.put("Name", goodsName);
        checkShops.put("Name", shopName);
        if(goods.find(checkGoods).first() != null && shops.find(checkShops).first() != null) {
            shops.updateOne(Filters.eq("Name", shopName), push("List", goodsName));
        } else {
            System.out.println("The shop or goods doesn't exist!");
        }
    }

    public void statistics() {
        goods.aggregate(
                Arrays.asList(
                        Aggregates.lookup("MongoShop", "Name", "List", "shops"),
                        Aggregates.unwind("$shops"),
                        Aggregates.group("$shops.Name",
                                Accumulators.avg("avgPrice", "$Price"),
                                Accumulators.min("minPrice", "$Price"),
                                Accumulators.max("maxPrice", "$Price"),
                                Accumulators.sum("sum", "$Price")


                        )
                )
        ).forEach((Consumer<Document>) document -> {
                    int count = (int) ((int) document.getInteger("sum") / document.getDouble("avgPrice"));
                    int countLt100 = getAvg(document.getString("_id"));
                    System.out.println("Магазин " + document.getString("_id"));
                    System.out.println("\tВсего в магазине " + document.getString("_id") + " " + count + " товаров");
                    System.out.println("\tСредняя цена товаров в магазине " + document.getString("_id") + " составляет - " + document.getDouble("avgPrice"));
                    System.out.println("\tМинимальная цена товаров в магазине " + document.getString("_id") + " составляет - " + document.getInteger("minPrice"));
                    System.out.println("\tМаксимальная цена товаров в магазине " + document.getString("_id") + " составляет - " + document.getInteger("maxPrice"));
                    System.out.println("\tВсего в магазине " + document.getString("_id") + " " + countLt100 + " товаров дешевле 100 рублей");
                }
        );


    }

    private int getAvg(String shopName) {
        final int[] count = {0};
        BsonDocument query = BsonDocument.parse("{\"goods.Price\": {$lt: 100}}");
        shops.aggregate(
                Arrays.asList(
                        Aggregates.lookup("Goods", "List", "Name", "goods"),
                        Aggregates.unwind("$goods"),
                        Aggregates.match(query),
                        Aggregates.group("$Name",
                                Accumulators.avg("avgPrice", "$goods.Price"),
                                Accumulators.min("minPrice", "$goods.Price"),
                                Accumulators.max("maxPrice", "$goods.Price"),
                                Accumulators.sum("sum", "$goods.Price")


                        )
                )
        ).forEach((Consumer<Document>) document -> {
            if (document.getString("_id").equals(shopName)) {
                int countLt100 = (int) ((int) document.getInteger("sum") / document.getDouble("avgPrice"));
                count[0] = countLt100;
            }

        });

        return count[0];
    }
}
