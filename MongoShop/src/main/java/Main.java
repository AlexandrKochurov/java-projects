import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        MongoShop ms = new MongoShop();
        ms.initialization();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter command: ");
            String command = scanner.nextLine();
            switch (command) {
                case "add_shop":
                    System.out.println("Insert name: ");
                    String addName = scanner.nextLine();
                    ms.addShop(addName);
                    break;
                case "add_goods":
                    System.out.println("Insert goods name: ");
                    String goodsName = scanner.nextLine();
                    System.out.println("Insert goods price: ");
                    int price = Integer.parseInt(scanner.nextLine());
                    ms.addGoods(goodsName, price);
                    break;
                case "ex_goods":
                    System.out.println("Insert goods name: ");
                    String exGoods = scanner.nextLine();
                    System.out.println("Insert shop name: ");
                    String exShop = scanner.nextLine();
                    ms.addGoodsToShop(exGoods, exShop);
                    break;
                case "stats":
                    ms.statistics();
                    break;
                default:
                    System.out.println("Available commands are: add_shop add_goods ex_goods stats");
            }

        }
    }
}
