package authen;

public class SetSession {
    private static int userId;
    private static String username;
    private static String role;
    private static boolean loggedIn = false;

    public static void startSession(int id, String user, String userRole) {
        userId = id;
        username = user;
        role = userRole;
        loggedIn = true;
        System.out.println("Session started for: " + username + " (" + role + ")");
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static int getUserId() { return userId; }
    public static String getUsername() { return username; }
    public static String getRole() { return role; }

    public static void endSession() {
        userId = 0;
        username = null;
        role = null;
        loggedIn = false;
        System.out.println("Session ended.");
    }
}
