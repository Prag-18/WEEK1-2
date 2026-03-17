import java.util.*;

public class Username {
    private Map<String, Integer> userMap = new HashMap<>();
    private Map<String, Integer> attemptMap = new HashMap<>();
    public void registerUser(String username, int userId) {
        userMap.put(username, userId);
    }
    public boolean checkAvailability(String username) {
        recordAttempt(username);
        return !userMap.containsKey(username);
    }
    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        if (userMap.containsKey(username)) {
            suggestions.add(username + "1");
            suggestions.add(username + "2");
            suggestions.add(username.replace("_", "."));
        }
        return suggestions;
    }
    private void recordAttempt(String username) {
        attemptMap.put(username, attemptMap.getOrDefault(username, 0) + 1);
    }
    public String getMostAttempted() {
        String mostVisited = "";
        int maxViews = 0;
        for (Map.Entry<String, Integer> entry : attemptMap.entrySet()) {
            if (entry.getValue() > maxViews) {
                maxViews = entry.getValue();
                mostVisited = entry.getKey();
            }
        }
        return mostVisited;
    }
    public static void main(String[] args) {
        Username system = new Username();
        system.registerUser("john_doe", 12345);
        system.registerUser("jane_smith", 67890);
        System.out.println("john_doe available? " + system.checkAvailability("john_doe"));
        System.out.println("jane_smith available? " + system.checkAvailability("jane_smith"));
        System.out.println("new_user available? " + system.checkAvailability("new_user"));
        System.out.println("Suggestions for john_doe: " + system.suggestAlternatives("john_doe"));
        System.out.println("Most attempted: " + system.getMostAttempted());
    }
}