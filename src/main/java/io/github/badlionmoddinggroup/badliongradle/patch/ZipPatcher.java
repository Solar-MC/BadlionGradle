package io.github.badlionmoddinggroup.badliongradle.patch;

import com.nothome.delta.GDiffPatcher;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ZipPatcher {

    private static final Map<String, String> CREATE = new HashMap<>();

    static {
        CREATE.put("create", "true");
    }

    public static void patch(File in, File patches, File out) throws URISyntaxException {
        try{
            try (FileSystem inFileSystem = FileSystems.newFileSystem(in.toPath(), null);
                 FileSystem outFileSystem = FileSystems.newFileSystem(new URI("jar:" + out.toURI()), CREATE);
                 FileSystem patchFileSystem = FileSystems.newFileSystem(patches.toPath(), null)) {
                for (Path origFile : Files.walk(inFileSystem.getPath("/")).collect(Collectors.toSet())) {
                    if (!Files.isRegularFile(origFile))
                        continue;
                    Path patch = patchFileSystem.getPath(origFile.toString() + ".gdiff");
                    Path outFile = outFileSystem.getPath(origFile.toString());
                    Files.createDirectories(outFile.getParent());
                    if (Files.isRegularFile(patch)) {
                        Files.write(outFile, patch(Files.readAllBytes(origFile), Files.readAllBytes(patch)));
                    } else {
                        Files.copy(origFile, outFile);
                    }
                }
                for (Path patchFile : Files.walk(patchFileSystem.getPath("/")).collect(Collectors.toSet())) {
                    if (Files.isRegularFile(patchFile) && !patchFile.toString().endsWith(".gdiff")) {
                        Path outFile = outFileSystem.getPath(patchFile.toString());
                        Files.createDirectories(outFile.getParent());
                        Files.copy(patchFile, outFile);
                    }
                }
            }
        }catch (IOException e){
            System.out.println("Silencing ioException in patching");
        }
    }

    private static byte[] patch(byte[] src, byte[] patch) throws IOException {
        GDiffPatcher patcher = new GDiffPatcher();
        return patcher.patch(src, patch);
    }

}
