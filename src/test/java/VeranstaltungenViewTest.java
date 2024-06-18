import com.example.application.Application;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.Environment;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
@Tag("playwright")
public class VeranstaltungenViewTest {

    @LocalServerPort
    private int port;

    // Um die application-test.properties zu verwenden muss die Umgebung auf test gesetzt werden
    // dazu unter Run auf Edit Configurations → Environment variables → Name: SPRING_PROFILES_ACTIVE, Value: test

    @Autowired
    private Environment env;

    private static Playwright playwright;
    private static Browser browser;

    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
        //Headless Modus kann auf true gesetzt werden, um den Browser nicht zu öffnen, wenn false wird der Browser geöffnet
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @AfterAll
    static void teardown() {
        browser.close();
        playwright.close();
    }

    private void performLogin(Page page) {
        String loginUrl = "http://localhost:" + port + "/login";

        page.navigate(loginUrl);
        page.waitForLoadState(); // Sicherstellen, dass die Seite geladen ist

        // Anmeldeinformationen eingeben
        page.fill("input[name='username']", "admin");
        page.fill("input[name='password']", "admin");

        // Klicken auf den Anmeldebutton
        page.locator("vaadin-button[role='button']:has-text('Log in')").click();

        // Warten, bis die Hauptseite nach dem Login geladen ist
        page.waitForURL("http://localhost:" + port + "/");
    }

    @Test
    public void testAddVeranstaltungDialogWithoutTeilnehmer() {
        System.out.println("Aktive Profile: " + String.join(", ", env.getActiveProfiles()));
        Page page = browser.newPage();

        // Verwenden Sie den dynamischen Port
        String url = "http://localhost:" + port + "/";

        page.navigate(url);
        page.waitForLoadState();

        performLogin(page);

        page.locator("vaadin-button[theme='icon'][role='button'][id='veranstaltung-erstellen-button']").click();

        assertThat(page.locator("h2:has-text('Veranstaltung hinzufügen')")).isVisible();

        page.fill("input[id='input-vaadin-text-field-18']", "Test Veranstaltung");
        page.fill("input[id='input-vaadin-date-picker-19']", "26.6.2024");


        page.locator("input[id='input-vaadin-date-picker-19']").press("Enter");
        page.waitForTimeout(500);

        page.locator("vaadin-button[theme='primary'][role='button']:has-text('Speichern')").click();

        assertThat(page.locator("div[id='veranstaltung-info']:has-text('Test Veranstaltung')")).isVisible();

        page.close();
    }

    @Test
    public void testAddTeilnehmerToVeranstaltung() {
        System.out.println("Aktive Profile: " + String.join(", ", env.getActiveProfiles()));
        Page page = browser.newPage();

        // Verwenden Sie den dynamischen Port
        String url = "http://localhost:" + port + "/";

        page.navigate(url);
        page.waitForLoadState();

        performLogin(page);

        page.locator("div[id='veranstaltung-info']:has-text('Test Veranstaltung')").click();

        page.locator("vaadin-button[id='teilnehmer-hinzufuegen-button']:has-text('Teilnehmer hinzufügen')").click();

        assertThat(page.locator("h2:has-text('Teilnehmer hinzufügen')")).isVisible();

        page.locator("vaadin-checkbox[id='selectAllCheckbox']").click();

        page.locator("vaadin-button[theme='error'][role='button']:has-text('Hinzufügen')").click();

        assertThat(page.locator("span[class='teilnehmer-name-liste']:has-text('Laura Meyer')")).isVisible();

        page.close();
    }

}