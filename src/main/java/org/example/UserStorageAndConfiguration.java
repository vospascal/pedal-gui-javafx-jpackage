package org.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import org.example.util.ApplicationProperties;

import java.io.*;
import java.util.*;

public class UserStorageAndConfiguration implements Serializable {
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
        actualLanguage = null; //Sin lenguaje inicial
        availableLanguages.forEach(e -> {
            String[] sp = e.split("_");
            if (systemLang.equalsIgnoreCase(sp[1])){
                actualLanguage = e;
            }
        });

        if (actualLanguage == null){
            actualLanguage = "en_UK";
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
        actualTheme = "light"; //Default theme
    }

    public void setActualLanguage(String actualLanguage) {
        this.actualLanguage = actualLanguage;
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

    public static Pane loadSource(String file) {
        Pane pane = null;

        try {
            pane = (Pane) getParentRoot(file);
        } catch (IOException e) {
            System.err.println(getString("bug.title") + " " + getString("bug.panelLoad"));
            e.printStackTrace();
            System.exit(0);
        }

        if (pane == null){
            System.err.println(getString("bug.title") + " " + getString("bug.panelLoad"));
            System.exit(0);
        }
        return pane;
    }


    private static void setResourceBundleLanguage(){
        String[] language = UserStorageAndConfiguration.getInstance().getActualLanguage().split("_");
        Locale locale = new Locale(language[0], language[1]);
        resourceBundle = ResourceBundle.getBundle("/i18n/strings", locale);
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