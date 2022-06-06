package org.example;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Labeled;
import org.example.util.ApplicationProperties;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Callable;

public class UserStorageAndConfiguration implements Serializable {

    /** the current selected Locale. */
    private static final ObjectProperty<Locale> locale;
    static {
        locale = new SimpleObjectProperty<>(getDefaultLocale());
        locale.addListener((observable, oldValue, newValue) -> {
            Locale.setDefault(newValue);
        });
    }

    public static ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    /**
     * gets the string with the given key from the resource bundle for the current locale and uses it as first argument
     * to MessageFormat.format, passing in the optional args and returning the result.
     * @param key message key
     * @param args optional arguments for the message
     * @return localized formatted string
     */

    public static String get(final String key, final Object... args) {
        ResourceBundle bundle = ResourceBundle.getBundle("/i18n/strings", getDefaultLocale());
        return MessageFormat.format(bundle.getString(key), args);
    }

    /**
     * creates a String binding to a localized String for the given message bundle key
     * @param key key
     * @return String binding
     */
    public static StringBinding createStringBinding(final String key, Object... args) {
        return Bindings.createStringBinding(() -> get(key, args), locale);
    }
    /**
     * creates a String Binding to a localized String that is computed by calling the given func
     * @param func function called on every change
     * @return StringBinding
     */
    public static StringBinding createStringBinding(Callable<String> func) {
        return Bindings.createStringBinding(func, locale);
    }

    /**
     * Generic method to bind localization string binding to any {@link Labeled} JavaFX component
     *
     * @param key
     * @param labeled
     * @param args
     * @param <L>
     */
    public static <L extends Labeled> void bindLocaleKey(L labeled, final String key, final Object... args) {
        labeled.setId(key);
        labeled.textProperty().bind(createStringBinding(key, args));
    }

//    UserStorageAndConfiguration.bindLocaleKey(title_language, "title.language");
//    UserStorageAndConfiguration.bindLocaleKey(title_language, "title.theme");

//    label.textProperty().bind(createStringBinding(() -> I18N.get("label.numSwitches", 1)));
//    label.textProperty().bind(UserStorageAndConfiguration.createStringBinding("title.language"));






    private static ResourceBundle resourceBundle;
    private static transient UserStorageAndConfiguration userStorageAndConfigurationInstance;
    public static final transient String SETTINGS_FILE_NAME = "settings.dat";
    private transient Set<String> availableLanguages;
    private transient Set<String> availableThemes;

    //serialized data
    private static final long serialVersionUID = 230;
    private String actualLanguage;
    private String actualTheme;

    private UserStorageAndConfiguration() {
        initConfiguration();
    }

    public static void loadData(){
        //Load persistence data
        try {
            FileInputStream input = new FileInputStream(UserStorageAndConfiguration.SETTINGS_FILE_NAME);
            ObjectInputStream inStream = new ObjectInputStream(input);
            userStorageAndConfigurationInstance = (UserStorageAndConfiguration) inStream.readObject();
            userStorageAndConfigurationInstance.initConfiguration();
        } catch (FileNotFoundException e){
            System.out.println(getString("msg.configNotFound"));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void initConfiguration() {
        initLanguages();
        initThemes();
    }

    private void initThemes() {
        availableThemes = new HashSet<>();
        String[] themeValues = ApplicationProperties.getInstance().readProperty("theme_list").split(";");
        availableThemes.addAll(Arrays.asList(themeValues));
    }

    /**
     * Assign the available languages according to the settings file in the i18n folder
     */
    private void initLanguages() {
        availableLanguages = new HashSet<>();
        String[] languageValues = ApplicationProperties.getInstance()
                .readProperty("language_list").split(";");
        availableLanguages.addAll(Arrays.asList(languageValues));
    }

    public String getActualLanguage() {
        if (actualLanguage == null){
            assignDefaultLanguage();
        }
        return actualLanguage;
    }

    private void assignDefaultLanguage() {
        String systemLang = System.getProperty("user.language");
        actualLanguage = null;
        availableLanguages.forEach(e -> {
            String[] sp = e.split("_");
            if (systemLang.equalsIgnoreCase(sp[0])){
                actualLanguage = e;
            }
        });

        if (actualLanguage == null){
            actualLanguage = "en_EN";
            System.out.println("❗ The detected system language has not been implemented in this application." +
                    "\n English assigned by default.");
        }
    }

    public String getActualTheme() {
        if (actualTheme == null){
            assignDefaultTheme();
        }
        return actualTheme;
    }

    private void assignDefaultTheme() {
        actualTheme = "Light"; //Default theme
    }

    public void setActualLanguage(String actualLanguage) {
        this.actualLanguage = actualLanguage;

        localeProperty().set(getDefaultLocale());
        Locale.setDefault(getDefaultLocale());

        saveData();
    }

    public void setActualTheme(String actualTheme) {
        this.actualTheme = actualTheme;
        saveData();
    }

    public static UserStorageAndConfiguration getInstance() {
        if (userStorageAndConfigurationInstance == null) {
            userStorageAndConfigurationInstance = new UserStorageAndConfiguration();
        }
        return userStorageAndConfigurationInstance;
    }

    private void saveData(){
        try {
            FileOutputStream output = new FileOutputStream(UserStorageAndConfiguration.SETTINGS_FILE_NAME);
            ObjectOutputStream outStream = new ObjectOutputStream(output);
            outStream.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getAvailableLanguages() {
        return availableLanguages;
    }

    public Set<String> getAvailableThemes() {
        return availableThemes;
    }

    public static Locale getDefaultLocale() {
        String[] language = UserStorageAndConfiguration.getInstance().getActualLanguage().split("_");
        Locale locale = new Locale(language[0], language[1]);
        return locale;
    }

    private static void setResourceBundleLanguage(){
        resourceBundle = ResourceBundle.getBundle("/i18n/strings", getDefaultLocale());
    }

    public static Parent getParentRoot(String file) throws IOException {
        setResourceBundleLanguage();
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(resourceBundle);
        loader.setLocation(Objects.requireNonNull(App.class.getResource(file + ".fxml")));

        return loader.load();
    }

    public static String getString(String str){
        if (resourceBundle == null){
            setResourceBundleLanguage();
        }
        return resourceBundle.getString(str);
    }

}