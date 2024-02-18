import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;


public class FileStatisticsCalculator {
    private final String directoryPath;
    private final boolean recursive;
    private final int maxDepth;
    private final int numThreads;
    private final Set<String> includeExtensions;
    private final Set<String> excludeExtensions;
    private final boolean gitIgnore;
    private final String outputFormat;

    public FileStatisticsCalculator(String directoryPath, boolean recursive, int maxDepth, int numThreads,
                                    Set<String> includeExtensions, Set<String> excludeExtensions,
                                    boolean gitIgnore, String outputFormat) {
        this.directoryPath = directoryPath;
        this.recursive = recursive;
        this.maxDepth = maxDepth;
        this.numThreads = numThreads;
        this.includeExtensions = includeExtensions;
        this.excludeExtensions = excludeExtensions;
        this.gitIgnore = gitIgnore;
        this.outputFormat = outputFormat;
    }

    public static void main(String[] args) {
        String directoryPath = "";
        boolean recursive = false;
        int maxDepth = Integer.MAX_VALUE;
        int numThreads = 1;
        Set<String> includeExtensions = new HashSet<>();
        Set<String> excludeExtensions = new HashSet<>();
        boolean gitIgnore = false;
        String outputFormat = "plain";


        for (String arg : args) {
            String[] parts = arg.split("=");
            String option = parts[0];
            String value = parts.length > 1 ? parts[1] : "";

            switch (option) {
                case "--path":
                    directoryPath = value;
                    break;
                case "--recursive":
                    recursive = true;
                    break;
                case "--max-depth":
                    maxDepth = Integer.parseInt(value);
                    break;
                case "--thread":
                    numThreads = Integer.parseInt(value);
                    break;
                case "--include-ext":
                    includeExtensions.addAll(Arrays.asList(value.split(",")));
                    break;
                case "--exclude-ext":
                    excludeExtensions.addAll(Arrays.asList(value.split(",")));
                    break;
                case "--git-ignore":
                    gitIgnore = true;
                    break;
                case "--output":
                    outputFormat = value;
                    break;
                default:
                    System.out.println("Unknown option: " + option);
                    break;
            }
        }


        FileStatisticsCalculator calculator = new FileStatisticsCalculator(directoryPath, recursive,
                maxDepth, numThreads, includeExtensions, excludeExtensions, gitIgnore, outputFormat);
        calculator.calculateStatistics();

    }

    public void calculateStatistics() {
        List<File> files = getFilesToProcess();
        List<Future<FileStatistics>> futures = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        for (File file : files) {
            Callable<FileStatistics> task = () -> calculateStatisticsForFile(file);
            futures.add(executorService.submit(task));
        }

        executorService.shutdown();

        List<FileStatistics> statisticsList = new ArrayList<>();

        for (Future<FileStatistics> future : futures) {
            try {
                statisticsList.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }


        switch (outputFormat) {
            case "xml":
                outputAsXML(statisticsList);
                break;
            case "json":
                outputAsJSON(statisticsList);
                break;
            default:
                outputAsPlainText(statisticsList);
        }
    }


    private List<File> getFilesToProcess() {
        List<File> files = new ArrayList<>();
        File directory = new File(directoryPath);

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory path.");
        }

        File[] allFiles = directory.listFiles();
        if (allFiles == null) {
            return files;
        }

        for (File file : allFiles) {
            if (file.isFile() && shouldProcessFile(file)) {
                files.add(file);
            } else if (recursive && file.isDirectory() && maxDepth > 0) {
                FileStatisticsCalculator subCalculator = new FileStatisticsCalculator(file.getAbsolutePath(),
                        true, maxDepth - 1, numThreads, includeExtensions, excludeExtensions, gitIgnore, outputFormat);
                files.addAll(subCalculator.getFilesToProcess());
            }
        }

        return files;
    }

    private boolean shouldProcessFile(File file) {
        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);

        if (!includeExtensions.isEmpty() && !includeExtensions.contains(fileExtension)) {
            return false;
        }

        if (excludeExtensions.contains(fileExtension)) {
            return false;
        }

        if (gitIgnore && isGitIgnored(file)) {
            return false;
        }

        return true;
    }

    private boolean isGitIgnored(File file) {
        Path gitIgnorePath = Paths.get(file.getParent(), ".gitignore");

        if (!Files.exists(gitIgnorePath) || !Files.isRegularFile(gitIgnorePath)) {
            return false;
        }

        try (BufferedReader reader = Files.newBufferedReader(gitIgnorePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }


                String regexPattern = line.trim().replace("*", ".*");
                if (file.getName().matches(regexPattern)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private FileStatistics calculateStatisticsForFile(File file) {
        int numLines = 0;
        int numNonEmptyLines = 0;
        int numCommentLines = 0;
        long fileSize = file.length();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isMultiLineComment = false;

            while ((line = reader.readLine()) != null) {
                numLines++;
                if (!line.trim().isEmpty()) {
                    numNonEmptyLines++;
                }

                if (!isMultiLineComment) {
                    if (line.trim().startsWith("//") || (!line.trim().startsWith("#!") && line.trim().startsWith("#"))) {
                        numCommentLines++;
                    } else if (line.trim().startsWith("/*")) {
                        numCommentLines++;
                        isMultiLineComment = true;
                    }
                } else {
                    numCommentLines++;
                    if (line.trim().endsWith("*/")) {
                        isMultiLineComment = false;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new FileStatistics(file.getName(), fileSize, numLines, numNonEmptyLines, numCommentLines);
    }




    private void outputAsXML(List<FileStatistics> statisticsList) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlBuilder.append("<fileStatistics>\n");
        xmlBuilder.append("<Total number of files:>").append(statisticsList.size()).append("</>\n");
        for (FileStatistics stats : statisticsList) {
            xmlBuilder.append("<file>\n");
            xmlBuilder.append("    <fileName>").append(stats.getFileName()).append("</fileName>\n");
            xmlBuilder.append("    <fileSize>").append(stats.getFileSize()).append("</fileSize>\n");
            xmlBuilder.append("    <numLines>").append(stats.getNumLines()).append("</numLines>\n");
            xmlBuilder.append("    <numNonEmptyLines>").append(stats.getNumNonEmptyLines()).append("</numNonEmptyLines>\n");
            xmlBuilder.append("    <numCommentLines>").append(stats.getNumCommentLines()).append("</numCommentLines>\n");
            xmlBuilder.append("</file>\n");
        }

        xmlBuilder.append("</fileStatistics>");

        try (FileWriter fileWriter = new FileWriter("file_statistics.xml")) {
            fileWriter.write(xmlBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void outputAsJSON(List<FileStatistics> statisticsList) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("number of files", statisticsList.size());
        jsonObject.put("files", statisticsList);
        String jsonOutput = jsonObject.toString(4); // Indentation for pretty printing

        try (FileWriter fileWriter = new FileWriter("file_statistics.json")) {
            fileWriter.write(jsonOutput);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void outputAsPlainText(List<FileStatistics> statisticsList) {
        System.out.println("Number of files: " + statisticsList.size());
        for (FileStatistics stats : statisticsList) {
            System.out.println(stats);
        }
    }
}