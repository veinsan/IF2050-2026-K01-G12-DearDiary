package dedi.model;

/**
 * User account entity. Mirrors the {@code pengguna} table.
 *
 * <p>Read-only session model: once constructed (typically by the login flow),
 * fields cannot be mutated. Changes such as password updates or role changes
 * must go through the data layer and re-load a fresh instance.
 *
 * <p>{@code role} is one of {@code "Researcher"} (full CRUD) or
 * {@code "Tim R&D"} (read-only). The set is enforced by a {@code CHECK} clause
 * on the table; role-based access control above the data layer is the
 * controller/service tier's responsibility.
 */
public class Pengguna {

    private final String username;
    private final String password;
    private final String role;

    public Pengguna(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public String getRole() { return role; }
}
