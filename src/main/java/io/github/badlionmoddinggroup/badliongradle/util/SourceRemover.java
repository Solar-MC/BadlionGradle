package io.github.badlionmoddinggroup.badliongradle.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class SourceRemover {

    public static void main(String[] args) throws IOException {
        File fileWithSrc = new File(args[0].replace('\\', '/'));
        File fileWithoutSrc = new File(args[1].replace('\\', '/'));
        System.out.println("Copying jar");
        Path fileToCopyTo = fileWithoutSrc.toPath();
        Path originalPath = fileWithSrc.toPath();
        Files.copy(originalPath, fileToCopyTo, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Removing Minecraft and its assets");
        try {
            deleteZipDir(fileWithoutSrc.getPath().replace('\\', '/').replace("C:/", "/"), "assets");
            deleteZipDir(fileWithoutSrc.getPath().replace('\\', '/').replace("C:/", "/"), "net/minecraft");
            deleteZipDir(fileWithoutSrc.getPath().replace('\\', '/').replace("C:/", "/"), "com");
        } catch (Exception exception) {
            System.out.println("An exception occurred when removing minecraft code");
            exception.printStackTrace();
        }
    }

    /**
     * modified version of https://stackoverflow.com/questions/41631210/deleting-file-inside-a-zip-in-java
     *
     * @param zipFilePath path to zip
     * @param fileName    file to remove
     * @throws Exception exeption if fails
     */
    public static void deleteFile(String zipFilePath, String fileName) throws Exception {
        Map<String, String> zip_properties = new HashMap<>();
        zip_properties.put("create", "false");

        /* Specify the path to the ZIP File that you want to read as a File System */
        URI zip_disk = URI.create("jar:file:" + zipFilePath);

        /* Create ZIP file System */
        try (FileSystem zipfs = FileSystems.newFileSystem(zip_disk, zip_properties)) {
            /* Get the Path inside ZIP File to delete the ZIP Entry */
            Path pathInZipfile = zipfs.getPath(fileName);
            System.out.println("About to delete an entry from ZIP File" + pathInZipfile.toUri());
            /* Execute Delete */
            Files.delete(pathInZipfile);
            System.out.println("File successfully deleted");
        }
    }

    public static void deleteZipDir(String zipFilePath, String folderName) throws Exception {
        Map<String, String> zip_properties = new HashMap<>();
        zip_properties.put("create", "false");

        /* Specify the path to the ZIP File that you want to read as a File System */
        URI zip_disk = URI.create("jar:file:" + new File(zipFilePath).getAbsolutePath().replace('\\', '/').replace("C:/", "/"));

        /* Create ZIP file System */
        try (FileSystem zipfs = FileSystems.newFileSystem(zip_disk, zip_properties)) {
            /* Get the Path inside ZIP File to delete the ZIP Entry */
            Path pathInZipfile = zipfs.getPath(folderName);
            System.out.println("deleting folder " + pathInZipfile.toUri());
            /* Execute Delete */
            //get all of the files in the directory
            deleteDir(pathInZipfile);
        }
    }

    public static void deleteDir(Path pathInZipfile) throws Exception {
        Stream<Path> blessedFolder = Files.list(pathInZipfile);
        blessedFolder.forEach(path -> {
            try {
                System.out.println("Removing file " + path.getFileName());
                Files.delete(path);
            } catch (DirectoryNotEmptyException e) {
                System.out.println("Found inner folder inside " + pathInZipfile + ". Removing");
                try {
                    deleteDir(path);
                } catch (Exception exception) {
                    System.out.println("Error occured in deleting inner folder " + path.getFileName());
                    exception.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Files.delete(pathInZipfile);
        System.out.println("Folder successfully deleted");
    }

}
