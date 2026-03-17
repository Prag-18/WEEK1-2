import java.util.*;
import java.util.concurrent.*;
class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;
    DNSEntry(String domain, String ipAddress, int ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + ttlSeconds * 1000;
    }
    boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}
public class DNS_cache {
    private final int MAX_CACHE_SIZE;
    private final Map<String, DNSEntry> cache;
    private int hits = 0;
    private int misses = 0;
    public DNS_cache(int maxSize) {
        this.MAX_CACHE_SIZE = maxSize;
        this.cache = new LinkedHashMap<>(16, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            synchronized (cache) {
                Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();
                while (it.hasNext()) {
                    if (it.next().getValue().isExpired()) {
                        it.remove();
                    }
                }
            }
        }, 5, 5, TimeUnit.SECONDS);
    }
    public String resolve(String domain) {
        synchronized (cache) {
            DNSEntry entry = cache.get(domain);
            if (entry != null && !entry.isExpired()) {
                hits++;
                return "Cache HIT → " + entry.ipAddress;
            } else {
                misses++;
                String ip = queryUpstreamDNS(domain);
                cache.put(domain, new DNSEntry(domain, ip, 5)); // TTL = 5s for demo
                return "Cache MISS → " + ip;
            }
        }
    }
    private String queryUpstreamDNS(String domain) {
        return "172.217." + new Random().nextInt(255) + "." + new Random().nextInt(255);
    }
    public String getCacheStats() {
        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);
        return String.format("Hit Rate: %.2f%%, Hits=%d, Misses=%d", hitRate, hits, misses);
    }
    public static void main(String[] args) throws InterruptedException {
        DNS_cache dnsCache = new DNS_cache(3);

        System.out.println(dnsCache.resolve("google.com"));
        System.out.println(dnsCache.resolve("google.com"));
        Thread.sleep(6000); // wait for TTL expiry
        System.out.println(dnsCache.resolve("google.com"));
        System.out.println(dnsCache.resolve("yahoo.com"));
        System.out.println(dnsCache.resolve("bing.com"));
        System.out.println(dnsCache.resolve("duckduckgo.com"));
        System.out.println(dnsCache.getCacheStats());
    }
}