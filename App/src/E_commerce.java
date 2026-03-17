import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class E_commerce {
    private static final Map<String, AtomicInteger> stockMap = new ConcurrentHashMap<>();
    private static final Map<String, Queue<Integer>> waitingListMap = new ConcurrentHashMap<>();
    public void addProduct(String productId, int initialStock) {
        stockMap.put(productId, new AtomicInteger(initialStock));
        waitingListMap.put(productId, new ConcurrentLinkedQueue<>());
    }
    public String checkStock(String productId) {
        AtomicInteger stock = stockMap.get(productId);
        if (stock == null) return "Product not found";
        return stock.get() + " units available";
    }
    public static String purchaseItem(String productId, int userId) {
        AtomicInteger stock = stockMap.get(productId);
        if (stock == null) return "Product not found";
        while (true) {
            int currentStock = stock.get();
            if (currentStock > 0) {
                if (stock.compareAndSet(currentStock, currentStock - 1)) {
                    return "Success, " + (currentStock - 1) + " units remaining";
                }
            } else {
                waitingListMap.get(productId).add(userId);
                int position = waitingListMap.get(productId).size();
                return "Added to waiting list, position #" + position;
            }
        }
    }
    public List<Integer> getWaitingList(String productId) {
        Queue<Integer> queue = waitingListMap.get(productId);
        if (queue == null) return Collections.emptyList();
        return new ArrayList<>(queue);
    }
    public static void main(String[] args) throws InterruptedException {
        E_commerce system = new E_commerce();
        system.addProduct("IPHONE15_256GB", 1);
        System.out.println(purchaseItem("IPHONE15_256GB",1234));
        System.out.println(purchaseItem("IPHONE15_256GB", 2345));
        System.out.println(purchaseItem("IPHONE15_256GB", 3456));
        System.out.println("Final stock: " + system.checkStock("IPHONE15_256GB"));
        System.out.println("Waiting list: " + system.getWaitingList("IPHONE15_256GB"));
    }
}