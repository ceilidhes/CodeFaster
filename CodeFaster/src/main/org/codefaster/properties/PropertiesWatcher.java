package org.codefaster.properties;


import com.intellij.execution.application.ApplicationConfiguration;
import org.apache.commons.lang3.SystemUtils;

import javax.swing.filechooser.FileNameExtensionFilter;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.*;
import java.nio.file.*;
import java.nio.file;

import java.util.Properties;

public class PropertyChangeListener implements Runnable {

    private static Properties properties;

    public PropertyChangeListener() {
        Path path = getPropertiesFileName();

        Properties p = new Properties(Path);


        File folder = new File("your/path");
        File[] listOfFiles = folder.listFiles();

    }

    public void run() {
            register(this.fullFilePath);

    }

    private void register(final String file) throws IOException {
        final int lastIndex = file.lastIndexOf("/");
        String dirPath = file.substring(0, lastIndex + 1);
        String fileName = file.substring(lastIndex + 1, file.length());
        this.configFileName = fileName;

        configurationChanged(file);
        startWatcher(dirPath, fileName);
    }

    private void startWatcher(String dirPath, String file) throws IOException {
        final WatchService watchService = FileSystems.getDefault()
                .newWatchService();
        Path path = Paths.get(dirPath);
        path.register(watchService, ENTRY_MODIFY);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    watchService.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        WatchKey key = null;
        while (true) {
            try {
                key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.context().toString().equals(configFileName)) {
                        configurationChanged(dirPath + file);
                    }
                }
                boolean reset = key.reset();
                if (!reset) {
                    System.out.println("Could not reset the watch key.");
                    break;
                }
            } catch (Exception e) {
                System.out.println("InterruptedException: " + e.getMessage());
            }
        }
    }

    public void configurationChanged(final String file) {
        System.out.println("Refreshing the configuration.");
        ApplicationConfiguration.getInstance().initilize(file);
    }


    private void create(){
        String homedir = getDirectory();
        String home = System.getProperty("user.home");
        if(currentUsersHomeDir + File.separator + ".CodeFaster";)

    }

    private String getDirectory(){
        String home = "/home";
        if( SystemUtils.IS_OS_WINDOWS ){
            home = System.getProperty("USERPROFILE");
        } else {
            home = System.getProperty("user.home");
        }
        testDirectoryExists(home);
        createDirectory(home, ".FasterCode");
        return  home + File.separator + ".FasterCode" + File.separator;

    }

    private String getPropertiesFileName(){
        return  "FasterCode.properties";
    }

    protected void testDirectoryExists(String directoryPath){
        File f  = new File(Paths.get(directoryPath));
        boolean exist =  f.isDirectory();
        if(!exist){
            throw new InvalidPathException(directoryPath, "Path does not exist");
        }
    }

    private void createDirectory(String directoryPath){
        File f  = new File(directoryPath);
        boolean directoriesExist = true;
        if(!f.isDirectory()){
          directoriesExist = f.mkdirs();
        }
        if(!directoriesExist){
            throw new InvalidPathException(directoryPath, "Directory Path " + directoryPath + " not created ");
        }
    }

    protected void listFilesInDirectory(Path path) throws IOException {

        String extension = "properties";
        File folder = path.toFile();
        if(!folder.isDirectory()){
            throw new InvalidPathException(path, "Directory Path " + path + " is not a directory ");
        }
        FileNameExtensionFilter filter = new FileNameExtensionFilter(extension,extension);
        File[] files = folder.listFiles(filter);


        for(File file : files) {
            InputStream input = new FileInputStream(file);

            properties.load(input);
            input.close();
        }
    }

}