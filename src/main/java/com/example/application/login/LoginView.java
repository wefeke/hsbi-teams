package com.example.application.login;

import com.example.application.security.AuthenticatedUser;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * Eine Klasse, die die Login-Ansicht der Anwendung repräsentiert.
 * Hier können Benutzer sich anmelden, um Zugang zu den Funktionen der Anwendung zu erhalten.
 *
 * @author Kennet
 */
@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {
    LoginI18n i18n;

    private final AuthenticatedUser authenticatedUser;

    /**
     * Konstruktor für die LoginView Klasse.
     * Hier werden die UI-Elemente initialisiert und die Aktionen für die UI-Elemente festgelegt.
     *
     * @author Kennet
     * @param authenticatedUser Ein AuthenticatedUser-Objekt, das Informationen über den aktuell authentifizierten Benutzer enthält.
     */
    public LoginView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));

        i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("HSBI Teams");
        i18n.getHeader().setDescription("Einfaches Einteilen IHRER Studenten");
        i18n.setForm(new LoginI18n.Form());
        i18n.getForm().setTitle("Anmelden");
        i18n.getForm().setPassword("Passwort");
        i18n.getForm().setUsername("Username");
        i18n.getForm().setForgotPassword("Passwort vergessen");
        i18n.getForm().setSubmit("Anmelden");
        i18n.setAdditionalInformation(null);
        i18n.setErrorMessage(new LoginI18n.ErrorMessage());
        i18n.getErrorMessage().setTitle("Anmeldung fehlgeschlagen");
        i18n.getErrorMessage().setMessage("Falscher Username oder falsches Passwort. Wenn Sie Ihr Passwort vergessen haben, wenden Sie sich an Ihren Administrator.");

        setI18n(i18n);

        Button registerButton = new Button("Registrieren", event -> getUI().ifPresent(ui -> ui.navigate("registration")));
        registerButton.setWidthFull();

        getFooter().add(registerButton);

        setForgotPasswordButtonVisible(false);
        setOpened(true);
    }

    /**
     * Wird aufgerufen, bevor der Benutzer die Seite betritt.
     * Überprüft, ob der Benutzer bereits eingeloggt ist und leitet ihn gegebenenfalls zur Hauptseite weiter.
     * Zeigt außerdem eine Fehlermeldung an, wenn der Parameter "error" in der URL vorhanden ist.
     *
     * @author Kennet
     * @param event Ein BeforeEnterEvent-Objekt, das Informationen über das bevorstehende Betreten der Seite enthält.
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        if (authenticatedUser.get().isPresent()) {
            // Already logged in
            setOpened(false);
            event.forwardTo("");
        }

        if (event.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("lockerror")) {
            setError(true);
            i18n.getErrorMessage().setMessage("Ihr Account wurde gesperrt. Bitte wenden Sie sich an Ihren Administrator.");
        } else if(event.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            setError(true);
            i18n.getErrorMessage().setMessage("Falscher Username oder falsches Passwort. Wenn Sie Ihr Passwort vergessen haben, wenden Sie sich an Ihren Administrator.");
        }
        else {
            setError(false);
        }
        setI18n(i18n);
    }
}
