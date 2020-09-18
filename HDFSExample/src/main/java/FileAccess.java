import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class FileAccess
{

    private static FileSystem hdfs;
    /**
     * Initializes the class, using rootPath as "/" directory
     *
     * @param rootPath - the path to the root of HDFS,
     * for example, hdfs://localhost:32771
     */
    public FileAccess(String rootPath) throws URISyntaxException, IOException {
        Configuration configuration = new Configuration();
        configuration.set("dfs.client.use.datanode.hostname", "true");
        System.setProperty("HADOOP_USER_NAME", "root");

        hdfs = FileSystem.get(new URI(rootPath), configuration);
    }

    /**
     * Creates empty file or directory
     *
     * @param path
     */
    public void create(String path, boolean isDir) throws IOException {
        Path path1 = new Path(path);
        if(isDir){
            hdfs.mkdirs(path1);
        } else {
            hdfs.createNewFile(path1);
        }
    }

    /**
     * Appends content to the file
     *
     * @param path
     * @param content
     */
    public void append(String path, String content) throws IOException {
        if(hdfs.exists(new Path(path))) {
            FSDataOutputStream out = hdfs.append(new Path(path));
            out.writeUTF(content);
            out.close();
        }
    }

    /**
     * Returns content of the file
     *
     * @param path
     * @return
     */
    public String read(String path) throws IOException {
        if(hdfs.exists(new Path(path))) {
            FSDataInputStream fsDataOutputStream = hdfs.open(new Path(path));
            return fsDataOutputStream.readUTF();
        }
        return "File is doesn't exist!";
    }

    /**
     * Deletes file or directory
     *
     * @param path
     */
    public void delete(String path) throws IOException {
        if(hdfs.exists(new Path(path))) {
            hdfs.delete(new Path(path), true);
        }
    }

    /**
     * Checks, is the "path" is directory or file
     *
     * @param path
     * @return
     */
    public boolean isDirectory(String path) throws IOException {
        return hdfs.isDirectory(new Path(path));
    }

    /**
     * Return the list of files and subdirectories on any directory
     *
     * @param path
     * @return
     */
    public List<String> list(String path) throws IOException {
        Path dir = new Path(path);

        List<String> files = new ArrayList<>();

        if (!hdfs.exists(dir)) {
            System.out.printf("Directory '%s' doesn't exist.\n", dir.toString());
            return files;
        }

        FileStatus[] fileStatuses = hdfs.listStatus(dir);

        for (FileStatus fileStatus : fileStatuses) {
            if (fileStatus.isDirectory()) {
                String directory = fileStatus.getPath().toString();
                files.add(directory);
                files.addAll(list(directory));
            } else {
                files.add(fileStatus.getPath().toString());
            }
        }

        return files;
    }


    public static FileSystem getHdfs() {
        return hdfs;
    }

    public static void setHdfs(FileSystem hdfs) {
        FileAccess.hdfs = hdfs;
    }
}
